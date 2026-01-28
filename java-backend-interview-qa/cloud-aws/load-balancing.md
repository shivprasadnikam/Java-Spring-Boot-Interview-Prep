# AWS Load Balancing — Interview Q&A

## Q1. ALB vs NLB: how do you choose?

Answer:
- ALB is Layer 7 (HTTP) with routing rules; NLB is Layer 4 (TCP).
- Use ALB for REST APIs; NLB for low-latency TCP or gRPC.
- Avoid ALB for non-HTTP traffic.
- Follow-up: How does NLB handle source IP preservation?

## Q2. What is the role of health checks in load balancing?

Answer:
- Ensures only healthy instances receive traffic.
- Use readiness checks in containerized apps.
- Avoid shallow checks that always return 200.
- Follow-up: How do you avoid health check storms?

## Q3. How does sticky session affect scalability?

Answer:
- Sticky sessions tie a client to one instance.
- Use only if session state cant be externalized.
- Avoid stickiness by storing session in Redis or JWT.
- Follow-up: What are the risks of sticky sessions during scaling?

## Q4. How does load balancing work with auto scaling?

Answer:
- New instances register with the LB automatically.
- Health checks ensure warm-up before traffic.
- Avoid scaling without LB health check readiness.
- Follow-up: How do you handle slow-start for new instances?

## Q5. What is cross-zone load balancing?

Answer:
- Distributes traffic across all AZs equally.
- Use to avoid hotspotting in a single AZ.
- Avoid if inter-AZ traffic cost is a concern.
- Follow-up: How do you handle AZ-specific outages?

## Q6. How do you handle SSL termination at the LB?

Answer:
- Terminate SSL at LB for simpler certificate management.
- Use HTTPS between LB and instances for sensitive data.
- Avoid plain HTTP for internal traffic when data is sensitive.
- Follow-up: How do you implement mTLS behind the LB?

## Q7. What is connection draining (deregistration delay)?

Answer:
- Allows in-flight requests to complete before instance removal.
- Use to avoid user-facing errors during scaling.
- Avoid setting it too high (slow scale down).
- Follow-up: How do you handle long-lived connections?

## Q8. How do you route traffic for multiple services?

Answer:
- Use path-based or host-based routing in ALB.
- Use separate target groups per service.
- Avoid single target group for unrelated services.
- Follow-up: How do you deploy canary traffic with ALB?

## Q9. What is the impact of LB on latency?

Answer:
- Adds a network hop and TLS handshake cost.
- Use keep-alive and HTTP/2 to reduce overhead.
- Avoid unnecessary hops in internal service calls.
- Follow-up: How do you measure LB-induced latency?

## Q10. How do you secure the load balancer?

Answer:
- Use security groups to restrict inbound traffic.
- Enable WAF for public endpoints.
- Avoid exposing internal services through public ALB.
- Follow-up: How do you protect against DDoS at the LB level?

## Q11. What is target group and why is it important?

Answer:
- Defines backend instances and health check rules.
- Use separate target groups for different services/versions.
- Avoid mixing workloads in one target group.
- Follow-up: How do you shift traffic between target groups?

## Q12. How do you handle HTTP/2 or gRPC through ALB/NLB?

Answer:
- ALB supports HTTP/2; NLB can forward TCP for gRPC.
- Use NLB for raw gRPC performance.
- Avoid ALB if gRPC needs low-level control.
- Follow-up: What are the TLS requirements for gRPC on AWS?
