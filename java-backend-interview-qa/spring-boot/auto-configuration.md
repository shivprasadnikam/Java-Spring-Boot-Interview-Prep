# Spring Boot Auto-Configuration — Interview Q&A

## Q1. What is auto-configuration in Spring Boot?

Answer:
- Boot automatically configures beans based on classpath, properties, and existing beans.
- It reduces manual configuration and enforces conventions.
- Use it to speed up service setup and standardize infra.
- Avoid relying on defaults you do not understand; inspect conditions.
- Follow-up: How do you view the auto-config report?

## Q2. How does `@EnableAutoConfiguration` work internally?

Answer:
- It imports auto-config classes listed in `META-INF/spring.factories`.
- Uses conditional annotations to decide what to load.
- Use it indirectly through `@SpringBootApplication`.
- Avoid disabling it unless you own full config.
- Follow-up: What replaced `spring.factories` in newer Boot versions?

## Q3. What are `@ConditionalOnClass` and `@ConditionalOnMissingBean`?

Answer:
- `OnClass`: config activates when a class is on classpath.
- `OnMissingBean`: config activates only if you did not define a bean.
- Use to override defaults safely.
- Avoid removing auto-config without providing your own beans.
- Follow-up: What happens if you define multiple beans of same type?

## Q4. How do you disable a specific auto-configuration?

Answer:
- Use `@SpringBootApplication(exclude=...)` or `spring.autoconfigure.exclude`.
- Use when defaults conflict with custom infra.
- Avoid blanket exclusions; it may break other configs.
- Follow-up: How do you debug which auto-config is active?

## Q5. What is the difference between starters and auto-configuration?

Answer:
- Starters provide dependencies; auto-config provides beans/config.
- Both work together: starters add classes, auto-config reacts.
- Use starters to ensure compatible dependency sets.
- Avoid mixing conflicting starters (e.g., reactive + MVC) without intent.
- Follow-up: How does Boot choose between WebFlux and MVC?

## Q6. What is `spring.main.allow-bean-definition-overriding` for?

Answer:
- Allows your custom bean to override a default bean definition.
- Use for controlled overrides in legacy modules.
- Avoid enabling globally unless you trust all modules.
- Follow-up: What errors occur without this flag?

## Q7. How do you create your own auto-configuration?

Answer:
- Create a config class with conditional annotations.
- Register it in `META-INF/spring.factories` (or `AutoConfiguration.imports`).
- Use for shared internal libraries.
- Avoid adding heavy auto-config for app-specific logic.
- Follow-up: How do you test your auto-configuration?

## Q8. What are common auto-configuration pitfalls in production?

Answer:
- Unexpected beans enabled by classpath dependencies.
- Wrong default settings (timeouts, pool sizes).
- Use explicit properties to pin values.
- Avoid adding dependencies without checking auto-config impact.
- Follow-up: How do you prevent actuator endpoints from auto-exposing?

## Q9. How does Boot configure DataSource automatically?

Answer:
- Detects JDBC driver and uses properties to build DataSource.
- Defaults to HikariCP if available.
- Use explicit pool settings for production stability.
- Avoid using in-memory DB defaults in non-test environments.
- Follow-up: How do you customize Hikari properties?

## Q10. What is `@AutoConfigureBefore` and `@AutoConfigureAfter`?

Answer:
- Controls ordering between auto-config classes.
- Use in custom auto-config libraries to ensure correct setup order.
- Avoid tight ordering dependencies unless necessary.
- Follow-up: What happens if ordering is incorrect?

## Q11. How do you inspect which beans came from auto-config?

Answer:
- Use actuator `/conditions` or `/beans` endpoints.
- Enable `debug` to get auto-config report in logs.
- Avoid debugging blind in production; use structured logs.
- Follow-up: How do you enable the conditions report securely?

## Q12. When should you turn off auto-configuration entirely?

Answer:
- Only when building a non-Boot Spring app or fully custom stack.
- Use in rare cases like deeply customized infra frameworks.
- Avoid disabling it for normal services; it adds maintenance cost.
- Follow-up: What is the minimum set of configs you must then provide?
