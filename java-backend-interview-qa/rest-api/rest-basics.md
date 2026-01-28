# REST API Basics — Interview Q&A

## Q1. What makes an API truly RESTful in practice?

Answer:
- Resource-based URIs, proper HTTP methods, statelessness.
- Use standard status codes and content negotiation.
- Avoid RPC-style endpoints like `/doPayment`.
- Follow-up: What is HATEOAS and do you use it?

## Q2. Idempotency: which HTTP methods are idempotent and why care?

Answer:
- GET, PUT, DELETE are idempotent by definition; POST is not.
- Idempotency prevents duplicate effects on retries.
- Use idempotency keys for POST in payment/order APIs.
- Avoid non-idempotent writes without retry strategy.
- Follow-up: How do you design idempotent create endpoints?

## Q3. How do you design resource URIs for nested resources?

Answer:
- Use `/orders/{id}/items` for child resources.
- Use query params for filtering, not for identity.
- Avoid deeply nested paths that imply tight coupling.
- Follow-up: When should you expose a sub-resource vs separate top-level resource?

## Q4. What is content negotiation and when is it useful?

Answer:
- Server chooses response format based on `Accept` header.
- Use when supporting multiple representations (JSON, XML).
- Avoid if you only ever use JSON; keep it simple.
- Follow-up: How does Spring handle content negotiation by default?

## Q5. How do you version REST APIs?

Answer:
- Common: URI versioning (`/v1`), header-based, or media types.
- Use URI versioning for clarity and caching.
- Avoid versioning for every small change; prefer backward compatibility.
- Follow-up: How do you sunset old versions?

## Q6. What are typical REST anti-patterns?

Answer:
- Using verbs in URIs, ignoring status codes, overusing POST.
- Returning 200 for all errors.
- Avoid leaking internal exceptions to clients.
- Follow-up: What is a proper error response format?

## Q7. How do you design pagination for large datasets?

Answer:
- Use cursor-based pagination for stable ordering.
- Use offset for simple admin views.
- Avoid offset for large tables due to performance issues.
- Follow-up: How do you encode and validate cursors?

## Q8. How do you handle partial updates?

Answer:
- Use PATCH with JSON Merge Patch or JSON Patch.
- Use PUT for full replacements.
- Avoid PATCH without clear contract; clients will break.
- Follow-up: How do you validate partial updates in Spring?

## Q9. When do you use synchronous vs async APIs?

Answer:
- Sync for quick operations; async for long-running tasks.
- Use 202 Accepted with a job resource for async.
- Avoid blocking request threads for heavy jobs.
- Follow-up: How do you communicate job status?

## Q10. What is the role of DTOs in REST APIs?

Answer:
- DTOs decouple API contracts from persistence models.
- Use DTOs to shape payloads and avoid entity leakage.
- Avoid returning entities directly; it causes tight coupling.
- Follow-up: How do you map DTOs efficiently?

## Q11. How do you secure REST endpoints beyond auth?

Answer:
- Validate inputs, limit payload sizes, use rate limiting.
- Use proper error handling to avoid info leakage.
- Avoid exposing stack traces.
- Follow-up: How do you implement rate limiting in Spring?

## Q12. What is ETag and when would you use it?

Answer:
- ETag enables caching and optimistic concurrency control.
- Use for GET caching or conditional updates (`If-Match`).
- Avoid if you cannot compute stable resource versions.
- Follow-up: How does ETag help prevent lost updates?
