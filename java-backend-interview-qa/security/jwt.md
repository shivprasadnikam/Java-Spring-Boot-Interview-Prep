# JWT — Interview Q&A

## Q1. What problems does JWT solve in backend systems?

Answer:
- Stateless auth across distributed services.
- Easy to pass identity/roles across boundaries.
- Use for API authentication and SSO flows.
- Avoid for sessions requiring immediate revocation.
- Follow-up: How do you revoke a JWT effectively?

## Q2. What are the main parts of a JWT?

Answer:
- Header, payload (claims), signature.
- Claims contain subject, roles, expiry, issuer.
- Avoid storing sensitive data in payload (not encrypted).
- Follow-up: What is the difference between JWS and JWE?

## Q3. How do you validate a JWT?

Answer:
- Verify signature, expiry, issuer, and audience.
- Use clock skew tolerance to handle time drift.
- Avoid accepting unsigned tokens.
- Follow-up: How do you handle key rotation?

## Q4. What is the biggest JWT security pitfall?

Answer:
- Long-lived tokens without revocation strategy.
- Use short-lived access tokens + refresh tokens.
- Avoid putting PII in JWT claims.
- Follow-up: Where do you store refresh tokens safely?

## Q5. How do you implement JWT authentication in Spring?

Answer:
- Add a filter that validates JWT and sets `SecurityContext`.
- Use `OncePerRequestFilter` for consistent behavior.
- Avoid storing token parsing logic in controllers.
- Follow-up: How do you handle token parsing errors?

## Q6. What is token refresh and why is it important?

Answer:
- Access tokens expire quickly; refresh token issues a new one.
- Reduces impact of token leakage.
- Avoid long-lived access tokens.
- Follow-up: How do you rotate refresh tokens?

## Q7. JWT vs opaque tokens: when do you choose which?

Answer:
- JWT: self-contained, no DB lookup; good for stateless auth.
- Opaque: requires lookup; better for immediate revocation.
- Use opaque tokens when you need fine-grained revocation.
- Follow-up: How do you support token introspection?

## Q8. How do you store JWT on the client side?

Answer:
- Prefer HttpOnly cookies for browsers, secure storage for mobile.
- Avoid localStorage for sensitive tokens (XSS risk).
- Follow-up: How do you protect against CSRF with cookies?

## Q9. What is the impact of large JWTs?

Answer:
- Larger headers increase latency and bandwidth.
- Use minimal claims; avoid storing bulky data.
- Follow-up: How do you compress or slim JWT payloads?

## Q10. What are common JWT interview traps?

Answer:
- Confusing JWT with encryption (its signed, not encrypted).
- Assuming JWT solves logout automatically.
- Avoid treating JWT as a session store.
- Follow-up: How do you implement logout in a JWT-based system?

## Q11. How does clock skew affect JWT validation?

Answer:
- Slight time differences can cause false expirations.
- Use leeway in validation.
- Avoid large leeway windows (security risk).
- Follow-up: How do you handle clock drift in distributed systems?

## Q12. How do you rotate JWT signing keys safely?

Answer:
- Use `kid` header to identify key version.
- Support multiple keys during rotation window.
- Avoid hardcoding keys in code.
- Follow-up: How do you distribute keys across services?
