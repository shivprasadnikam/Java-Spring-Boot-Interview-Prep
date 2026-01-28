# Spring Boot Basics — Interview Q&A

## Q1. Spring vs Spring Boot: what problem does Boot actually solve?

Answer:
- Boot removes boilerplate configuration and provides sane defaults.
- It standardizes dependency sets via starters and auto-configuration.
- Use Boot for rapid setup and consistent conventions across services.
- Avoid Boot only when you need extreme framework customization.
- Follow-up: What is Spring Boots auto-configuration mechanism?

## Q2. What is `@SpringBootApplication` and why is it preferred?

Answer:
- It bundles `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- Reduces annotation clutter and makes the app entry obvious.
- Use it for standard Boot apps; place it at the root package.
- Avoid placing it in sub-packages to prevent missing beans.
- Follow-up: How does component scanning work with this annotation?

## Q3. What are Spring Boot starters and why are they important?

Answer:
- Starters are curated dependency bundles (e.g., `spring-boot-starter-web`).
- They avoid version conflicts and align transitive dependencies.
- Use starters to keep `pom.xml` clean and consistent.
- Avoid mixing multiple competing starters (e.g., Tomcat + Jetty) unless needed.
- Follow-up: How do you override a transitive dependency version?

## Q4. How does auto-configuration decide which beans to create?

Answer:
- It uses `@Conditional` annotations (classpath, bean presence, property checks).
- Example: `DataSourceAutoConfiguration` triggers only if JDBC is on classpath.
- Use auto-config to reduce manual wiring; override when defaults dont fit.
- Avoid silent auto-config surprises by checking conditions report.
- Follow-up: How do you debug auto-configuration?

## Q5. Externalized configuration: whats the real precedence order?

Answer:
- Highest: command-line args, env vars, then `application-*.yml`, then defaults.
- Use env vars for secrets; keep defaults in config files.
- Avoid committing secrets in `application.yml`.
- Follow-up: How do you map properties to POJOs?

## Q6. Why does Boot use an embedded server and when is that good?

Answer:
- Embedded servers make apps self-contained and easy to deploy.
- Great for containers and CI/CD pipelines.
- Avoid embedded servers if you need a shared app server model (rare today).
- Follow-up: How do you switch between Tomcat and Jetty?

## Q7. What is Actuator and how do you use it in production?

Answer:
- Provides health, metrics, env, and custom endpoints.
- Use it for readiness/liveness and operational visibility.
- Avoid exposing sensitive endpoints publicly; lock down with security.
- Follow-up: How do you enable only specific actuator endpoints?

## Q8. Profiles: when do you use them and when do you avoid them?

Answer:
- Profiles separate environment-specific beans and configs.
- Use for dev/test/prod differences (DBs, mock clients).
- Avoid excessive profiles that hide production parity issues.
- Follow-up: How do you activate multiple profiles?

## Q9. Bean overriding: whats the safe way to customize Boot defaults?

Answer:
- Define your own bean of the same type to override Boots default.
- Use `@Primary` or `@ConditionalOnMissingBean` to control precedence.
- Avoid accidental overrides that change behavior silently.
- Follow-up: What happens if two beans of same type exist and none is primary?

## Q10. Packaging: fat JAR vs WAR  whats the current best practice?

Answer:
- Fat JAR (bootable JAR) is the standard for modern deployments.
- Use WAR only when deploying to legacy app servers.
- Avoid WAR unless the platform requires it.
- Follow-up: How do you run a Boot JAR in Docker?

## Q11. Logging defaults: what should you know before production?

Answer:
- Boot uses Logback by default with sensible levels.
- Use structured logging for better search in centralized systems.
- Avoid logging full payloads or PII.
- Follow-up: How do you change logging levels at runtime?

## Q12. Boot testing basics: what do interviewers expect?

Answer:
- Use slice tests (`@WebMvcTest`, `@DataJpaTest`) for fast feedback.
- Use `@SpringBootTest` for full context but keep them few and focused.
- Avoid loading full context for simple unit tests.
- Follow-up: How do you mock beans in a Spring Boot test?
