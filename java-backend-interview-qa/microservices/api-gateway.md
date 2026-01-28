# API Gateway — Interview Q&A

## Q1. Why do you need an API gateway?

Answer:
- Single entry point for routing, auth, throttling, and aggregation.
- Reduces client complexity and hides internal services.
- Use for public APIs and multi-service systems.
- Avoid if system is small or purely internal.
- Follow-up: What is the trade-off between gateway and direct calls?

## Q2. What are typical gateway responsibilities?

Answer:
- Auth, rate limiting, request routing, response aggregation.
- TLS termination and request logging.
- Avoid packing business logic into the gateway.
- Follow-up: How do you handle gateway failures?

## Q3. How do you handle authentication at the gateway?

Answer:
- Validate tokens and pass identity headers downstream.
- Use centralized auth with JWT or OAuth2.
- Avoid re-authenticating in every service without need.
- Follow-up: How do you prevent header spoofing?

## Q4. What is request aggregation and when is it useful?

Answer:
- Combine multiple service calls into one response.
- Use to reduce client-side chattiness.
- Avoid heavy aggregation that increases gateway latency.
- Follow-up: How do you handle partial failures in aggregation?

## Q5. How do you implement rate limiting?

Answer:
- Use token bucket or leaky bucket algorithms.
- Store counters in Redis for distributed limits.
- Avoid per-instance limits without global coordination.
- Follow-up: How do you set fair limits for different clients?

## Q6. How do you handle versioning in an API gateway?

Answer:
- Route requests based on URI or header version.
- Support multiple versions concurrently.
- Avoid breaking old versions without deprecation.
- Follow-up: How do you roll out a new version safely?

## Q7. What is the difference between API gateway and service mesh?

Answer:
- Gateway is edge traffic; mesh is internal service-to-service traffic.
- Use both in complex systems.
- Avoid overlapping responsibilities without clarity.
- Follow-up: When would a mesh reduce the need for a gateway?

## Q8. How do you secure a gateway?

Answer:
- TLS, WAF rules, auth, rate limiting.
- Restrict internal routes and admin endpoints.
- Avoid exposing gateway admin APIs publicly.
- Follow-up: How do you handle DDoS protection?

## Q9. How do you handle logging and tracing at the gateway?

Answer:
- Generate or propagate correlation IDs.
- Add structured logs for latency and failures.
- Avoid logging sensitive payloads.
- Follow-up: How do you propagate trace IDs downstream?

## Q10. What are common gateway pitfalls?

Answer:
- Overloading with business logic, creating a bottleneck.
- Single point of failure if not scaled.
- Avoid heavy transformations that slow throughput.
- Follow-up: How do you make gateways highly available?

## Q11. How do you handle retries and timeouts at the gateway?

Answer:
- Use conservative timeouts and retry only idempotent requests.
- Avoid retry storms that overload services.
- Follow-up: How do you implement circuit breakers at the gateway?

## Q12. How do you choose between Spring Cloud Gateway and API Gateway services?

Answer:
- Use Spring Cloud Gateway for custom Java/Spring stacks.
- Use managed gateways (AWS API Gateway) for simpler ops.
- Avoid heavy custom gateways if managed ones suffice.
- Follow-up: What are cost trade-offs between managed vs self-hosted?
