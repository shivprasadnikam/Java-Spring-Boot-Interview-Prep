# Docker — Interview Q&A

## Q1. What is the difference between a container and a VM?

Answer:
- Containers share the host kernel; VMs emulate hardware.
- Containers are lighter and faster to start.
- Use containers for microservices and CI pipelines.
- Avoid containers for workloads needing full OS isolation.
- Follow-up: What are the security trade-offs?

## Q2. What makes a Docker image efficient?

Answer:
- Small layers, minimal base images, cached builds.
- Use multi-stage builds to reduce size.
- Avoid bundling build tools in runtime images.
- Follow-up: Why is image size a performance concern?

## Q3. How do you manage secrets in Docker?

Answer:
- Use env vars, Docker secrets, or external secret managers.
- Avoid baking secrets into images.
- Use runtime injection through CI/CD.
- Follow-up: How do you rotate secrets safely?

## Q4. What is the role of a Dockerfile layer cache?

Answer:
- Speeds up builds by reusing unchanged layers.
- Order instructions from least to most frequently changing.
- Avoid invalidating cache with unnecessary COPY steps.
- Follow-up: How do you optimize build time in CI?

## Q5. What is Docker networking and which mode is common?

Answer:
- Bridge networking is common for single-host setups.
- Host mode removes isolation but reduces overhead.
- Use overlay in Swarm or Kubernetes networking.
- Avoid host mode for multi-tenant security needs.
- Follow-up: How do containers discover each other?

## Q6. What is a volume and when do you use it?

Answer:
- Volumes persist data outside container lifecycle.
- Use for DBs, logs, or local dev data.
- Avoid storing state inside ephemeral containers.
- Follow-up: What is the difference between bind mounts and volumes?

## Q7. How do you handle logging in containers?

Answer:
- Log to stdout/stderr and aggregate centrally.
- Use log drivers or sidecar collectors.
- Avoid writing logs to local files inside containers.
- Follow-up: How do you prevent log loss on container restart?

## Q8. What is the difference between ENTRYPOINT and CMD?

Answer:
- ENTRYPOINT defines the executable; CMD provides defaults.
- Use ENTRYPOINT for fixed binaries.
- Avoid overriding ENTRYPOINT unless necessary.
- Follow-up: How do you pass runtime arguments?

## Q9. How do you debug container failures in prod?

Answer:
- Use logs, health checks, and `docker inspect`.
- Use exec into container only for temporary debugging.
- Avoid changing running containers; fix via new builds.
- Follow-up: How do you debug crash loops?

## Q10. What are common Docker security pitfalls?

Answer:
- Running as root, using privileged mode, exposing ports.
- Use non-root users and least privilege.
- Avoid mounting docker socket in containers.
- Follow-up: How do you scan images for vulnerabilities?

## Q11. How do you handle resource limits in Docker?

Answer:
- Use CPU/memory limits to prevent noisy neighbors.
- Avoid unlimited containers in shared hosts.
- Follow-up: How do limits affect JVM performance?

## Q12. When should you use Docker Compose?

Answer:
- Use for local dev and integration testing.
- Avoid for production deployments at scale.
- Follow-up: How do you manage config across environments?
