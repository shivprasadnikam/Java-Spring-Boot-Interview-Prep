# Spring Boot Profiles & Properties — Interview Q&A

## Q1. What problem do profiles solve in real teams?

Answer:
- Separate environment-specific configuration and beans.
- Keeps dev/test/prod differences explicit.
- Use profiles for infra differences (DB, cache, mock services).
- Avoid using profiles to hide business logic changes.
- Follow-up: How do you activate multiple profiles?

## Q2. What is the property precedence order in Boot?

Answer:
- Command-line args > env vars > application-*.yml > defaults.
- Use env vars for secrets and runtime overrides.
- Avoid storing secrets in versioned config files.
- Follow-up: How do you override a property for a single test?

## Q3. `application.yml` vs `application.properties`: any difference?

Answer:
- Same capabilities; YAML is hierarchical and more readable.
- Use YAML for complex nested config.
- Avoid mixing both in one project (confusing precedence).
- Follow-up: How do you bind YAML to POJOs?

## Q4. Why use `@ConfigurationProperties` instead of `@Value`?

Answer:
- Type-safe binding, validation, and grouping.
- Better for large config sets and reusability.
- Use `@Validated` to catch invalid configs at startup.
- Avoid `@Value` for many related properties.
- Follow-up: How do you enable configuration properties scanning?

## Q5. How do you handle secrets in Spring Boot?

Answer:
- Use env vars, vault, or secret managers (AWS Secrets Manager).
- Avoid hardcoding secrets or committing them.
- Use `spring.config.import` to load external secrets.
- Follow-up: How do you rotate secrets safely?

## Q6. What is `@Profile` on beans vs profile-specific properties?

Answer:
- `@Profile` controls bean registration.
- Profile-specific properties override values by profile.
- Use both for clean separation of infrastructure.
- Avoid using profiles for feature flags (use toggles instead).
- Follow-up: How do you set a default profile?

## Q7. How do you debug property binding issues?

Answer:
- Enable `--debug` or check `/env` actuator endpoint.
- Use `@ConfigurationProperties` with validation to fail fast.
- Avoid silent fallbacks; they hide misconfiguration.
- Follow-up: How do you handle missing properties gracefully?

## Q8. What are common pitfalls with profiles in microservices?

Answer:
- Inconsistent profiles across services causing config drift.
- Too many profiles and poor naming conventions.
- Use a shared config strategy or config server.
- Avoid profile sprawl without ownership.
- Follow-up: How do you manage profiles across multiple environments?

## Q9. What is `spring.config.import` and why use it?

Answer:
- Imports external config files or config server data.
- Use for centralized config management.
- Avoid large monolithic config files per service.
- Follow-up: How does Spring Cloud Config integrate?

## Q10. How do you manage configuration for local development?

Answer:
- Use a local profile with mock integrations.
- Keep local config minimal and safe.
- Avoid using prod-like secrets locally.
- Follow-up: How do you ensure local config does not leak into prod?

## Q11. When should you use system properties vs env vars?

Answer:
- Use env vars for deployment overrides.
- Use system properties for JVM-level flags or testing.
- Avoid mixing for the same setting (hard to debug).
- Follow-up: Which wins if both are set?

## Q12. How do you handle dynamic configuration changes?

Answer:
- Use Spring Cloud Config or dynamic property refresh.
- Use feature toggles for runtime changes.
- Avoid changing critical configs without rollout controls.
- Follow-up: What is the risk of refreshing beans in production?
