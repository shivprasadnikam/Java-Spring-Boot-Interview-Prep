# Kafka — Interview Q&A

## Q1. Why use Kafka over a traditional message queue?

Answer:
- Kafka is a distributed log with high throughput and replayability.
- Consumers can re-read messages; queues usually delete after consume.
- Use Kafka for event streaming and audit trails.
- Avoid Kafka for simple point-to-point tasks where a queue is enough.
- Follow-up: How does Kafka ensure ordering?

## Q2. What is the role of partitions in Kafka?

Answer:
- Partitions enable parallelism and scale.
- Ordering is guaranteed only within a partition.
- Use key-based partitioning to keep related events ordered.
- Avoid too many partitions; it increases overhead.
- Follow-up: How do you choose partition count?

## Q3. Producer acks: what do they mean?

Answer:
- `acks=0`: fire-and-forget, fastest, least safe.
- `acks=1`: leader only; good balance for most cases.
- `acks=all`: safest, slower.
- Avoid `acks=0` for critical events.
- Follow-up: How does `min.insync.replicas` affect durability?

## Q4. What is consumer group rebalancing and why is it tricky?

Answer:
- Rebalancing redistributes partitions when consumers change.
- It pauses consumption temporarily.
- Use cooperative rebalancing to reduce disruption.
- Avoid frequent scaling events without understanding impact.
- Follow-up: How do you minimize rebalancing time?

## Q5. What is the difference between at-least-once and exactly-once?

Answer:
- At-least-once can produce duplicates; exactly-once avoids it.
- Use idempotent consumers even with at-least-once.
- Avoid assuming Kafka guarantees exactly-once end-to-end.
- Follow-up: How does Kafka implement EOS?

## Q6. How do you handle message ordering requirements?

Answer:
- Use the same key to keep events in one partition.
- For global ordering, use a single partition (limits scale).
- Avoid relying on ordering across partitions.
- Follow-up: How do you handle ordering with multiple producers?

## Q7. What is a consumer offset and where is it stored?

Answer:
- Offset tracks last processed message.
- Stored in Kafka internal topic `__consumer_offsets`.
- Use manual commits for fine-grained control.
- Avoid auto-commit for critical processing.
- Follow-up: What happens if you commit too early?

## Q8. How do you handle retries and DLQ with Kafka?

Answer:
- Use retry topics with backoff or a dead-letter topic.
- Avoid infinite retries that block partitions.
- Follow-up: How do you ensure idempotency in consumers?

## Q9. What is log compaction and when do you enable it?

Answer:
- Retains the latest message per key.
- Use for state snapshots (e.g., user profiles).
- Avoid compaction for event history.
- Follow-up: How does compaction interact with retention?

## Q10. How do you tune Kafka for throughput?

Answer:
- Increase batch size, linger time, and compression.
- Use appropriate partition count.
- Avoid huge batches that increase latency.
- Follow-up: How do you monitor producer/consumer lag?

## Q11. What are common Kafka failure modes in production?

Answer:
- Broker outages, leader election delays, disk saturation.
- Use replication and monitor ISR shrinkage.
- Avoid running with replication factor 1 in production.
- Follow-up: How do you handle disk full scenarios?

## Q12. What is schema registry and why use it?

Answer:
- Manages schema evolution (Avro/Protobuf).
- Ensures compatibility between producers and consumers.
- Avoid breaking schema changes.
- Follow-up: What are backward/forward compatibility rules?
