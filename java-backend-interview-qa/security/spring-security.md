# Spring Security — Interview Q&A

## Q1. What are the core components of Spring Security?

Answer:
- Filter chain, authentication manager, security context, and access decision voters.
- Filters intercept requests before controllers.
- Use to enforce auth/authz centrally.
- Avoid adding custom security logic in controllers.
- Follow-up: Where does `SecurityContext` get stored by default?

## Q2. Authentication vs authorization: how do you explain it cleanly?

Answer:
- Authentication = who you are; authorization = what you can do.
- Use authentication for identity, authorization for access rules.
- Avoid mixing both in the same service layer logic.
- Follow-up: How does Spring separate these two concerns?

## Q3. How does the Spring Security filter chain work?

Answer:
- A chain of filters processes a request before reaching controllers.
- Filters handle auth, CSRF, session management, exception handling.
- Use custom filters for token auth (JWT).
- Avoid placing custom filters after `ExceptionTranslationFilter`.
- Follow-up: How do you control filter order?

## Q4. Why is stateless authentication common in microservices?

Answer:
- No server-side session storage; scales easily.
- Works well with API gateways and multiple instances.
- Use JWT or opaque tokens with centralized auth.
- Avoid storing auth state in memory (breaks horizontal scaling).
- Follow-up: How do you revoke tokens in a stateless system?

## Q5. What is CSRF and when do you enable it?

Answer:
- CSRF exploits authenticated browser sessions.
- Enable for browser-based apps with cookies.
- Disable for stateless APIs that use tokens.
- Avoid disabling CSRF without understanding the client model.
- Follow-up: How does CSRF protection work under the hood?

## Q6. What is method-level security and when is it useful?

Answer:
- Use `@PreAuthorize` / `@PostAuthorize` for fine-grained access.
- Useful when endpoint-level checks are not enough.
- Avoid putting complex authorization logic in annotations.
- Follow-up: Whats the difference between `@Secured` and `@PreAuthorize`?

## Q7. How do you implement role-based access control (RBAC)?

Answer:
- Map roles to authorities and check them in security rules.
- Store roles in token claims or DB.
- Avoid over-granular roles; maintainability suffers.
- Follow-up: How do you handle permission-based access?

## Q8. What is `UserDetailsService` and when do you use it?

Answer:
- Loads user details during authentication.
- Use for DB-backed authentication systems.
- Avoid putting business logic in `UserDetailsService`.
- Follow-up: How do you cache user details safely?

## Q9. How do you handle password storage securely?

Answer:
- Use bcrypt/argon2 with appropriate strength.
- Never store plain text passwords.
- Use `PasswordEncoder` in Spring.
- Follow-up: How do you migrate hashing algorithms?

## Q10. What is the difference between OAuth2 and JWT in Spring?

Answer:
- OAuth2 is an authorization framework; JWT is a token format.
- Use OAuth2 for delegated access, JWT for stateless tokens.
- Avoid treating JWT as OAuth2 replacement.
- Follow-up: How does Spring Security handle OAuth2 resource servers?

## Q11. How do you secure actuator endpoints?

Answer:
- Expose only needed endpoints and secure with roles.
- Use `management.endpoints.web.exposure.include` carefully.
- Avoid exposing `/env` or `/beans` in production publicly.
- Follow-up: How do you configure separate management port?

## Q12. What are common security misconfigurations in Spring apps?

Answer:
- Permit-all rules in prod, missing CSRF in browser apps.
- Overly broad CORS configuration.
- Avoid leaking stack traces and debug endpoints.
- Follow-up: How do you implement secure CORS rules?
