# feature-flags

Lightweight feature flag library for Java. Supports Spring Boot and plain Java apps with a shared interface, making it easy to swap to a full feature flag platform (e.g. Unleash) later.

## Modules

| Module | Use case |
|---|---|
| `feature-flags-core` | Plain Java 8+, no Spring dependency |
| `feature-flags-spring` | Spring Boot — reads from `application.properties` via Spring `Environment` |

## Interface

```java
public interface FeatureFlags {
    boolean isEnabled(String toggleName);
    boolean isEnabled(String toggleName, boolean defaultValue);
}
```

## Spring Boot

Add the dependency:

```xml
<dependency>
    <groupId>com.guranxp</groupId>
    <artifactId>feature-flags-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

A `FeatureFlags` bean is registered automatically via Spring Boot autoconfiguration. Inject it where needed:

```java
@Service
public class RegistrationService {

    private final FeatureFlags featureFlags;

    public void register() {
        if (!featureFlags.isEnabled("feature.registration")) {
            throw new FeatureDisabledException();
        }
        // ...
    }
}
```

Enable a flag in `application.properties`:

```properties
feature.registration=true
```

Flags not defined in properties default to `false`, or to the value passed at the call site:

```java
featureFlags.isEnabled("feature.registration", false)
```

### Priority (highest first)

1. System properties (`-Dfeature.registration=true`)
2. Environment variables (`FEATURE_REGISTRATION=true`)
3. `application.properties` / profile-specific files
4. Call-site default

### Consistent with `@ConditionalOnProperty`

Both `FeatureFlags` and `@ConditionalOnProperty` read from Spring's `Environment`, so they always agree:

```java
@Bean
@ConditionalOnProperty("feature.registration")
public RegistrationController registrationController() { ... }
```

## Plain Java (non-Spring)

Add the dependency:

```xml
<dependency>
    <groupId>com.guranxp</groupId>
    <artifactId>feature-flags-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

Instantiate `InMemoryFeatureFlags` directly:

```java
FeatureFlags flags = new InMemoryFeatureFlags(Collections.emptyMap());
flags.isEnabled("feature.registration", false);
```

### Priority (highest first)

1. System properties (`-Dfeature.registration=true`) — checked live at each call
2. `feature-flags.properties` on the classpath — loaded at startup
3. Code defaults passed to the constructor
4. Call-site default

### Custom properties file

```java
FeatureFlags flags = new InMemoryFeatureFlags(Collections.emptyMap(), "my-flags.properties");
```

### Code defaults

```java
Map<String, Boolean> defaults = new HashMap<>();
defaults.put("feature.registration", false);
defaults.put("feature.admin", false);

FeatureFlags flags = new InMemoryFeatureFlags(defaults);
```

## Feature enum

For larger projects, instead of passing flag key strings around, define a project-specific enum that centralises all flags. Each constant declares its key, default value, creation date, and type:

```java
enum Feature {

    NEW_DASHBOARD("feature.new-dashboard", false, LocalDate.of(2026, 3, 20)),
    PAYMENT_PROVIDER("feature.payment-provider", true, LocalDate.of(2026, 3, 20), FlagType.OPERATIONAL),
    ONBOARDING_V2("feature.onboarding-v2", false, LocalDate.of(2026, 3, 20), FlagType.EXPERIMENT),
    BETA_REPORTS("feature.beta-reports", false, LocalDate.of(2026, 3, 20), FlagType.PERMISSION);

    private static FeatureFlags featureFlags;

    public static void configure(final FeatureFlags flags) {
        featureFlags = flags;
    }

    private final String key;
    private final boolean defaultValue;
    private final LocalDate createdAt;
    private final FlagType type;

    Feature(final String key, final boolean defaultValue, final LocalDate createdAt) {
        this(key, defaultValue, createdAt, FlagType.RELEASE);
    }

    Feature(final String key, final boolean defaultValue, final LocalDate createdAt, final FlagType type) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.createdAt = createdAt;
        this.type = type;
    }

    public boolean isEnabled() {
        return featureFlags.isEnabled(key, defaultValue);
    }

    public String key() { return key; }
    public boolean defaultValue() { return defaultValue; }
    public LocalDate createdAt() { return createdAt; }
    public FlagType type() { return type; }
}
```

Configure once at startup, then call `isEnabled()` anywhere without passing a `FeatureFlags` instance:

```java
// Once at startup:
Feature.configure(new InMemoryFeatureFlags(Collections.emptyMap()));

// Anywhere in the codebase:
if (Feature.NEW_DASHBOARD.isEnabled()) {
    showNewDashboard();
}
```

The `createdAt` field enables an expiry test that fails CI when a short-lived flag has been around too long, nudging the team to clean it up:

```java
@Test
void shouldNotHaveExpiredReleaseFlags() {
    LocalDate cutoff = LocalDate.now().minusDays(90);
    for (Feature f : Feature.values()) {
        if (f.type() == FlagType.RELEASE || f.type() == FlagType.EXPERIMENT) {
            assertThat(f.createdAt())
                .as("Flag %s is older than 90 days — time to clean it up", f)
                .isAfter(cutoff);
        }
    }
}
```

A full example can be found in [`feature-flags-core/src/test/java/.../ExampleFeature.java`](feature-flags-core/src/test/java/com/guranxp/featureflags/ExampleFeature.java).

## Migrating to Unleash

When you are ready to move to a full feature flag platform, implement the `FeatureFlags` interface against Unleash and swap the bean — no other code changes needed:

```java
// Spring Boot
@Bean
public FeatureFlags featureFlags(final Unleash unleash) {
    return unleash::isEnabled;
}

// Plain Java
FeatureFlags flags = unleash::isEnabled;
```
