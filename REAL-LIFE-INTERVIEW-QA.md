# Real-Life Interview Questions & Answers 🎯
*For 3+ YOE Java Spring Boot Developer targeting 12+ LPA*

## 📅 Timeline: Job Switch by December 1st, 2025

### 🎯 Study Schedule (8 weeks preparation)
- **Week 1-2**: Core Java + Spring Boot fundamentals
- **Week 3-4**: System Design + Database optimization
- **Week 5-6**: Microservices + Performance tuning
- **Week 7-8**: Mock interviews + Coding practice

---

## 🔥 Most Asked Questions in Real Interviews

### **SPRING BOOT CORE (High Priority)**

#### Q1: "Walk me through what happens when a Spring Boot application starts up"
**Answer:**
1. **Main method** calls `SpringApplication.run()`
2. **Environment preparation** - loads application.properties/yml
3. **ApplicationContext creation** - creates appropriate context (web/non-web)
4. **Auto-configuration** - @EnableAutoConfiguration scans classpath and configures beans
5. **Component scanning** - @ComponentScan finds and registers beans
6. **Bean instantiation** - Creates beans in dependency order
7. **Post-processors** run - BeanPostProcessors modify beans
8. **Application ready** - ApplicationReadyEvent fired

*Follow-up: "How would you customize this process?"*
- Custom ApplicationListener
- @PostConstruct methods
- CommandLineRunner/ApplicationRunner

#### Q2: "You have a service that's taking 5 seconds to respond. How do you debug this?"
**Answer:**
```java
// 1. Add logging with timestamps
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        logger.info("Getting user with id: {}", id);
        
        User user = userService.findById(id);
        
        long endTime = System.currentTimeMillis();
        logger.info("User retrieval took: {} ms", (endTime - startTime));
        
        return ResponseEntity.ok(user);
    }
}

// 2. Database query optimization
@Repository
public class UserRepository {
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    User findByIdWithOrders(@Param("id") Long id); // Avoid N+1 problem
}

// 3. Add caching
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}

// 4. Use profiling tools
// - Spring Boot Actuator endpoints
// - Application Performance Monitoring (APM)
// - Database query logs
```

#### Q3: "How do you handle exceptions globally in Spring Boot?"
**Answer:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("ENTITY_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        logger.error("Unexpected error", ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "Something went wrong");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

### **DATABASE & JPA (High Priority)**

#### Q4: "Your application is slow due to N+1 queries. How do you fix this?"
**Answer:**
```java
// Problem: N+1 Query
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

// This causes N+1 queries
List<User> users = userRepository.findAll(); // 1 query
for(User user : users) {
    user.getOrders().size(); // N queries (one for each user)
}

// Solution 1: JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();

// Solution 2: @EntityGraph
@EntityGraph(attributePaths = {"orders"})
@Query("SELECT u FROM User u")
List<User> findAllUsersWithOrders();

// Solution 3: Batch fetching
@BatchSize(size = 10)
@OneToMany(mappedBy = "user")
private List<Order> orders;
```

#### Q5: "How do you handle database transactions in a distributed system?"
**Answer:**
```java
// 1. Declarative transactions
@Service
@Transactional
public class OrderService {
    
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderRequest request) {
        // This method runs in a transaction
        Order order = new Order(request);
        orderRepository.save(order);
        
        // If this fails, entire transaction rolls back
        inventoryService.reduceStock(request.getProductId(), request.getQuantity());
    }
    
    // Read-only transaction for better performance
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}

// 2. Programmatic transactions for complex scenarios
@Service
public class PaymentService {
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    public void processPayment(PaymentRequest request) {
        transactionTemplate.execute(status -> {
            try {
                // Complex business logic
                validatePayment(request);
                chargeCard(request);
                updateOrderStatus(request.getOrderId());
                return null;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new PaymentException("Payment failed", e);
            }
        });
    }
}

// 3. Distributed transactions (Saga pattern)
@Component
public class OrderSagaOrchestrator {
    
    public void processOrder(OrderRequest request) {
        try {
            // Step 1: Create order
            Order order = orderService.createOrder(request);
            
            // Step 2: Reserve inventory
            inventoryService.reserveItems(request.getItems());
            
            // Step 3: Process payment
            paymentService.processPayment(request.getPayment());
            
        } catch (Exception e) {
            // Compensating actions
            compensateOrder(request);
        }
    }
}
```

---

### **MICROSERVICES & SYSTEM DESIGN (High Priority)**

#### Q6: "Design a URL shortener like bit.ly. How would you implement it?"
**Answer:**
```
1. REQUIREMENTS:
   - Shorten long URLs
   - Redirect to original URL
   - 100M URLs per day
   - 100:1 read/write ratio

2. SYSTEM DESIGN:
   ┌─────────────┐    ┌──────────────┐    ┌─────────────┐
   │   Client    │───▶│ Load Balancer│───▶│   API GW    │
   └─────────────┘    └──────────────┘    └─────────────┘
                                                  │
                      ┌─────────────────────────────┼─────────────────────────────┐
                      ▼                             ▼                             ▼
               ┌─────────────┐              ┌─────────────┐              ┌─────────────┐
               │URL Shortener│              │ Analytics   │              │   Cache     │
               │  Service    │              │   Service   │              │  (Redis)    │
               └─────────────┘              └─────────────┘              └─────────────┘
                      │                             │
                      ▼                             ▼
               ┌─────────────┐              ┌─────────────┐
               │  Database   │              │  Analytics  │
               │ (Cassandra) │              │     DB      │
               └─────────────┘              └─────────────┘

3. IMPLEMENTATION:
```

```java
@RestController
public class UrlShortenerController {
    
    @Autowired
    private UrlShortenerService urlService;
    
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(@RequestBody ShortenRequest request) {
        String shortUrl = urlService.shortenUrl(request.getLongUrl());
        return ResponseEntity.ok(new ShortenResponse(shortUrl));
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlService.getLongUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                           .location(URI.create(longUrl))
                           .build();
    }
}

@Service
public class UrlShortenerService {
    
    @Autowired
    private UrlRepository urlRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public String shortenUrl(String longUrl) {
        // Check if URL already exists
        String existingShortCode = urlRepository.findShortCodeByLongUrl(longUrl);
        if (existingShortCode != null) {
            return buildShortUrl(existingShortCode);
        }
        
        // Generate unique short code
        String shortCode = generateShortCode();
        
        // Save to database
        UrlMapping mapping = new UrlMapping(shortCode, longUrl);
        urlRepository.save(mapping);
        
        // Cache for quick access
        redisTemplate.opsForValue().set(shortCode, longUrl, Duration.ofHours(24));
        
        return buildShortUrl(shortCode);
    }
    
    public String getLongUrl(String shortCode) {
        // Try cache first
        String longUrl = redisTemplate.opsForValue().get(shortCode);
        if (longUrl != null) {
            return longUrl;
        }
        
        // Fallback to database
        UrlMapping mapping = urlRepository.findByShortCode(shortCode);
        if (mapping != null) {
            // Update cache
            redisTemplate.opsForValue().set(shortCode, mapping.getLongUrl(), Duration.ofHours(24));
            return mapping.getLongUrl();
        }
        
        throw new UrlNotFoundException("Short URL not found");
    }
    
    private String generateShortCode() {
        // Use counter-based approach for uniqueness
        long counter = getNextCounter();
        return encodeBase62(counter);
    }
}
```

#### Q7: "How do you handle service-to-service communication failures?"
**Answer:**
```java
// 1. Circuit Breaker Pattern
@Component
public class PaymentServiceClient {
    
    private final CircuitBreaker circuitBreaker;
    
    public PaymentServiceClient() {
        this.circuitBreaker = CircuitBreaker.ofDefaults("paymentService");
        circuitBreaker.getEventPublisher()
                     .onStateTransition(event -> 
                         log.info("Circuit breaker state transition: {}", event));
    }
    
    public PaymentResponse processPayment(PaymentRequest request) {
        return circuitBreaker.executeSupplier(() -> {
            // Actual service call
            return restTemplate.postForObject("/payment", request, PaymentResponse.class);
        });
    }
}

// 2. Retry with Exponential Backoff
@Retryable(
    value = {ConnectException.class, SocketTimeoutException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public UserResponse getUserDetails(Long userId) {
    return restTemplate.getForObject("/users/" + userId, UserResponse.class);
}

// 3. Fallback Mechanisms
@Service
public class RecommendationService {
    
    @Autowired
    private ExternalRecommendationClient externalClient;
    
    public List<Product> getRecommendations(Long userId) {
        try {
            return externalClient.getRecommendations(userId);
        } catch (Exception e) {
            log.warn("External recommendation service failed, using fallback", e);
            return getFallbackRecommendations(userId);
        }
    }
    
    private List<Product> getFallbackRecommendations(Long userId) {
        // Return cached recommendations or popular products
        return productService.getPopularProducts();
    }
}

// 4. Async Processing with Message Queues
@RabbitListener(queues = "order.processing")
public void processOrder(OrderMessage orderMessage) {
    try {
        orderService.processOrder(orderMessage);
    } catch (Exception e) {
        log.error("Failed to process order: {}", orderMessage.getOrderId(), e);
        // Send to dead letter queue for manual processing
        rabbitTemplate.convertAndSend("order.dlq", orderMessage);
    }
}
```

---

### **PERFORMANCE & OPTIMIZATION (High Priority)**

#### Q8: "Your API response time increased from 200ms to 2 seconds after a deployment. How do you troubleshoot?"
**Answer:**
```java
// 1. Immediate checks
@RestController
public class HealthController {
    
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Database connectivity
        long dbStart = System.currentTimeMillis();
        boolean dbHealthy = checkDatabaseHealth();
        health.put("database", Map.of(
            "status", dbHealthy ? "UP" : "DOWN",
            "responseTime", System.currentTimeMillis() - dbStart + "ms"
        ));
        
        // External service connectivity
        long extStart = System.currentTimeMillis();
        boolean extHealthy = checkExternalServices();
        health.put("externalServices", Map.of(
            "status", extHealthy ? "UP" : "DOWN",
            "responseTime", System.currentTimeMillis() - extStart + "ms"
        ));
        
        // Memory usage
        Runtime runtime = Runtime.getRuntime();
        health.put("memory", Map.of(
            "used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "MB",
            "free", runtime.freeMemory() / 1024 / 1024 + "MB",
            "total", runtime.totalMemory() / 1024 / 1024 + "MB"
        ));
        
        return ResponseEntity.ok(health);
    }
}

// 2. Performance monitoring
@Component
public class PerformanceInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        if (duration > 1000) { // Log slow requests
            logger.warn("Slow request: {} {} took {}ms", 
                       request.getMethod(), request.getRequestURI(), duration);
        }
    }
}

// 3. Database query optimization
@Repository
public class OptimizedUserRepository {
    
    // Before: N+1 query problem
    // List<User> users = findAll();
    // users.forEach(user -> user.getOrders().size()); // N queries
    
    // After: Single query with JOIN FETCH
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.orders o LEFT JOIN FETCH o.items")
    List<User> findAllWithOrdersAndItems();
    
    // Pagination for large datasets
    @Query("SELECT u FROM User u WHERE u.createdDate >= :date")
    Page<User> findRecentUsers(@Param("date") LocalDateTime date, Pageable pageable);
    
    // Projection for minimal data transfer
    @Query("SELECT new com.example.dto.UserSummary(u.id, u.name, u.email) FROM User u")
    List<UserSummary> findUserSummaries();
}

// 4. Caching strategy
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(cacheConfiguration());
        
        return builder.build();
    }
    
    private RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
```

---

### **BEHAVIORAL QUESTIONS (STAR Method)**

#### Q9: "Tell me about a time when you had to optimize a slow-performing application"
**Answer using STAR method:**

**Situation:** In my previous role, our e-commerce application's product search API was taking 8-10 seconds to respond during peak hours, causing customer complaints and cart abandonment.

**Task:** I was assigned to identify the bottleneck and improve response time to under 500ms while maintaining search accuracy.

**Action:** 
1. **Analysis:** Used APM tools to identify that 80% of time was spent on database queries
2. **Database optimization:** 
   - Added database indexes on frequently searched columns
   - Implemented full-text search using Elasticsearch
   - Optimized N+1 queries using JOIN FETCH
3. **Caching:** Implemented Redis caching for popular search terms
4. **Code optimization:** Used Spring Boot async processing for non-critical operations

**Result:** Reduced average response time from 8 seconds to 300ms (96% improvement). Customer satisfaction increased by 25%, and cart abandonment decreased by 15%.

#### Q10: "Describe a challenging bug you fixed"
**Answer:**

**Situation:** Our payment processing system was randomly failing for about 2% of transactions, but only during high traffic periods. No clear error patterns in logs.

**Task:** Identify root cause and fix the intermittent payment failures without affecting ongoing transactions.

**Action:**
1. **Deep logging:** Added detailed request/response logging with correlation IDs
2. **Thread analysis:** Discovered thread pool exhaustion during peak loads
3. **Configuration tuning:** Increased thread pool size and added proper timeout configurations
4. **Monitoring:** Implemented custom metrics to track thread pool usage

**Result:** Eliminated payment failures completely. Implemented proactive monitoring that alerts before thread pool exhaustion occurs.

---

## 🎯 **SALARY NEGOTIATION TIPS for 12+ LPA**

### **Know Your Worth:**
- Research salary ranges on Glassdoor, PayScale
- 3+ YOE Spring Boot developer: 8-15 LPA range
- Your target of 12+ LPA is reasonable

### **Highlight Your Value:**
- "I've optimized applications reducing response time by 90%"
- "I've designed microservices handling 10K+ requests/minute"
- "I've mentored junior developers and led code reviews"

### **Negotiation Script:**
*"Based on my 3 years of experience with Spring Boot, my track record of performance optimization, and the market rate for similar roles, I'm looking for a package in the range of 12-14 LPA. I'm confident I can bring immediate value to your team."*

---

## 📚 **FINAL PREPARATION CHECKLIST**

### **Week Before Interview:**
- [ ] Practice coding problems on LeetCode (focus on medium difficulty)
- [ ] Review your projects - be ready to explain architecture decisions
- [ ] Prepare questions to ask interviewer
- [ ] Mock interview with friends/colleagues
- [ ] Research the company and their tech stack

### **Day of Interview:**
- [ ] Test your internet/video setup
- [ ] Have your resume and project details ready
- [ ] Prepare pen and paper for problem-solving
- [ ] Stay calm and think out loud during coding

**Remember:** Confidence + Preparation = Success! 🚀
