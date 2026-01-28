# REST Exception Handling — Interview Q&A

## Q1. Why is centralized exception handling important?

Answer:
- Ensures consistent error responses and better client experience.
- Avoids leaking internal stack traces.
- Use `@ControllerAdvice` for global handlers.
- Avoid try/catch in every controller method.
- Follow-up: How do you structure a standard error response?

## Q2. How do you map exceptions to HTTP codes in Spring?

Answer:
- Use `@ExceptionHandler` with specific exceptions.
- Use `ResponseStatusException` or `@ResponseStatus` for simple cases.
- Avoid mapping too many exceptions to 500.
- Follow-up: How do you handle validation exceptions?

## Q3. What is the difference between checked and unchecked exceptions in APIs?

Answer:
- Checked exceptions force handling but add noise.
- Unchecked exceptions are preferred in Spring services.
- Use custom runtime exceptions with meaningful codes.
- Avoid generic `RuntimeException` without context.
- Follow-up: How do you expose error codes to clients?

## Q4. How do you handle validation errors cleanly?

Answer:
- Use `@Valid` with `MethodArgumentNotValidException` handler.
- Return field-level errors with code + message.
- Avoid exposing internal validation rules directly.
- Follow-up: How do you support localization of error messages?

## Q5. How do you handle downstream service failures?

Answer:
- Map to 503 or 502 based on context.
- Use circuit breakers to avoid cascading failures.
- Avoid exposing downstream exceptions to clients.
- Follow-up: How do you log correlation IDs for such failures?

## Q6. What is a problem details (RFC 7807) response?

Answer:
- Standardized error format with `type`, `title`, `status`, `detail`.
- Use for consistent client parsing.
- Avoid custom error schemas without documentation.
- Follow-up: How do you implement RFC 7807 in Spring?

## Q7. How do you handle exceptions in async controllers?

Answer:
- Use `@ExceptionHandler` with `CompletableFuture` or reactive handlers.
- Map exceptions in completion stage.
- Avoid letting futures complete exceptionally without mapping.
- Follow-up: How do you set timeouts for async requests?

## Q8. Whats the risk of returning raw exception messages?

Answer:
- Leaks internal details and security information.
- Exposes stack traces and class names.
- Use user-friendly error messages and stable codes.
- Follow-up: How do you log detailed errors securely?

## Q9. How do you handle 404 for invalid resource IDs?

Answer:
- Throw a domain-specific NotFound exception.
- Map to 404 with meaningful message.
- Avoid returning 200 with null payload.
- Follow-up: How do you avoid timing attacks in auth errors?

## Q10. What is the role of global filters/interceptors in error handling?

Answer:
- They add correlation IDs, logs, and audit info.
- Use filters for cross-cutting error context.
- Avoid heavy logic in filters; keep them minimal.
- Follow-up: How do you add trace IDs to every error response?

## Q11. How do you handle serialization errors?

Answer:
- Catch `HttpMessageNotReadableException` and return 400.
- Provide a clear error message on invalid JSON.
- Avoid swallowing serialization issues; clients need feedback.
- Follow-up: How do you limit request payload size?

## Q12. How do you test your exception handlers?

Answer:
- Use `@WebMvcTest` to trigger handlers with mocked services.
- Verify HTTP code and error body structure.
- Avoid testing only the happy path.
- Follow-up: How do you test global handlers in integration tests?
