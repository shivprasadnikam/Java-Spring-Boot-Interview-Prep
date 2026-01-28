# Spring Core Annotations — Interview Q&A

## Q1. What is the difference between `@Component`, `@Service`, and `@Repository`?

Answer:
- All are stereotypes; `@Service` and `@Repository` add semantics.
- `@Repository` enables exception translation for persistence layers.
- Use specific stereotypes for clarity.
- Avoid using only `@Component` everywhere.
- Follow-up: How does exception translation actually work?

## Q2. What does `@Configuration` do beyond `@Component`?

Answer:
- Marks class as source of bean definitions.
- Enables proxying of `@Bean` methods to enforce singleton behavior.
- Use `@Configuration` for factory-style bean setup.
- Avoid using `@Component` for complex bean factories.
- Follow-up: What changes if `proxyBeanMethods=false`?

## Q3. When do you use `@Bean` instead of `@Component`?

Answer:
- Use `@Bean` for third-party or complex construction logic.
- `@Component` is for your own classes with simple instantiation.
- Avoid writing `@Bean` for classes already annotated with stereotypes.
- Follow-up: How does `@Bean` affect scope and lifecycle?

## Q4. What is `@Primary` and when should you avoid it?

Answer:
- Marks a default bean when multiple candidates exist.
- Use for common implementation with occasional overrides.
- Avoid if it hides ambiguity in a complex module (use `@Qualifier`).
- Follow-up: Can you set multiple `@Primary` beans?

## Q5. What is `@Qualifier` used for and what is the common pitfall?

Answer:
- Resolves ambiguity by bean name or qualifier value.
- Use when you have multiple implementations.
- Pitfall: mismatch between qualifier value and bean name.
- Follow-up: How do `@Qualifier` and `@Primary` interact?

## Q6. What does `@Autowired(required=false)` do?

Answer:
- Makes injection optional; bean may be null.
- Use for optional integrations or feature toggles.
- Avoid for required dependencies (it hides startup errors).
- Follow-up: What is a cleaner alternative to optional autowiring?

## Q7. What is `@Value` and how can it go wrong?

Answer:
- Injects property values into fields/params.
- Use for config values and flags.
- Pitfalls: type conversion errors, missing properties.
- Follow-up: Why is `@ConfigurationProperties` preferred for complex configs?

## Q8. How does `@Profile` help in real systems?

Answer:
- Enables beans only for specific environments.
- Use to swap infra dependencies (mock vs real).
- Avoid overusing profiles to hide environment drift.
- Follow-up: Can you enable multiple profiles simultaneously?

## Q9. What is `@Lazy` and whats the trap?

Answer:
- Defers bean creation until first use.
- Use to reduce startup time for optional beans.
- Trap: hidden initialization errors appear at runtime.
- Follow-up: How does `@Lazy` affect proxying?

## Q10. What is `@Order` used for?

Answer:
- Controls ordering of beans in collections or filter chains.
- Use in filters, interceptors, or strategy lists.
- Avoid relying on default ordering; its not deterministic.
- Follow-up: How does `@Order` interact with `Ordered` interface?

## Q11. What are `@Conditional` annotations and why should you care?

Answer:
- Controls bean registration based on classpath or properties.
- Use for modular features or optional integrations.
- Avoid hardcoding conditional logic in constructors.
- Follow-up: How do you write a custom condition?

## Q12. Whats the difference between `@RestController` and `@Controller`?

Answer:
- `@RestController` = `@Controller` + `@ResponseBody`.
- Use for JSON APIs; `@Controller` for HTML views.
- Avoid mixing view-based and REST patterns in same controller.
- Follow-up: How does `@ResponseBody` affect serialization?
