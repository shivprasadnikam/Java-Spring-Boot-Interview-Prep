# Notification System — Interview Q&A

## Q1. What are the core components of a notification system?

Answer:
- API layer, queue, notification worker, templates, delivery providers.
- Use multi-channel (email/SMS/push) pipelines.
- Avoid synchronous sending in request path.
- Follow-up: How do you handle retries and failures?

## Q2. How do you ensure high deliverability?

Answer:
- Use provider feedback, retries with backoff, and fallback channels.
- Warm up domains and monitor bounce rates.
- Avoid sending from unverified domains.
- Follow-up: How do you handle spam complaints?

## Q3. How do you design for scale (millions of notifications)?

Answer:
- Use queues and horizontal workers.
- Use batching and rate control per provider.
- Avoid per-user synchronous sends.
- Follow-up: How do you throttle per provider limits?

## Q4. How do you handle user preferences and opt-outs?

Answer:
- Store preferences per channel and respect regulatory rules.
- Enforce opt-out at send time and template generation.
- Avoid storing preferences in multiple places.
- Follow-up: How do you ensure GDPR/consent compliance?

## Q5. What is idempotency in notifications?

Answer:
- Ensures the same event doesn’t send duplicate messages.
- Use idempotency keys on events.
- Avoid retry storms causing duplicates.
- Follow-up: How do you handle deduplication at scale?

## Q6. How do you design templates and localization?

Answer:
- Use template store with versioning and locale support.
- Avoid hardcoding content in code.
- Use safe variable substitution and fallback locales.
- Follow-up: How do you test templates safely?

## Q7. How do you handle retries and dead-letter queues?

Answer:
- Use retry topics/queues with backoff.
- Use DLQ for persistent failures with manual review.
- Avoid infinite retries.
- Follow-up: What triggers moving to DLQ?

## Q8. How do you design real-time vs scheduled notifications?

Answer:
- Real-time: event-driven pipeline; scheduled: scheduler + queue.
- Use separate worker pools for scheduled sends.
- Avoid mixing both in the same queue without priorities.
- Follow-up: How do you handle time zones for scheduled notifications?

## Q9. How do you handle provider failures?

Answer:
- Use fallback providers and circuit breakers.
- Queue messages until provider recovery.
- Avoid hard dependency on a single provider.
- Follow-up: How do you monitor provider SLA issues?

## Q10. How do you measure system health and success?

Answer:
- Metrics: delivery rate, bounce rate, latency, retries, DLQ size.
- Use dashboards and alerts.
- Avoid only monitoring queue depth.
- Follow-up: What is your alerting threshold strategy?

## Q11. How do you handle message ordering?

Answer:
- Use per-user ordering if needed via partitioning.
- Avoid global ordering due to scale.
- Follow-up: What if a cancellation message arrives after a send?

## Q12. How do you secure notification pipelines?

Answer:
- Encrypt PII, enforce access controls, secure provider credentials.
- Avoid logging sensitive payloads.
- Follow-up: How do you audit message access?
