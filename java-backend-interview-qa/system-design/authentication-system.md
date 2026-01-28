# Authentication System — Interview Q&A

## Q1. What are the core components of an auth system?

Answer:
- User store, auth service, token service, and session management.
- Use centralized identity provider for scale.
- Avoid mixing auth logic across services.
- Follow-up: How do you separate auth and user profile data?

## Q2. Session-based vs token-based auth: when to use each?

Answer:
- Session-based: server state, good for web apps.
- Token-based: stateless, good for APIs and mobile.
- Avoid sessions in microservices without shared store.
- Follow-up: How do you handle logout with JWT?

## Q3. How do you store passwords securely?

Answer:
- Use bcrypt/argon2 with proper cost factors.
- Never store plain passwords.
- Avoid using SHA or MD5.
- Follow-up: How do you handle password resets securely?

## Q4. What is MFA and when do you require it?

Answer:
- Adds second factor (OTP, authenticator app).
- Use for high-risk or admin access.
- Avoid forcing MFA for low-risk use cases without UX consideration.
- Follow-up: How do you handle MFA recovery flows?

## Q5. How do you design token expiration and refresh?

Answer:
- Short-lived access tokens, longer refresh tokens.
- Use rotation and revocation lists.
- Avoid long-lived access tokens.
- Follow-up: How do you detect refresh token reuse?

## Q6. How do you prevent brute-force attacks?

Answer:
- Rate limit login attempts, add CAPTCHA, lockout policies.
- Use anomaly detection for suspicious IPs.
- Avoid weak password policies.
- Follow-up: How do you balance security and user experience?

## Q7. How do you manage user sessions across devices?

Answer:
- Track sessions per device with metadata.
- Allow session revocation by user.
- Avoid global logout unless required.
- Follow-up: How do you implement session revocation at scale?

## Q8. What is OAuth2 and where does it fit?

Answer:
- Authorization framework for delegated access.
- Use for third-party integrations and SSO.
- Avoid using OAuth2 when simple login is sufficient.
- Follow-up: What is the difference between OAuth2 and OpenID Connect?

## Q9. How do you handle account lockouts and recovery?

Answer:
- Lock on repeated failures, provide secure recovery flow.
- Use audit logs for suspicious activity.
- Avoid permanent lockouts without recovery.
- Follow-up: How do you protect recovery flows from abuse?

## Q10. How do you secure auth APIs?

Answer:
- TLS everywhere, input validation, and rate limits.
- Use WAF for public endpoints.
- Avoid leaking error details in login responses.
- Follow-up: How do you prevent user enumeration?

## Q11. How do you handle social login securely?

Answer:
- Validate tokens with providers and map identities carefully.
- Store provider IDs to prevent account hijacking.
- Avoid trusting client-provided tokens without verification.
- Follow-up: How do you handle account linking?

## Q12. What metrics do you track in auth systems?

Answer:
- Login success rate, token refresh failures, MFA adoption.
- Monitor suspicious IP and brute-force attempts.
- Avoid ignoring failed login spikes.
- Follow-up: How do you build alerting for auth abuse?
