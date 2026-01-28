# Microservices Communication — Interview Q&A

## Q1. Synchronous vs asynchronous communication: how do you decide?

Answer:
- Sync: low-latency request/response.
- Async: decoupled, resilient, better for long-running workflows.
- Use async for event-driven flows.
- Avoid deep sync chains across multiple services.
- Follow-up: How do you handle timeouts and retries?

## Q2. REST vs messaging: what is the trade-off?

Answer:
- REST is simple and observable; messaging is decoupled.
- Use REST for direct queries; messaging for workflows.
- Avoid using messaging for query-like interactions.
- Follow-up: How do you ensure message ordering?

## Q3. How do you ensure idempotency in inter-service calls?

Answer:
- Use idempotency keys and deduplication stores.
- Make handlers safe for repeated requests.
- Avoid non-idempotent retries on failures.
- Follow-up: How do you implement idempotency for async consumers?

## Q4. What is a circuit breaker and why is it important?

Answer:
- Prevents cascading failures by stopping calls to failing services.
- Use libraries like Resilience4j.
- Avoid retry storms without backoff.
- Follow-up: Whats the difference between circuit breaker and bulkhead?

## Q5. How do you handle timeouts?

Answer:
- Set short, realistic timeouts based on SLOs.
- Use separate timeouts for connect and read.
- Avoid infinite timeouts in production.
- Follow-up: How do you align timeouts across service chains?

## Q6. How do you handle retries safely?

Answer:
- Retry only idempotent requests with exponential backoff.
- Use jitter to avoid synchronized retries.
- Avoid retrying on 4xx errors.
- Follow-up: How do you detect retry storms?

## Q7. How do you implement distributed tracing?

Answer:
- Propagate trace IDs via headers (B3/W3C).
- Use OpenTelemetry + centralized tracing systems.
- Avoid logging without correlation IDs.
- Follow-up: How do you propagate trace IDs in async systems?

## Q8. What is schema evolution in event-driven systems?

Answer:
- Evolving message formats without breaking consumers.
- Use versioned schemas and backward compatibility.
- Avoid breaking changes in published events.
- Follow-up: How do you handle old consumers with new fields?

## Q9. How do you handle consistency across services?

Answer:
- Use eventual consistency with compensating actions.
- Avoid distributed transactions across services.
- Follow-up: What patterns help with consistency in microservices?

## Q10. What is the outbox pattern in communication?

Answer:
- Persist events in DB and publish reliably to message broker.
- Prevents lost events in dual-write scenarios.
- Avoid direct publish without outbox for critical workflows.
- Follow-up: How do you process the outbox reliably?

## Q11. How do you handle backward compatibility in APIs?

Answer:
- Tolerant readers: ignore unknown fields.
- Version APIs carefully and deprecate gradually.
- Avoid deleting fields without a migration plan.
- Follow-up: What is the consumer-driven contract testing?

## Q12. How do you secure inter-service communication?

Answer:
- Use mTLS, service identity, and least-privilege policies.
- Avoid relying only on network-level security.
- Follow-up: How does service mesh help with security?
