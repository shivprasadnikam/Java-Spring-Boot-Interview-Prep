# Microservices Basics — Interview Q&A

## Q1. Why do companies adopt microservices instead of a monolith?

Answer:
- Independent deployment, faster team autonomy, and tech flexibility.
- Scales parts of the system independently.
- Use when domain boundaries are clear and teams are mature.
- Avoid microservices for small teams or early-stage products.
- Follow-up: What are the biggest hidden costs of microservices?

## Q2. What is a bounded context and why does it matter?

Answer:
- A boundary where a model has a specific meaning (DDD).
- Prevents shared models and tight coupling across services.
- Use it to define service ownership.
- Avoid “shared entity” libraries across services.
- Follow-up: How do you split services when boundaries are unclear?

## Q3. How do you manage data ownership in microservices?

Answer:
- Each service owns its data and schema.
- Use APIs or events for cross-service access.
- Avoid shared databases across services.
- Follow-up: When is a shared DB acceptable temporarily?

## Q4. How do you handle distributed transactions?

Answer:
- Use eventual consistency with Saga or outbox patterns.
- Avoid 2PC in high-scale systems.
- Use compensating actions when needed.
- Follow-up: What are the trade-offs of Saga choreography vs orchestration?

## Q5. What is the biggest performance trap in microservices?

Answer:
- Network calls replace in-process calls; latency adds up.
- Use bulkheads, caching, and request aggregation.
- Avoid chatty service interactions.
- Follow-up: How do you design APIs to minimize hops?

## Q6. How do you handle shared libraries across services?

Answer:
- Prefer small, stable shared libraries (logging, tracing).
- Avoid sharing domain models; it couples teams.
- Use internal versioning and backward compatibility.
- Follow-up: How do you manage breaking changes in shared libs?

## Q7. What is service decomposition and how do you validate it?

Answer:
- Split by business capability, not technical layer.
- Validate by team ownership and independent deployability.
- Avoid splitting too early or too granularly.
- Follow-up: How do you refactor a monolith incrementally?

## Q8. How do you manage configuration across many services?

Answer:
- Use centralized config management (Spring Cloud Config, Vault).
- Use environment-specific configs with strict validation.
- Avoid snowflake configurations per service.
- Follow-up: How do you handle secrets across services?

## Q9. What is the role of observability in microservices?

Answer:
- Distributed tracing, structured logs, and metrics are essential.
- Use correlation IDs across services.
- Avoid debugging without trace context.
- Follow-up: How do you propagate trace IDs?

## Q10. How do you handle backward compatibility in APIs?

Answer:
- Use versioning, optional fields, and tolerant readers.
- Avoid breaking contract changes without deprecation.
- Follow-up: How do you do zero-downtime deployments with API changes?

## Q11. What is the difference between orchestration and choreography?

Answer:
- Orchestration: central coordinator (saga orchestrator).
- Choreography: services react to events without central control.
- Use orchestration for complex workflows; choreography for simple flows.
- Avoid mixed styles without clear rules.
- Follow-up: What are failure modes in choreography?

## Q12. How do you choose sync vs async communication?

Answer:
- Sync for request/response; async for decoupled workflows.
- Use async for long-running processes or fan-out.
- Avoid sync chains across many services.
- Follow-up: How do you handle retries and idempotency?
