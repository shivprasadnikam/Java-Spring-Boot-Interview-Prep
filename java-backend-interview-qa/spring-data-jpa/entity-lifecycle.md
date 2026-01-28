# Spring Data JPA Entity Lifecycle — Interview Q&A

## Q1. What are the core JPA entity states?

Answer:
- **Transient** (new, not persisted), **Managed/Persistent**, **Detached**, **Removed**.
- Managed entities are tracked by the persistence context.
- Use managed state for updates without explicit save.
- Avoid modifying detached entities without merging.
- Follow-up: What triggers a transition from managed to detached?

## Q2. What is the persistence context and why is it important?

Answer:
- Its the first-level cache managed by the EntityManager.
- Ensures identity: same entity ID returns same instance.
- Use it to reduce DB hits within a transaction.
- Avoid large persistence contexts in batch jobs (memory bloat).
- Follow-up: How do you clear the persistence context?

## Q3. What is dirty checking and why does it matter?

Answer:
- Hibernate detects changes to managed entities and flushes updates.
- No explicit update call needed.
- Use it for simple updates inside transactions.
- Avoid unexpected updates by mutating entities outside intended scope.
- Follow-up: When does dirty checking happen?

## Q4. `flush()` vs `commit()`: whats the difference?

Answer:
- `flush()` syncs changes to DB but keeps transaction open.
- `commit()` ends the transaction.
- Use flush to enforce DB constraints early.
- Avoid flushing frequently in high-traffic endpoints.
- Follow-up: Can flush happen automatically?

## Q5. What is the difference between `find()` and `getReference()`?

Answer:
- `find()` hits DB immediately; `getReference()` returns a proxy.
- Use `getReference()` when only ID is needed and entity may not be accessed.
- Avoid accessing proxy outside transaction (LazyInitializationException).
- Follow-up: How do you check if a proxy is initialized?

## Q6. When do entities become detached?

Answer:
- After transaction ends, session closed, or `detach()` called.
- Detached entities are not tracked for changes.
- Use `merge()` to reattach changes.
- Avoid modifying detached entities assuming changes are auto-saved.
- Follow-up: Difference between `merge()` and `save()`?

## Q7. What is the first-level cache vs second-level cache?

Answer:
- First-level: persistence context, per EntityManager.
- Second-level: shared across sessions, optional (e.g., Ehcache).
- Use L2 cache for read-heavy immutable data.
- Avoid caching frequently changing entities (stale data risk).
- Follow-up: What are cache invalidation strategies?

## Q8. What does `@Transactional` do to entity lifecycle?

Answer:
- Opens a persistence context and manages flush/commit.
- Entities are managed inside transaction.
- Use for unit-of-work boundaries.
- Avoid long transactions in high-throughput services.
- Follow-up: What is the default propagation of `@Transactional`?

## Q9. What is `orphanRemoval` in terms of lifecycle?

Answer:
- Deletes child entities removed from a parent collection.
- Use for true composition (child has no independent life).
- Avoid for shared entities.
- Follow-up: How is orphan removal different from cascade remove?

## Q10. What is `EntityManager.clear()` used for?

Answer:
- Detaches all managed entities (clears persistence context).
- Use in batch processing to prevent memory growth.
- Avoid using in web requests unless you know the impact.
- Follow-up: What is the effect on dirty checking?

## Q11. When should you use `merge()`?

Answer:
- Use to reattach detached entities with updates.
- It returns a new managed instance.
- Avoid modifying the detached instance after merge (its still detached).
- Follow-up: Why does `merge()` return a different instance?

## Q12. How do you handle lifecycle callbacks in JPA?

Answer:
- Use `@PrePersist`, `@PreUpdate`, `@PostLoad`, etc.
- Use for auditing timestamps or validation.
- Avoid heavy logic in callbacks (hard to test).
- Follow-up: How do callbacks behave with bulk updates?
