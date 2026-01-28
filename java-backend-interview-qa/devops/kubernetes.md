# Kubernetes — Interview Q&A

## Q1. What problem does Kubernetes solve?

Answer:
- Orchestrates containers: scaling, scheduling, self-healing.
- Provides declarative deployment and service discovery.
- Use when managing multiple containerized services.
- Avoid k8s for very small deployments (ops overhead).
- Follow-up: What are the biggest operational costs of k8s?

## Q2. What is the role of a Pod?

Answer:
- Smallest deployable unit; one or more containers sharing network.
- Use sidecar patterns for logging or proxies.
- Avoid putting unrelated containers in one pod.
- Follow-up: How does pod networking work?

## Q3. Deployment vs StatefulSet: when do you use each?

Answer:
- Deployment: stateless workloads.
- StatefulSet: stable identity and storage for stateful apps.
- Use StatefulSet for databases or Kafka.
- Avoid StatefulSet for stateless services.
- Follow-up: How do you handle stateful upgrades safely?

## Q4. What is a Service in Kubernetes?

Answer:
- Stable virtual IP and DNS for pods.
- Use ClusterIP for internal access, LoadBalancer for external.
- Avoid exposing internal services unnecessarily.
- Follow-up: What is the difference between NodePort and LoadBalancer?

## Q5. What are liveness and readiness probes?

Answer:
- Liveness: restart unhealthy pods; Readiness: traffic routing readiness.
- Use both for robust deployments.
- Avoid using the same endpoint for both.
- Follow-up: How do you design effective readiness checks?

## Q6. How do you scale in Kubernetes?

Answer:
- Use HPA based on CPU/metrics.
- Use Cluster Autoscaler for node scaling.
- Avoid autoscaling without resource limits.
- Follow-up: How do you scale on custom metrics?

## Q7. What is ConfigMap vs Secret?

Answer:
- ConfigMap for non-sensitive config; Secret for sensitive data.
- Use external secret managers for production.
- Avoid putting secrets in plain YAML.
- Follow-up: How do you rotate secrets in k8s?

## Q8. What is ingress and when do you use it?

Answer:
- Ingress manages HTTP routing to services.
- Use for consolidated routing and TLS termination.
- Avoid multiple external load balancers per service.
- Follow-up: How does ingress compare to an API gateway?

## Q9. What are common failure causes in k8s deployments?

Answer:
- Incorrect resource limits, bad probes, missing configs.
- Use `kubectl describe` and events for debugging.
- Avoid deploying without staging tests.
- Follow-up: How do you debug CrashLoopBackOff?

## Q10. What is a namespace and why use it?

Answer:
- Logical isolation of resources.
- Use for multi-team or multi-env clusters.
- Avoid putting everything in `default` namespace.
- Follow-up: How do you control access by namespace?

## Q11. What is a rolling update and how does it work?

Answer:
- Gradually replaces pods while keeping service available.
- Use readiness probes to avoid downtime.
- Avoid rolling updates without rollback plans.
- Follow-up: How do you do canary deployments in k8s?

## Q12. How do you secure Kubernetes clusters?

Answer:
- Use RBAC, network policies, and least privilege.
- Avoid default service account permissions.
- Follow-up: How do you enforce pod security standards?
