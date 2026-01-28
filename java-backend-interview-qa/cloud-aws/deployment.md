# AWS Deployment — Interview Q&A

## Q1. Blue/green vs rolling deployments: what do you prefer?

Answer:
- Blue/green: instant rollback, higher cost.
- Rolling: lower cost, slower rollback.
- Use blue/green for critical services.
- Avoid rolling if you cant handle partial versions.
- Follow-up: How do you manage DB schema changes with blue/green?

## Q2. What is a launch template and why use it?

Answer:
- Defines instance config for Auto Scaling.
- Use to ensure consistent AMIs and settings.
- Avoid manual instance creation in autoscaled systems.
- Follow-up: How do you update launch templates safely?

## Q3. How do you deploy containerized apps on AWS?

Answer:
- Use ECS/Fargate or EKS.
- Choose Fargate for serverless containers.
- Avoid managing EC2 unless necessary for cost or control.
- Follow-up: How do you handle service discovery in ECS?

## Q4. What is AWS CodeDeploy and when is it used?

Answer:
- Automates deployments to EC2, Lambda, or ECS.
- Use with CodePipeline for CI/CD.
- Avoid manual SSH deployments in production.
- Follow-up: How do you perform canary deployments in CodeDeploy?

## Q5. How do you handle secrets during deployment?

Answer:
- Use AWS Secrets Manager or Parameter Store.
- Inject secrets at runtime, not bake into images.
- Avoid storing secrets in code or build artifacts.
- Follow-up: How do you rotate secrets without downtime?

## Q6. What is immutable infrastructure?

Answer:
- Replace servers instead of patching in place.
- Use AMIs or container images for each release.
- Avoid in-place changes that cause drift.
- Follow-up: How does immutable infra simplify rollback?

## Q7. How do you implement zero-downtime deployments?

Answer:
- Use load balancers with health checks and gradual traffic shift.
- Use backward-compatible changes.
- Avoid schema changes that break old code.
- Follow-up: How do you do zero-downtime DB migrations?

## Q8. What is the role of health checks in deployment?

Answer:
- Ensures only healthy instances receive traffic.
- Use readiness checks for warm-up time.
- Avoid shallow checks that miss failures.
- Follow-up: What should a good health check verify?

## Q9. How do you manage deployment across multiple environments?

Answer:
- Use separate accounts or strong isolation per env.
- Use consistent pipelines with env-specific configs.
- Avoid manual differences between envs.
- Follow-up: How do you manage config drift across envs?

## Q10. How do you roll back safely?

Answer:
- Use previous version images and keep metadata.
- Automate rollback triggers on failures.
- Avoid manual hotfixes that diverge from pipeline.
- Follow-up: What metrics trigger automatic rollback?

## Q11. What is canary deployment and when is it best?

Answer:
- Roll out to a small % of traffic before full rollout.
- Use for high-risk changes.
- Avoid canary without monitoring and alerting.
- Follow-up: How do you select canary cohorts?

## Q12. How do you handle logging during deployments?

Answer:
- Centralize logs and tag with version metadata.
- Use log correlation to detect regressions.
- Avoid losing logs during instance replacement.
- Follow-up: How do you compare error rates between versions?
