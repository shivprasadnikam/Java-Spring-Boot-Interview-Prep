# Service Discovery — Interview Q&A

## Q1. What problem does service discovery solve?

Answer:
- Services scale dynamically; IPs change frequently.
- Discovery provides a registry for finding instances.
- Use it in microservices with dynamic scaling.
- Avoid it if you have static endpoints.
- Follow-up: How does service discovery relate to load balancing?

## Q2. Client-side vs server-side discovery: when to use each?

Answer:
- Client-side: client queries registry directly (Eureka).
- Server-side: client hits LB/gateway which queries registry.
- Use client-side for internal services; server-side for external traffic.
- Avoid client-side for untrusted clients.
- Follow-up: What are the trade-offs in latency and complexity?

## Q3. How does Eureka work in Spring Cloud?

Answer:
- Services register and send heartbeats; clients query registry.
- Eureka is AP (availability over consistency).
- Use for non-critical discovery with eventual consistency.
- Avoid Eureka for strict consistency requirements.
- Follow-up: How does Eureka handle stale instances?

## Q4. What are health checks and why are they critical?

Answer:
- They determine if an instance is alive and ready.
- Use liveness/readiness probes in Kubernetes.
- Avoid routing traffic to instances during startup.
- Follow-up: How do you design deep vs shallow health checks?

## Q5. How do you handle discovery across multiple regions?

Answer:
- Use region-aware registries or global service mesh.
- Avoid cross-region traffic for latency-sensitive calls.
- Use routing rules or topology-aware discovery.
- Follow-up: How do you handle failover across regions?

## Q6. What is a service mesh and how does it relate to discovery?

Answer:
- Mesh provides sidecars for discovery, routing, and security.
- It can replace client-side discovery logic.
- Use when you need consistent traffic policies.
- Avoid mesh if your system is small and simple.
- Follow-up: What are the operational costs of a service mesh?

## Q7. How do you handle service deregistration failures?

Answer:
- Use heartbeat timeouts and passive eviction.
- Avoid relying solely on graceful deregistration.
- Follow-up: What happens during network partitions?

## Q8. How do you secure service discovery?

Answer:
- Use TLS and service-to-service auth for registry access.
- Restrict registry access to internal networks.
- Avoid exposing service registry publicly.
- Follow-up: How do you manage certificates for discovery?

## Q9. How do you handle canary deployments with discovery?

Answer:
- Register canary instances with tags/metadata.
- Use routing rules to direct a small percentage of traffic.
- Avoid canary without monitoring and rollback plan.
- Follow-up: How do you ensure canary doesn’t pollute all traffic?

## Q10. What are the failure modes of service discovery?

Answer:
- Registry outages, stale data, or network partitions.
- Use local caching and retries.
- Avoid single registry instance (SPOF).
- Follow-up: How do you design a highly available registry?

## Q11. What is DNS-based discovery and when is it enough?

Answer:
- Uses DNS records to resolve service endpoints.
- Use for simple, stable services or k8s internal DNS.
- Avoid when you need richer metadata or health checks.
- Follow-up: How does DNS TTL affect traffic routing?

## Q12. How do you handle versioning in discovery?

Answer:
- Use metadata tags or separate service names per version.
- Avoid overwriting old versions without client migration.
- Follow-up: How do you route traffic to specific versions?
