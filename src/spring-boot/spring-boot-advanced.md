# Spring Boot Advanced - Expert Guide

## Table of Contents
- [Auto-configuration Deep Dive](#auto-configuration-deep-dive)
- [Advanced Configuration](#advanced-configuration)
- [Custom Starters](#custom-starters)
- [Actuator & Production-ready Features](#actuator--production-ready-features)
- [Performance Optimization](#performance-optimization)
- [Testing Strategies](#testing-strategies)
- [Deployment & Cloud Native](#deployment--cloud-native)
- [Advanced Security](#advanced-security)
- [Monitoring & Management](#monitoring--management)
- [Best Practices & Anti-patterns](#best-practices--anti-patterns)

## Auto-configuration Deep Dive

### How Auto-configuration Works
- `@SpringBootApplication` breakdown
- `@EnableAutoConfiguration` internals
- `spring.factories` and `META-INF/spring/`
- Conditional annotations (`@ConditionalOn*`)

### Custom Auto-configuration
```java
@Configuration
@ConditionalOnClass(MyService.class)
@EnableConfigurationProperties(MyProperties.class)
public class MyAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyProperties properties) {
        return new MyService(properties);
    }
}
```

## Advanced Configuration

### Profile-based Configuration
- `application-{profile}.properties`
- `@Profile` annotation
- Profile groups
- Profile-specific beans

### Configuration Properties
- Type-safe configuration
- Nested properties
- Validation
- Reloading with `@RefreshScope`

### Externalized Configuration
- Property sources order
- `@PropertySource`
- Vault integration
- Kubernetes ConfigMaps and Secrets

## Custom Starters

### Creating a Custom Starter
1. Create `autoconfigure` module
2. Add `spring-boot-autoconfigure` dependency
3. Create `META-INF/spring.factories`
4. Define `@Configuration` classes
5. Add optional `spring-boot-configuration-processor`

### Best Practices
- Use proper naming conventions
- Provide sensible defaults
- Document configuration properties
- Handle classpath conditions

## Actuator & Production-ready Features

### Key Actuator Endpoints
- `/actuator/health` - Custom health indicators
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment properties
- `/actuator/threaddump` - Thread information
- Custom endpoints with `@Endpoint`

### Health Indicators
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Custom health check logic
        return Health.up()
            .withDetail("custom", "Custom health check")
            .build();
    }
}
```

## Performance Optimization

### Startup Time Optimization
- Lazy initialization
- Component scanning optimization
- Classpath optimization
- AOT (Ahead-of-Time) compilation

### Memory Optimization
- Heap and off-heap memory
- Garbage collection tuning
- Memory leak detection
- Native image with GraalVM

### Database Performance
- Connection pooling (HikariCP)
- JPA/Hibernate optimizations
- Second-level caching
- Read replicas and sharding

## Testing Strategies

### Integration Testing
- `@SpringBootTest`
- Test slices (`@WebMvcTest`, `@DataJpaTest`)
- Test containers
- `@MockBean` and `@SpyBean`

### Performance Testing
- JMeter integration
- Gatling for load testing
- TestContainers for integration tests
- Chaos engineering with Chaos Monkey

## Deployment & Cloud Native

### Containerization
- Multi-stage Docker builds
- Jib for container images
- Best practices for Dockerfiles
- Distroless containers

### Kubernetes Deployment
- Helm charts
- ConfigMaps and Secrets
- Horizontal Pod Autoscaler
- Liveness and Readiness Probes

### Cloud Platforms
- Spring Cloud Kubernetes
- Azure Spring Apps
- AWS EKS with Spring Boot
- GCP Cloud Run

## Advanced Security

### OAuth2 & JWT
- OAuth2 Resource Server
- JWT validation
- Custom token converters
- Keycloak integration

### Method Security
- `@PreAuthorize` and `@PostAuthorize`
- Custom permission evaluators
- Method security expressions
- Reactive method security

## Monitoring & Management

### Distributed Tracing
- Sleuth and Zipkin
- OpenTelemetry
- Custom spans and tags
- Correlation IDs

### Logging
- Structured logging with JSON
- Logback configurations
- Log aggregation
- Correlation IDs in logs

## Best Practices & Anti-patterns

### Do's
- Use constructor injection
- Follow the single responsibility principle
- Implement proper error handling
- Use proper logging levels
- Write comprehensive tests

### Don'ts
- Avoid `@Autowired` on fields
- Don't ignore exceptions
- Avoid circular dependencies
- Don't use `@SpringBootApplication` for library projects

## Interview Questions

### Advanced Topics
1. How does Spring Boot's auto-configuration work under the hood?
2. Explain the difference between `@SpringBootTest` and `@WebMvcTest`.
3. How would you optimize a Spring Boot application for production?

### Performance Tuning
1. What strategies would you use to reduce Spring Boot startup time?
2. How do you identify and fix memory leaks in a Spring Boot application?
3. Explain how you would implement distributed tracing in a microservices architecture.

## Resources
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/actuator-api/html/)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Spring Security](https://spring.io/projects/spring-security)

---
*This guide is designed to help senior Java developers master advanced Spring Boot concepts and prepare for technical interviews at top product-based companies in Pune.*
