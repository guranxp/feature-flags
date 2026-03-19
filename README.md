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
