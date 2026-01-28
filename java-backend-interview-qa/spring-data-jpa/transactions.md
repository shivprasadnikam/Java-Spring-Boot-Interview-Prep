# Spring Data JPA Transactions — Interview Q&A

## Q1. What does `@Transactional` actually guarantee?

Answer:
- Provides ACID boundary for the method.
- Ensures commit or rollback at the end.
- Use for write operations or consistent reads.
- Avoid wrapping long-running operations in a single transaction.
- Follow-up: What is the default propagation?

## Q2. Propagation types: when do you use REQUIRES_NEW?

Answer:
- REQUIRES_NEW suspends existing transaction and creates a new one.
- Use for audit logs or outbox events that must commit independently.
- Avoid overusing; it can break atomicity assumptions.
- Follow-up: How does REQUIRES_NEW affect isolation?

## Q3. What is isolation level and how do you pick it?

Answer:
- Controls read phenomena (dirty, non-repeatable, phantom reads).
- Default is often READ_COMMITTED.
- Use higher levels for financial/critical workflows.
- Avoid SERIALIZABLE for high-throughput services (locks).
- Follow-up: How do you handle phantom reads without SERIALIZABLE?

## Q4. When does Spring roll back a transaction?

Answer:
- Rolls back on unchecked exceptions by default.
- Checked exceptions require explicit configuration.
- Use `rollbackFor` for business checked exceptions.
- Avoid swallowing exceptions inside transactional methods.
- Follow-up: How do you handle partial failures gracefully?

## Q5. What is the difference between programmatic and declarative transactions?

Answer:
- Declarative (`@Transactional`) is cleaner and preferred.
- Programmatic gives fine control but adds boilerplate.
- Use programmatic in rare, complex flows.
- Avoid mixing both in the same service.
- Follow-up: When would you use TransactionTemplate?

## Q6. Transactional proxy pitfalls: what should you watch for?

Answer:
- Self-invocation bypasses proxies.
- Private methods are not proxied.
- Use separate service beans for transactional boundaries.
- Avoid calling transactional methods inside the same class.
- Follow-up: How do you test transactional boundaries?

## Q7. Read-only transactions: do they matter?

Answer:
- Hint to the provider; can optimize flush behavior.
- Use for pure reads to reduce overhead.
- Avoid assuming read-only enforces immutability.
- Follow-up: Does read-only prevent updates in Hibernate?

## Q8. What is the outbox pattern and why is it transactional?

Answer:
- Writes domain event to DB in same transaction as business update.
- Ensures reliable event publishing.
- Use when integrating with Kafka without dual-write issues.
- Avoid direct publish without outbox for critical events.
- Follow-up: How do you publish outbox events safely?

## Q9. How do transactions interact with lazy loading?

Answer:
- Lazy loading requires an open session/transaction.
- Use transactions in service layer to keep session open.
- Avoid LazyInitializationException by using DTOs or fetch joins.
- Follow-up: How do you avoid OpenSessionInView in APIs?

## Q10. What is `TransactionSynchronization` used for?

Answer:
- Hooks into transaction lifecycle for callbacks.
- Use for post-commit actions (publish events).
- Avoid heavy logic in synchronization callbacks.
- Follow-up: Whats the difference from `@TransactionalEventListener`?

## Q11. How do you handle retries with transactions?

Answer:
- Use retry templates around transactional methods.
- Ensure idempotency to avoid duplicates.
- Avoid retrying non-idempotent operations without safeguards.
- Follow-up: How do you make writes idempotent?

## Q12. What are common transaction performance problems?

Answer:
- Long transactions holding locks.
- Large persistence context causing memory spikes.
- Use smaller transaction scopes and batch flushing.
- Avoid transactions around network calls.
- Follow-up: How do you measure transaction duration?
