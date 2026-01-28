# Spring Bean Lifecycle — Interview Q&A

## Q1. What are the major phases of a Spring bean lifecycle?

Answer:
- Instantiation -> dependency injection -> initialization -> usage -> destruction.
- Hooks: `@PostConstruct`, `InitializingBean`, `@PreDestroy`.
- Use lifecycle hooks for initializing resources or cleanup.
- Avoid heavy logic in constructors; prefer `@PostConstruct`.
- Follow-up: When are `BeanPostProcessor`s invoked?

## Q2. How do `@PostConstruct` and `InitializingBean` differ?

Answer:
- `@PostConstruct` is annotation-based and JSR-250 standard.
- `InitializingBean` is Spring-specific interface.
- Prefer `@PostConstruct` for portability.
- Avoid mixing both without clear need (double init).
- Follow-up: When would you avoid `@PostConstruct`?

## Q3. What does `BeanPostProcessor` do?

Answer:
- It allows modification of bean instances before/after initialization.
- Used internally for AOP, annotation processing, and proxies.
- Use custom BPP for cross-cutting infrastructure (rare in app code).
- Avoid heavy operations in BPPs (startup slowdown).
- Follow-up: Whats the difference between BPP and BFPP?

## Q4. What is `BeanFactoryPostProcessor` and when is it useful?

Answer:
- It modifies bean definitions before beans are instantiated.
- Used for property overrides or dynamic bean registration.
- Useful in frameworks, not typical business code.
- Avoid if a simple configuration change suffices.
- Follow-up: How does `PropertySourcesPlaceholderConfigurer` work?

## Q5. How do you control bean initialization order?

Answer:
- Use `@DependsOn` for explicit ordering.
- Use `@Order` for ordered lists of beans.
- Avoid relying on component scan order (non-deterministic).
- Follow-up: What are the risks of too many `@DependsOn`?

## Q6. How does prototype scope affect lifecycle callbacks?

Answer:
- Spring creates a new instance every request.
- Destruction callbacks are not managed by container.
- Use prototype for short-lived, stateful objects.
- Avoid prototype for beans that need cleanup.
- Follow-up: How do you manually destroy prototype beans?

## Q7. What is the difference between singleton and prototype scopes?

Answer:
- Singleton: one instance per container (default).
- Prototype: new instance on each request.
- Use singleton for stateless services; prototype for stateful helpers.
- Avoid storing user-specific state in singleton beans.
- Follow-up: What about request/session scopes in web apps?

## Q8. What happens if a bean fails during initialization?

Answer:
- Context startup fails and the app wont start.
- Use `@Conditional` or lazy init to avoid non-critical failures.
- Avoid swallowing init exceptions; fail fast.
- Follow-up: How do you handle optional dependencies at startup?

## Q9. What is `SmartLifecycle` and where is it used?

Answer:
- Allows beans to start/stop in phases (e.g., message listeners).
- Useful for controlling order of infrastructure components.
- Avoid using it for simple beans (overkill).
- Follow-up: How do phases affect startup/shutdown?

## Q10. How does Spring manage destruction callbacks?

Answer:
- Calls `@PreDestroy` or `DisposableBean` on shutdown.
- Use for closing connections and stopping threads.
- Avoid leaving resources open; it causes leaks.
- Follow-up: How does it behave in containerized shutdowns?

## Q11. Why can `@PostConstruct` fail with proxied beans?

Answer:
- Some proxies delay actual target initialization.
- Self-invocation or early bean references can bypass proxies.
- Avoid relying on proxies inside `@PostConstruct` for transactional logic.
- Follow-up: How do you ensure proxy-aware initialization?

## Q12. What is `@Lazy` and how does it affect lifecycle?

Answer:
- Bean is created on first use instead of startup.
- Useful for expensive or optional components.
- Avoid using it to mask slow startup problems.
- Follow-up: How does `@Lazy` interact with circular dependencies?
