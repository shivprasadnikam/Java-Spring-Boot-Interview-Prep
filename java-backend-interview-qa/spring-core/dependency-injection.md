# Spring Core Dependency Injection — Interview Q&A

## Q1. Why is constructor injection preferred over field injection?

Answer:
- Makes dependencies explicit and final, improving immutability.
- Easier unit testing (no reflection or Spring context required).
- Fails fast if required beans are missing.
- Avoid field injection in production code unless you have a strong reason.
- Follow-up: How does constructor injection help detect circular dependencies?

## Q2. What is the difference between dependency injection and dependency lookup?

Answer:
- DI pushes dependencies into a class; lookup pulls them manually.
- DI is cleaner and keeps code decoupled from the container.
- Lookup is rarely needed (like in legacy code or dynamic factory logic).
- Avoid manual lookups (`ApplicationContext.getBean`) in business logic.
- Follow-up: When is lookup justified in Spring?

## Q3. How does Spring decide which bean to inject when multiple candidates exist?

Answer:
- Uses `@Primary`, then `@Qualifier`, then bean name matching.
- Use `@Qualifier` for explicit selection in complex systems.
- Avoid ambiguous wiring; it breaks at startup or behaves unpredictably.
- Follow-up: What happens if two beans are primary?

## Q4. What is `@Autowired` actually doing under the hood?

Answer:
- It triggers dependency resolution by type and qualifier.
- Its processed by `AutowiredAnnotationBeanPostProcessor`.
- Use `@Autowired` on constructors, setters, or fields (constructor preferred).
- Avoid optional injections unless the dependency is truly optional.
- Follow-up: How do you handle optional beans safely?

## Q5. What is the difference between `@Component`, `@Service`, and `@Repository`?

Answer:
- All are stereotype annotations; `@Service` and `@Repository` are semantic.
- `@Repository` adds exception translation for persistence exceptions.
- Use them for readability and tooling support.
- Avoid using `@Component` everywhere; use the more specific stereotype.
- Follow-up: How does exception translation work?

## Q6. How do `@Bean` methods differ from `@Component` classes?

Answer:
- `@Bean` methods are explicit bean factory methods in `@Configuration`.
- `@Component` classes are discovered via classpath scanning.
- Use `@Bean` for third-party classes or complex construction logic.
- Avoid overusing `@Bean` if simple component scanning suffices.
- Follow-up: What is the role of `@Configuration(proxyBeanMethods=true)`?

## Q7. What are common DI pitfalls in Spring Boot services?

Answer:
- Circular dependencies (usually a design smell).
- Over-injecting dependencies into one service (God object).
- Using field injection that hides dependencies.
- Avoid injecting repositories into controllers directly for non-trivial logic.
- Follow-up: How do you break circular dependencies cleanly?

## Q8. What is `@Lazy` injection and when is it useful?

Answer:
- Defers bean initialization until first use.
- Useful for heavy beans or optional features.
- Avoid `@Lazy` as a band-aid for circular dependencies.
- Follow-up: How does `@Lazy` affect proxy behavior?

## Q9. What is the difference between `BeanFactory` and `ApplicationContext`?

Answer:
- `BeanFactory` is minimal; `ApplicationContext` adds enterprise features.
- Context supports events, AOP, i18n, and more.
- Use `ApplicationContext` for most Spring Boot apps.
- Avoid using `BeanFactory` unless you want minimal overhead.
- Follow-up: Does `ApplicationContext` eagerly initialize beans?

## Q10. Why is dependency injection important for testability?

Answer:
- Allows mocking of dependencies and isolation of units.
- Enables swapping real components with fakes in tests.
- Use constructor injection for straightforward mocking.
- Avoid static dependencies; they are hard to replace.
- Follow-up: How do you mock beans in a Spring Boot test?

## Q11. How does DI interact with AOP proxies?

Answer:
- Spring often injects a proxy rather than the real bean.
- This enables transactions, security, and monitoring.
- Be aware of self-invocation issues (proxy bypass).
- Avoid calling proxied methods internally within the same class.
- Follow-up: How do you handle self-invocation for transactional methods?

## Q12. How do you handle configuration-driven implementations cleanly?

Answer:
- Use `@ConditionalOnProperty` or `@Profile` to select beans.
- Example: switch between in-memory and Redis cache clients.
- Avoid if/else logic in service constructors.
- Follow-up: When would you prefer `@ConditionalOnClass`?
