# CI/CD — Interview Q&A

## Q1. What is the real value of CI/CD in backend teams?

Answer:
- Faster feedback, reliable releases, less manual error.
- Enables frequent, smaller deployments.
- Use CI/CD to enforce quality gates.
- Avoid manual deployments for production services.
- Follow-up: What are key CI/CD metrics you track?

## Q2. What should a good CI pipeline include?

Answer:
- Build, unit tests, static analysis, security scans.
- Use fast feedback for developer productivity.
- Avoid slow pipelines that block development.
- Follow-up: How do you parallelize CI jobs?

## Q3. What should a CD pipeline include?

Answer:
- Artifact promotion, deployment, smoke tests, rollback.
- Use staging environment for validation.
- Avoid deploying directly to prod without staging.
- Follow-up: How do you implement automated rollback?

## Q4. What is the difference between build artifacts and deploy artifacts?

Answer:
- Build artifact: compiled package (JAR, image).
- Deploy artifact: environment-ready unit with config.
- Use immutable artifacts across environments.
- Avoid rebuilding artifacts per environment.
- Follow-up: How do you manage artifact versioning?

## Q5. How do you handle database migrations in CI/CD?

Answer:
- Use Flyway or Liquibase with versioned scripts.
- Apply migrations before app rollout.
- Avoid destructive migrations without rollback plans.
- Follow-up: How do you make backward-compatible schema changes?

## Q6. What are deployment strategies in CI/CD?

Answer:
- Blue/green, rolling, canary.
- Use canary for high-risk changes.
- Avoid big-bang releases.
- Follow-up: How do you choose a strategy per service?

## Q7. How do you integrate security into CI/CD?

Answer:
- Use SAST, dependency scans, container scans.
- Fail builds on critical vulnerabilities.
- Avoid ignoring security scan results.
- Follow-up: How do you handle false positives in scans?

## Q8. What is trunk-based development and why use it?

Answer:
- Developers commit to main branch frequently with feature flags.
- Reduces merge conflicts and integration pain.
- Avoid long-lived feature branches.
- Follow-up: How do you manage feature toggles in production?

## Q9. How do you handle environment parity across dev/stage/prod?

Answer:
- Use IaC and consistent configs.
- Avoid manual environment differences.
- Use containers to standardize runtime.
- Follow-up: How do you detect config drift?

## Q10. What is the role of observability in CI/CD?

Answer:
- Deployment metrics and logs drive rollback decisions.
- Use automated checks after release.
- Avoid deploying without monitoring coverage.
- Follow-up: What signals trigger rollback?

## Q11. How do you manage secrets in CI/CD pipelines?

Answer:
- Use secret managers, not plain environment files.
- Rotate secrets regularly.
- Avoid logging secrets in pipeline output.
- Follow-up: How do you audit secret usage?

## Q12. How do you handle multi-service deployments?

Answer:
- Use dependency-aware rollouts and contract tests.
- Avoid tight coupling between release schedules.
- Follow-up: How do you handle breaking API changes?
