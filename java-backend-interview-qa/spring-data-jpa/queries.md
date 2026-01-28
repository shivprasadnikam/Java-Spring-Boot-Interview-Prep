# Spring Data JPA Queries — Interview Q&A

## Q1. Derived query methods: when are they good and when do they hurt?

Answer:
- Good for simple lookups (`findByEmailAndStatus`).
- Hurt readability when method names get too long.
- Use for simple filters; use @Query for complex ones.
- Avoid overusing derived queries for dynamic filters.
- Follow-up: Whats the limit before you move to Specifications?

## Q2. JPQL vs native SQL: how do you choose?

Answer:
- JPQL is portable and entity-based; native SQL is DB-specific.
- Use JPQL for portability; native for complex performance queries.
- Avoid native SQL when entity mappings are sufficient.
- Follow-up: How do you map native query results to DTOs?

## Q3. What is the N+1 problem and how do you solve it?

Answer:
- N+1: one query for parent + N queries for children.
- Fix with fetch joins, entity graphs, or batch fetching.
- Avoid EAGER fetching to hide the issue.
- Follow-up: How do you detect N+1 in production?

## Q4. What is `@EntityGraph` and when do you use it?

Answer:
- Defines fetch plan for a query without changing mapping.
- Use to optimize read-heavy queries.
- Avoid large graphs that load too much data.
- Follow-up: How is `@EntityGraph` different from fetch join?

## Q5. What are Specifications and when are they better than @Query?

Answer:
- Specifications build dynamic criteria queries.
- Use for complex search screens with optional filters.
- Avoid for simple fixed queries (overkill).
- Follow-up: How do you combine Specifications?

## Q6. How do projections improve performance?

Answer:
- Return only required columns via DTO or interface projection.
- Use for read-heavy APIs to reduce data transfer.
- Avoid returning full entities when not needed.
- Follow-up: What is the difference between closed and open projections?

## Q7. How do you implement pagination correctly?

Answer:
- Use `Pageable` and `Page<T>` for total counts.
- Use `Slice<T>` when total count is expensive.
- Avoid `Page` on huge datasets with heavy count queries.
- Follow-up: How do you optimize count queries?

## Q8. What is `@Modifying` and why is it important?

Answer:
- Required for update/delete queries in repositories.
- Use with `@Transactional` for modifying queries.
- Avoid bulk updates without understanding persistence context impact.
- Follow-up: What happens to managed entities after bulk update?

## Q9. How do you implement soft deletes in JPA?

Answer:
- Use a `deleted` flag and filter queries.
- Use `@Where` or global filters in Hibernate.
- Avoid physical deletes when audit is required.
- Follow-up: How do you prevent soft-deleted data from being fetched?

## Q10. How do you handle dynamic sorting in queries?

Answer:
- Use `Sort` or `Pageable`.
- Validate allowed sort fields to prevent SQL injection.
- Avoid exposing DB column names directly to clients.
- Follow-up: How do you whitelist sorting fields?

## Q11. When do you use Querydsl?

Answer:
- Use for type-safe dynamic queries with good readability.
- Prefer when Specifications become complex.
- Avoid introducing it if your query needs are simple.
- Follow-up: How does Querydsl compare to Criteria API?

## Q12. How do you debug slow JPA queries?

Answer:
- Enable SQL logs and analyze execution plans.
- Use datasource metrics and APM tools.
- Avoid guessing; always check query plans.
- Follow-up: How do you identify missing indexes?
