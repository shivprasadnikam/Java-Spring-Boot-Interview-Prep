# Core Java OOPs — Interview Q&A

## Q1. Composition vs inheritance: which one do you prefer in backend services?

Answer:
- Prefer **composition** for business logic to reduce coupling and ease testing.
- Inheritance is best reserved for stable framework abstractions or template methods.
- Example: `OrderService` has a `PaymentClient` (composition), not extends it.
- Use composition when you expect multiple implementations or frequent changes.
- Avoid inheritance when base-class changes can ripple to many services.
- Follow-up: How does this relate to LSP and the fragile base class problem?

## Q2. How does polymorphism show up in Spring Boot applications?

Answer:
- DI enables runtime polymorphism: interface + multiple implementations.
- Example: `NotificationSender` with `EmailSender` and `SmsSender` beans.
- Use when behavior varies by config, region, or tenant.
- Avoid creating interfaces for trivial CRUD logic (unnecessary abstraction).
- Follow-up: What happens when multiple beans match one interface?

## Q3. Abstract class vs interface (Java 8+): practical choice?

Answer:
- Interface for contracts and multiple capabilities; abstract class for shared state.
- Example: repository interfaces with default methods vs abstract base services.
- Use abstract class when you control the inheritance tree and need shared logic.
- Avoid abstract classes for business rules that should be configurable.
- Follow-up: How do default methods change interface design?

## Q4. Encapsulation in production services: whats the real benefit?

Answer:
- Hides internal state, protects invariants, and keeps APIs stable.
- Example: expose DTOs instead of entities from controllers.
- Use private fields + constructors to enforce valid object creation.
- Avoid leaking mutable internal collections; return unmodifiable views.
- Follow-up: How do you enforce invariants on JPA entities?

## Q5. Abstraction in a Spring Boot context: where do you actually use it?

Answer:
- Repository interfaces abstract persistence; services abstract business workflows.
- Example: `UserRepository` hides SQL, service hides orchestration details.
- Use abstraction to swap implementations (DB or cache) without code changes.
- Avoid abstractions that only mirror a single implementation (YAGNI).
- Follow-up: Where do you draw the line between service and repository logic?

## Q6. Overloading vs overriding: what interviewers test here?

Answer:
- Overloading: same method name, different params (compile-time).
- Overriding: same signature, different implementation (runtime).
- Example: overloaded `find` methods; overridden `save` in custom repository.
- Avoid overloading when it creates ambiguous or confusing APIs.
- Follow-up: Can you overload by return type only? Why not?

## Q7. What is the Liskov Substitution Principle (LSP) in real code?

Answer:
- Subtypes should be usable wherever base types are expected.
- Example: a `ReadonlyUserRepository` shouldnt throw on `save()` if it extends a base repository.
- Use LSP to validate inheritance and interface contracts.
- Avoid inheritance when subclasses change behavior semantics.
- Follow-up: Give a concrete LSP violation from your projects.

## Q8. Equality in entities: how do you implement `equals`/`hashCode` safely?

Answer:
- Use business keys (stable, unique) or immutable IDs after persistence.
- Avoid using mutable fields (causes cache/collection bugs).
- Example: use `email` or a generated `id` after its assigned.
- Avoid `equals` on full entity graphs (performance + recursion problems).
- Follow-up: What about Hibernate proxies and `equals`?

## Q9. Immutability: when does it matter in backend systems?

Answer:
- Immutable objects are thread-safe and easier to reason about.
- Example: value objects like `Money` or `Address`.
- Use immutability for shared config, events, and DTOs.
- Avoid immutability for large aggregates that change frequently (too much copying).
- Follow-up: How do you combine immutability with JPA entities?

## Q10. Association vs aggregation vs composition: why should you care?

Answer:
- Association: loose reference (service uses repository).
- Aggregation: part-of but independent lifecycle (Order has Items).
- Composition: strict lifecycle dependency (Invoice has Lines).
- Use composition when child should not exist without parent.
- Avoid composition when reuse or independent lifecycle is needed.
- Follow-up: How does this affect cascading deletes in JPA?

## Q11. Dependency Inversion Principle (DIP): how does Spring help?

Answer:
- High-level modules depend on abstractions, not concrete classes.
- Spring DI wires implementations without changing business code.
- Use interfaces for external integrations (payment, notifications).
- Avoid interface explosion; keep abstractions meaningful.
- Follow-up: When would you skip an interface entirely?

## Q12. How do you avoid tight coupling when using static utility classes?

Answer:
- Static utilities are hard to mock and limit testability.
- Wrap static calls behind an interface or adapter.
- Use static only for pure, stateless logic (e.g., string formatting).
- Avoid static access for environment-specific logic (time, network, config).
- Follow-up: How do you test time-based logic without static calls?
