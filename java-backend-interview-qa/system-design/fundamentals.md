# System Design Fundamentals — Interview Q&A

## Q1. How do you start a system design interview?

Answer:
- Clarify requirements, scope, and non-functional needs first.
- Identify core workflows and traffic assumptions.
- Avoid jumping into components before constraints.
- Follow-up: What questions do you ask to size traffic?

## Q2. What are the most important non-functional requirements?

Answer:
- Availability, latency, scalability, consistency, and durability.
- Use SLOs to decide trade-offs.
- Avoid treating all requirements as equal priority.
- Follow-up: How do you pick availability targets?

## Q3. How do you estimate capacity quickly?

Answer:
- Use rough numbers: QPS, data size, growth, peak multipliers.
- Focus on orders of magnitude, not exactness.
- Avoid over-optimizing the estimate early.
- Follow-up: How do you model peak vs average load?

## Q4. When do you choose relational vs NoSQL?

Answer:
- Relational for strong consistency and complex queries.
- NoSQL for high scale and flexible schema.
- Avoid NoSQL if you need joins and transactions heavily.
- Follow-up: How do you ensure consistency in NoSQL?

## Q5. What are common caching strategies?

Answer:
- Cache-aside, read-through, write-through, write-behind.
- Use TTLs and eviction policies.
- Avoid caching mutable data without invalidation strategy.
- Follow-up: What causes cache stampedes and how to prevent them?

## Q6. How do you handle data partitioning?

Answer:
- Use sharding by user ID or tenant.
- Ensure even distribution and avoid hotspots.
- Avoid hardcoding shard maps without rebalancing plan.
- Follow-up: How do you handle resharding?

## Q7. Consistency models: when do you pick strong consistency?

Answer:
- Strong consistency for money or inventory.
- Eventual consistency for feeds or analytics.
- Avoid strong consistency when latency is critical.
- Follow-up: How do you explain CAP trade-offs?

## Q8. How do you design for high availability?

Answer:
- Use redundancy, health checks, and multi-AZ deployments.
- Ensure stateless services and externalized state.
- Avoid single points of failure.
- Follow-up: How do you design failover?

## Q9. What is idempotency and why is it critical?

Answer:
- Ensures repeated requests don’t cause extra side effects.
- Use idempotency keys for writes.
- Avoid non-idempotent retries.
- Follow-up: How do you implement idempotency in APIs?

## Q10. How do you design rate limiting?

Answer:
- Use token bucket or leaky bucket.
- Enforce limits at API gateway or edge.
- Avoid per-instance limits without shared state.
- Follow-up: How do you handle burst traffic fairly?

## Q11. What is the role of queues in system design?

Answer:
- Decouple services and smooth traffic spikes.
- Use queues for async processing and retries.
- Avoid queueing for sync user flows that need immediate responses.
- Follow-up: How do you handle poison messages?

## Q12. How do you evaluate trade-offs during design?

Answer:
- Optimize for the most important constraint first.
- Explain why each component exists.
- Avoid adding complexity without clear benefit.
- Follow-up: What are typical trade-offs between cost and latency?
