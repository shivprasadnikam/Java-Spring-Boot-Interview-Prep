# Microservices Interview Questions 🏗️

## **🌐 Most Asked Microservices Questions (2025 Edition)**

## **1. Microservices Fundamentals**

### **Q1: What are Microservices and when to use them?**
**Answer:**
- **Microservices** are small, independent services that work together, each running in its own process
- **When to use**:
  - Large, complex applications needing frequent updates
  - Need for independent scaling of components
  - Multiple teams working on different features
  - Need for technology diversity

### **Q2: Microservices vs Monolithic Architecture**
**Comparison:**
| Aspect | Monolithic | Microservices |
|--------|------------|---------------|
| Development | Single codebase | Multiple services |
| Deployment | Single unit | Independent |
| Scaling | Vertical | Horizontal |
| Technology | Single stack | Polyglot |
| Fault Isolation | Poor | Excellent |
| Complexity | Lower initial | Higher operational |

## **2. Service Communication**

### **Q3: Synchronous vs Asynchronous Communication**
**Answer:**
```java
// Synchronous (REST/HTTP)
@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/orders/{id}")
    Order getOrder(@PathVariable Long id);
}

// Asynchronous (Message Queue)
@RabbitListener(queues = "order.queue")
public void processOrder(Order order) {
    // Process order asynchronously
}
```

### **Q4: Service Discovery in Microservices**
**Answer:**
- **Eureka Server** (Netflix OSS)
```java
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
```
- **Service Registration**
```yaml
# application.yml
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
  instance:
    preferIpAddress: true
```

## **3. API Gateway & Load Balancing**

### **Q5: Spring Cloud Gateway Configuration**
**Answer:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: productCircuitBreaker
                fallbackUri: forward:/fallback/product
```

## **4. Distributed Tracing & Monitoring**

### **Q6: Implement Distributed Tracing with Sleuth & Zipkin**
**Answer:**
1. **Add Dependencies**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

2. **Configure Zipkin**
```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1.0  # 100% of traces are sent to Zipkin
```

## **5. Circuit Breaker & Resilience**

### **Q7: Implement Circuit Breaker with Resilience4j**
**Answer:**
```java
@Service
public class OrderService {
    private final CircuitBreaker circuitBreaker;
    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.circuitBreaker = CircuitBreaker.ofDefaults("orderService");
    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrderFallback")
    public Order getOrder(Long orderId) {
        return restTemplate.getForObject(
            "http://order-service/orders/" + orderId, 
            Order.class
        );
    }

    private Order getOrderFallback(Long orderId, Exception e) {
        return new Order(orderId, "Fallback Order", 0.0);
    }
}
```

## **6. Containerization & Orchestration**

### **Q8: Dockerize a Spring Boot Application**
**Dockerfile:**
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  order-service:
    build: ./order-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: order_db
    ports:
      - "3306:3306"

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
```

## **7. Security in Microservices**

### **Q9: Implement JWT Authentication**
**Security Configuration:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            
        http.addFilterBefore(jwtAuthenticationFilter(), 
            UsernamePasswordAuthenticationFilter.class);
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
```

## **8. Best Practices**

### **Q10: Microservices Best Practices**
**Answer:**
1. **Design for Failure**
   - Implement circuit breakers
   - Use bulkheads pattern
   - Implement retry mechanisms

2. **API Design**
   - Use RESTful principles
   - Version your APIs
   - Implement HATEOAS

3. **Data Management**
   - Database per service
   - Use Saga pattern for distributed transactions
   - Implement CQRS when needed

4. **Monitoring & Logging**
   - Centralized logging (ELK Stack)
   - Distributed tracing
   - Health checks

5. **Security**
   - API Gateway authentication
   - Service-to-service authentication
   - Secrets management

6. **Deployment**
   - Containerization (Docker)
   - Orchestration (Kubernetes)
   - CI/CD pipelines

## **9. Real-world Scenarios**

### **Q11: How would you design Twitter?**
**Approach:**
1. **Service Decomposition**
   - User Service
   - Tweet Service
   - Feed Service
   - Social Graph Service
   - Notification Service

2. **Data Storage**
   - Users: SQL database
   - Tweets: NoSQL (Cassandra)
   - Social Graph: Graph database (Neo4j)
   - Media: Object storage (S3)

3. **Caching Strategy**
   - Redis for feed caching
   - CDN for media files

4. **Scaling**
   - Sharding for user data
   - Read replicas for high read throughput
   - Queue-based processing for fan-out

## **10. Common Interview Questions**

### **Q12: How do you handle distributed transactions?**
**Answer:**
1. **Saga Pattern**
   - Choreography-based
   - Orchestration-based

2. **Two-Phase Commit (2PC)**
   - Prepare phase
   - Commit/Rollback phase

3. **Event Sourcing**
   - Store state changes as events
   - Rebuild state by replaying events

### **Q13: How do you ensure data consistency?**
**Answer:**
- **Eventual Consistency**
- **Compensating Transactions**
- **Idempotent Operations**
- **Message Deduplication**

### **Q14: How do you handle service versioning?**
**Answer:**
1. **URL Versioning**
   ```
   /api/v1/users
   /api/v2/users
   ```

2. **Header Versioning**
   ```
   Accept: application/vnd.company.app-v1+json
   ```

3. **Content Negotiation**
   ```
   /users?version=1
   ```

4. **API Gateway Routing**
   - Route based on headers/path
   - Transform requests/responses

## **Conclusion**

Preparing for microservices interviews requires understanding both theoretical concepts and practical implementations. Focus on:
- Service decomposition
- Communication patterns
- Data consistency
- Fault tolerance
- Security
- Monitoring and observability
- Containerization and orchestration

Remember to practice explaining your design decisions and be prepared to discuss trade-offs in your architectural choices.
