# AWS Core Services — Interview Q&A

## Q1. EC2 vs ECS vs EKS: how do you choose?

Answer:
- EC2: full VM control; ECS: managed containers; EKS: Kubernetes.
- Use ECS for simpler ops; EKS for k8s ecosystem.
- Avoid EKS unless you need k8s-specific features.
- Follow-up: What are the hidden costs of EKS?

## Q2. What is the difference between S3 and EBS?

Answer:
- S3 is object storage; EBS is block storage attached to EC2.
- Use S3 for static assets and backups.
- Avoid using S3 as a database replacement.
- Follow-up: How do you secure S3 buckets?

## Q3. RDS vs DynamoDB: when to use each?

Answer:
- RDS: relational, ACID, SQL.
- DynamoDB: NoSQL, key-value, high scale.
- Use RDS for transactional apps; DynamoDB for scale-heavy workloads.
- Avoid DynamoDB for complex relational queries.
- Follow-up: How do you model relational data in DynamoDB?

## Q4. What is IAM and why is it critical?

Answer:
- Controls permissions and access policies.
- Use least privilege for all services.
- Avoid long-lived access keys for apps.
- Follow-up: How do you rotate IAM credentials?

## Q5. What is VPC and how does it affect service design?

Answer:
- Virtual network isolation for your AWS resources.
- Use subnets and security groups for segmentation.
- Avoid placing everything in a public subnet.
- Follow-up: What is the role of NAT Gateway?

## Q6. What are security groups vs NACLs?

Answer:
- Security groups are stateful, instance-level.
- NACLs are stateless, subnet-level.
- Use security groups for fine-grained control.
- Avoid using NACLs as primary security mechanism.
- Follow-up: How does traffic flow through SG and NACL?

## Q7. What is CloudWatch used for?

Answer:
- Metrics, logs, alarms, and dashboards.
- Use for monitoring and alerting service health.
- Avoid ignoring log retention settings.
- Follow-up: How do you set up custom metrics?

## Q8. What is SQS and when do you use it?

Answer:
- Managed queue for decoupling services.
- Use for background processing and buffering spikes.
- Avoid using SQS for event streaming (Kafka is better).
- Follow-up: How do you handle DLQ in SQS?

## Q9. What is SNS and how is it different from SQS?

Answer:
- SNS is pub/sub; SQS is point-to-point.
- Use SNS for fan-out to multiple subscribers.
- Avoid using SNS alone for guaranteed processing (use SNS+SQS).
- Follow-up: How do you design fan-out at scale?

## Q10. What is ELB/ALB and where does it fit?

Answer:
- ELB distributes traffic across instances.
- ALB works at HTTP layer with path-based routing.
- Use ALB for microservices routing.
- Avoid hardcoding instance IPs.
- Follow-up: How do you perform health checks with ALB?

## Q11. What is Auto Scaling and why does it matter?

Answer:
- Automatically adds/removes instances based on load.
- Use for cost optimization and resilience.
- Avoid scaling without proper metrics and cooldowns.
- Follow-up: What metrics drive scaling policies?

## Q12. What is the role of Route 53?

Answer:
- DNS service for routing and failover.
- Use for latency-based and weighted routing.
- Avoid direct public IP usage without DNS.
- Follow-up: How do you design DNS failover?
