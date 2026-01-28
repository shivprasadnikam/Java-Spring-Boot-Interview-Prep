# HTTP Status Codes — Interview Q&A

## Q1. 200 vs 201: when should you use 201?

Answer:
- Use 201 Created when a new resource is created.
- Include `Location` header with new URI.
- Avoid 200 for creates; it hides semantics.
- Follow-up: When would you use 202 instead?

## Q2. 400 vs 422: what is the practical difference?

Answer:
- 400: malformed request or invalid syntax.
- 422: well-formed but semantic validation failed.
- Use 422 for domain validation errors.
- Avoid mixing both for the same error type.
- Follow-up: How do you structure validation error responses?

## Q3. 401 vs 403: how do you explain it?

Answer:
- 401: unauthenticated (missing/invalid credentials).
- 403: authenticated but not authorized.
- Use 401 to trigger login/refresh.
- Avoid returning 403 when token is missing.
- Follow-up: What is the correct response for expired token?

## Q4. When should you return 404 vs 204?

Answer:
- 404: resource not found.
- 204: success with no content (common on deletes).
- Avoid 204 for missing resources; it hides errors.
- Follow-up: Should delete on missing resource be 404 or 204?

## Q5. 409 Conflict: where is it useful?

Answer:
- Use for optimistic lock failures or duplicate constraints.
- Example: duplicate email registration.
- Avoid 409 for generic validation errors.
- Follow-up: How do you map database unique violations to 409?

## Q6. 429 Too Many Requests: when should it appear?

Answer:
- Use when rate limiting is enforced.
- Provide `Retry-After` header.
- Avoid generic 503 for throttling.
- Follow-up: How do you implement rate limiting in Spring?

## Q7. 500 vs 503: how to choose?

Answer:
- 500: unexpected server error.
- 503: service unavailable (overload/maintenance).
- Use 503 for downstream dependency failures or overload.
- Avoid leaking stack traces.
- Follow-up: Should you retry 500 or 503?

## Q8. When is 304 Not Modified relevant?

Answer:
- Use with ETag/If-None-Match to reduce bandwidth.
- Useful for GET-heavy read APIs.
- Avoid if resources are highly dynamic or non-cacheable.
- Follow-up: How do you generate ETags efficiently?

## Q9. What is a proper response for validation failures?

Answer:
- 400 or 422 with structured error details.
- Include field-level errors and a stable error code.
- Avoid plain text error bodies.
- Follow-up: How should clients localize error messages?

## Q10. What status code do you return for async processing?

Answer:
- 202 Accepted with a job URI.
- Use when processing will happen later.
- Avoid blocking the client until completion.
- Follow-up: How does the client track job status?

## Q11. What status code for partial success?

Answer:
- 207 Multi-Status (WebDAV) or 200 with per-item status in body.
- Use 207 for batch operations with mixed results.
- Avoid 200 without indicating failed items.
- Follow-up: How do you design batch error responses?

## Q12. How do you handle redirection in APIs?

Answer:
- Prefer 301/302 for permanent/temporary redirects.
- In APIs, redirect usage is rare; better to update clients.
- Avoid redirects for JSON APIs unless absolutely required.
- Follow-up: How does caching behave with 301 vs 302?
