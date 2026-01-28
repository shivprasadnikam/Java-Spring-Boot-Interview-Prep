# Spring Data JPA Performance — Interview Q&A

## Q1. What are the top JPA performance killers in production?

Answer:
- N+1 queries, large persistence context, unbounded pagination.
- Use fetch joins, projections, and batch fetching.
- Avoid returning entities directly for read APIs.
- Follow-up: How do you detect N+1 in logs?

## Q2. How do you handle batch inserts/updates efficiently?

Answer:
- Use JDBC batch settings and flush/clear periodically.
- Use `saveAll` carefully with batch size.
- Avoid huge transactions without batching.
- Follow-up: What Hibernate properties enable batching?

## Q3. When should you use projections or DTO queries?

Answer:
- Use for read-heavy endpoints needing only a subset of fields.
- Reduces memory and serialization cost.
- Avoid using entities for large list endpoints.
- Follow-up: How do you map native query results to DTOs?

## Q4. How do you use indexes with JPA to improve performance?

Answer:
- Add DB indexes on filter and join columns.
- Use `@Index` or DB migrations for indexing.
- Avoid over-indexing; it slows writes.
- Follow-up: How do you verify index usage?

## Q5. What is second-level cache and when do you enable it?

Answer:
- Shared cache across sessions for read-heavy, rarely changing data.
- Use for reference data like countries or product categories.
- Avoid caching high-churn entities.
- Follow-up: How do you invalidate cached entities?

## Q6. How do you avoid `LazyInitializationException` without OSIV?

Answer:
- Use fetch joins or DTOs in service layer.
- Keep transactional boundaries in service methods.
- Avoid OSIV in APIs for performance and clarity.
- Follow-up: When is OSIV acceptable?

## Q7. What is the cost of EAGER fetching?

Answer:
- Loads large object graphs unnecessarily.
- Can cause huge joins and slow queries.
- Use LAZY by default and fetch explicitly.
- Follow-up: How do you override fetch plan per query?

## Q8. How do you handle large result sets?

Answer:
- Use pagination, streaming, or batch processing.
- For exports, use cursor-based streaming to avoid memory spikes.
- Avoid loading everything into memory.
- Follow-up: How do you stream results safely with transactions?

## Q9. What are entity graphs and when are they better than join fetch?

Answer:
- Entity graphs allow dynamic fetch plans without changing queries.
- Use for reusable fetch profiles.
- Avoid giant graphs that fetch too much.
- Follow-up: How do you combine entity graphs with pagination?

## Q10. How do you measure and monitor JPA performance?

Answer:
- Use SQL logging, APM tools, and database metrics.
- Track slow queries and connection pool usage.
- Avoid guessing; rely on profiles and query plans.
- Follow-up: What metrics do you alert on?

## Q11. What is the impact of `save()` in loops?

Answer:
- Each save can trigger flush and persistence overhead.
- Use `saveAll` with batch size and flush control.
- Avoid auto-flush on every iteration.
- Follow-up: How do you control flush mode?

## Q12. When should you consider skipping JPA entirely?

Answer:
- For highly complex or performance-critical queries.
- Use JDBC or jOOQ for fine-grained control.
- Avoid dogmatically using JPA everywhere.
- Follow-up: How do you mix JPA with JDBC safely?
