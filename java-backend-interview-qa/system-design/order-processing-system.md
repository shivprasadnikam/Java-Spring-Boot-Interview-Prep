# Order Processing System — Interview Q&A

## Q1. What are the core components of an order processing system?

Answer:
- Order API, inventory, payment, fulfillment, and notification services.
- Use event-driven pipeline for state transitions.
- Avoid tightly coupled synchronous calls for every step.
- Follow-up: How do you model the order state machine?

## Q2. How do you ensure order consistency across services?

Answer:
- Use Saga pattern with compensating actions.
- Use outbox to publish state changes reliably.
- Avoid distributed transactions across services.
- Follow-up: How do you handle payment failure rollback?

## Q3. How do you handle idempotency for order creation?

Answer:
- Use idempotency keys per client request.
- Deduplicate requests at API layer.
- Avoid duplicate orders on retries.
- Follow-up: How do you handle retries from payment gateway?

## Q4. How do you handle inventory reservation?

Answer:
- Reserve stock with timeout and release on failure.
- Use optimistic locking to prevent over-selling.
- Avoid hard locks for long-running checkout.
- Follow-up: How do you handle race conditions on stock updates?

## Q5. How do you process payments safely?

Answer:
- Use payment intents and async confirmation.
- Store payment status and retry safely.
- Avoid synchronous payment blocking order pipeline.
- Follow-up: How do you handle partial payments or retries?

## Q6. How do you design order status transitions?

Answer:
- Use a state machine: CREATED -> PAID -> SHIPPED -> DELIVERED.
- Enforce valid transitions at service layer.
- Avoid ad-hoc status updates without validation.
- Follow-up: How do you handle cancellation at each stage?

## Q7. How do you handle high traffic spikes (sales events)?

Answer:
- Use queueing for non-critical steps and rate limit order creation.
- Pre-scale inventory and order services.
- Avoid synchronous fan-out in peak loads.
- Follow-up: How do you prevent overselling in flash sales?

## Q8. How do you implement auditing and traceability?

Answer:
- Store order events and state transitions.
- Use correlation IDs and event logs.
- Avoid relying only on current order state.
- Follow-up: How do you reconstruct order history?

## Q9. How do you handle refunds and chargebacks?

Answer:
- Model refunds as a separate workflow with approvals.
- Use idempotent refund requests.
- Avoid merging refund logic into main order flow.
- Follow-up: How do you handle partial refunds?

## Q10. How do you manage delivery and fulfillment failures?

Answer:
- Use retries and fallback logistics providers.
- Maintain order status with failure reasons.
- Avoid silent failures without alerts.
- Follow-up: How do you handle re-shipment scenarios?

## Q11. How do you handle data consistency between order and inventory services?

Answer:
- Use events and reconciliation jobs.
- Avoid direct DB joins across services.
- Follow-up: How do you reconcile mismatched inventory?

## Q12. What metrics do you track for order systems?

Answer:
- Order success rate, payment failures, inventory holds, fulfillment latency.
- Use alerts on drops in conversion.
- Avoid tracking only average latency.
- Follow-up: How do you detect fraud or abuse?
