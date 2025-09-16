# Spring Security & OAuth2 - Complete Guide

## Table of Contents
- [Core Concepts](#core-concepts)
- [Authentication](#authentication)
- [Authorization](#authorization)
- [OAuth2 & OIDC](#oauth2--oidc)
- [JWT (JSON Web Tokens)](#jwt-json-web-tokens)
- [Method Security](#method-security)
- [CSRF & CORS](#csrf--cors)
- [Security Headers](#security-headers)
- [Testing Security](#testing-security)
- [Best Practices](#best-practices)
- [Interview Questions](#interview-questions)

## Core Concepts

### Security Filters
- Filter Chain
- SecurityContext
- Authentication vs Authorization
- SecurityContextHolder

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## Authentication

### In-Memory Authentication
```java
@Bean
public UserDetailsService users() {
    UserDetails user = User.builder()
        .username("user")
        .password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cUUubQS5d1bprCh/EA0bK8YJWBCW.")
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
}
```

### Database Authentication
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            getAuthorities(user.getRoles())
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
    }
}
```

### Custom Authentication Provider
```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        UserDetails user = userDetailsService.loadUserByUsername(username);
        
        if (passwordEncoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                username, 
                password, 
                user.getAuthorities()
            );
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

## Authorization

### URL-based Authorization
```java
http
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
        .requestMatchers("/public/**").permitAll()
        .anyRequest().authenticated()
    );
```

### Method Security
```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
    // Configuration
}

@Service
public class ProductService {
    
    @PreAuthorize("hasRole('ADMIN') or #product.owner == authentication.name")
    public void updateProduct(Product product) {
        // Update product
    }
    
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<Product> getAllProducts() {
        // Return products
    }
}
```

### Permission-based Access Control
```java
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Override
    public boolean hasPermission(
        Authentication authentication, 
        Object targetDomainObject, 
        Object permission
    ) {
        // Custom permission logic
        return true;
    }
    
    @Override
    public boolean hasPermission(
        Authentication authentication, 
        Serializable targetId, 
        String targetType, 
        Object permission
    ) {
        // Custom permission logic
        return true;
    }
}
```

## OAuth2 & OIDC

### OAuth2 Client Configuration
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-client-id
            client-secret: your-client-secret
            scope: email, profile
```

### Resource Server Configuration
```java
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                )
            );
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("https://your-issuer/.well-known/jwks.json").build();
    }
}
```

## JWT (JSON Web Tokens)

### JWT Authentication Filter
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### JWT Token Provider
```java
@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;
    
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        return Jwts.builder()
            .setSubject(Long.toString(userPrincipal.getId()))
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
            
        return Long.parseLong(claims.getSubject());
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | 
                UnsupportedJwtException | IllegalArgumentException ex) {
            // Log exception
            return false;
        }
    }
}
```

## CSRF & CORS

### CSRF Protection
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/public/**")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        );
    return http.build();
}
```

### CORS Configuration
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Security Headers

### Default Security Headers
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; script-src 'self'")
            )
            .frameOptions(frame -> frame
                .sameOrigin()
            )
            .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000)
            )
            .xssProtection(xss -> xss
                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
            )
            .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
        );
    return http.build();
}
```

## Testing Security

### Security Test Annotations
```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "USER")
    public void whenUserAccessUserEndpoint_thenOk() throws Exception {
        mockMvc.perform(get("/api/user"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void whenUserAccessAdminEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/admin"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithAnonymousUser
    public void whenAnonymousAccessLogin_thenOk() throws Exception {
        mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"user\",\"password\":\"password\"}"))
            .andExpect(status().isOk());
    }
}
```

## Best Practices

### Do's
- Use HTTPS in production
- Implement proper password hashing (BCrypt, Argon2, PBKDF2)
- Use secure session management
- Implement proper error handling
- Keep dependencies updated
- Use Content Security Policy (CSP)
- Implement rate limiting
- Use secure headers
- Log security events
- Regular security audits

### Don'ts
- Don't expose sensitive information in logs
- Don't store plain text passwords
- Don't use deprecated algorithms
- Don't disable CSRF without good reason
- Don't expose stack traces to users
- Don't use hardcoded secrets
- Don't ignore security warnings

## Interview Questions

### Basic
1. What is the difference between authentication and authorization?
2. Explain the Spring Security filter chain.
3. What is CSRF and how do you prevent it in Spring Security?

### Advanced
1. How does OAuth2 work with Spring Security?
2. Explain JWT and its advantages over session-based authentication.
3. How would you implement role-based and permission-based access control?

### Practical
1. How would you secure a REST API with JWT?
2. Design an authentication system with social login (Google, Facebook).
3. How would you implement multi-factor authentication?

## Resources
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [OAuth 2.0 and OIDC](https://oauth.net/2/)
- [JWT.io](https://jwt.io/)
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)

---
*This guide is designed to help senior Java developers master Spring Security, OAuth2, and JWT for technical interviews at top product-based companies in Pune.*
