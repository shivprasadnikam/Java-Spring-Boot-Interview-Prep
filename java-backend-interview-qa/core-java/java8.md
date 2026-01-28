# Java 8+ — Interview Q&A

## Q1. What real problem do lambdas solve in backend code?

Answer:
- Reduce boilerplate and make behavior passing simpler.
- Example: filtering collections or customizing repository methods.
- Use lambdas for small, functional behaviors.
- Avoid long lambdas that hide complex logic.
- Follow-up: When should you use a method reference?

## Q2. Streams vs loops: when do you use each?

Answer:
- Streams are expressive for pipelines; loops are explicit and faster in hot paths.
- Use streams for readable transformations and aggregations.
- Avoid streams when debugging or micro-optimizing critical code.
- Follow-up: Whats the cost of boxing in streams?

## Q3. What is a functional interface and why does it matter?

Answer:
- An interface with a single abstract method; enables lambdas.
- Example: `Predicate`, `Supplier`, `Function`.
- Use custom functional interfaces only when existing ones dont fit.
- Avoid creating too many similar interfaces.
- Follow-up: What does `@FunctionalInterface` enforce?

## Q4. What is `Optional` good for and where is it misused?

Answer:
- Use it as a return type to model absence explicitly.
- Avoid using `Optional` in fields or method parameters (adds noise).
- Do not call `get()` without `isPresent` or `orElse`.
- Follow-up: When should you use `orElseGet` vs `orElse`?

## Q5. `default` methods in interfaces: why were they added?

Answer:
- Allows interface evolution without breaking implementations.
- Example: adding a new method to a public API.
- Use default methods for backward compatibility, not core logic.
- Avoid heavy logic in default methods (hard to override safely).
- Follow-up: How do you resolve conflicts between two defaults?

## Q6. `CompletableFuture` vs Future: whats the benefit?

Answer:
- CompletableFuture supports chaining and composition without blocking.
- Future is blocking and limited.
- Use CompletableFuture for async orchestration.
- Avoid deep callback chains; keep pipelines readable.
- Follow-up: How do you handle exceptions in CompletableFuture?

## Q7. What is the difference between `map` and `flatMap` in streams?

Answer:
- `map` transforms elements; `flatMap` flattens nested structures.
- Use `flatMap` when one element produces multiple outputs.
- Avoid `flatMap` if a simple `map` is sufficient.
- Follow-up: How does `flatMap` relate to Optional?

## Q8. Why should you be careful with parallel streams?

Answer:
- They use the common ForkJoinPool, which can starve other tasks.
- Use parallel streams for CPU-bound, large datasets.
- Avoid for I/O-bound tasks or small collections.
- Follow-up: How do you provide a custom ForkJoinPool?

## Q9. What is `Collector` and why use custom collectors?

Answer:
- Collectors transform a stream into a container (list, map, summary).
- Use custom collectors for complex aggregations.
- Avoid custom collectors when a built-in one exists.
- Follow-up: How does `groupingBy` work with downstream collectors?

## Q10. Date/Time API (`java.time`): what should you know?

Answer:
- Immutable, thread-safe replacement for `Date` and `Calendar`.
- Use `Instant` for timestamps, `ZonedDateTime` for user timezones.
- Avoid storing timezone-less times for user-facing events.
- Follow-up: Why is `LocalDateTime` risky for APIs?

## Q11. Method references: when are they clearer?

Answer:
- Use when it improves readability: `User::getId`.
- Avoid method references if it hides context or becomes cryptic.
- Follow-up: What are the four kinds of method references?

## Q12. What are the risks of using `Optional` in JSON serialization?

Answer:
- Some serializers produce unexpected JSON or ignore Optional fields.
- Use `Optional` in service layer, not in API DTOs.
- Avoid Optional in entity fields; JPA doesnt handle it well.
- Follow-up: How do you map Optional to API responses safely?
