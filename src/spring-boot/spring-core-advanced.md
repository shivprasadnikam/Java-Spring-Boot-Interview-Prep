# Spring Framework Core - Advanced Interview Guide

## 1. Core Container & IoC

### 1.1 Bean Lifecycle
```java
public class CustomBean implements InitializingBean, DisposableBean, 
        BeanNameAware, BeanFactoryAware, ApplicationContextAware {
    
    @Override
    public void setBeanName(String name) {
        // 1. BeanNameAware callback
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 2. BeanFactoryAware callback
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // 3. ApplicationContextAware callback
    }
    
    @PostConstruct
    public void init() {
        // 4. @PostConstruct callback
    }
    
    @Override
    public void afterPropertiesSet() {
        // 5. InitializingBean callback
    }
    
    @PreDestroy
    public void preDestroy() {
        // 6. @PreDestroy callback
    }
    
    @Override
    public void destroy() {
        // 7. DisposableBean callback
    }
}
```

### 1.2 Bean Scopes
| Scope | Description | Use Case |
|-------|-------------|----------|
| singleton | Single instance per container (default) | Stateless services, repositories |
| prototype | New instance per request | Stateful beans, request processing |
| request | Single instance per HTTP request | Web applications |
| session | Single instance per HTTP session | User session data |
| application | Single instance per ServletContext | Application-wide data |
| websocket | Single instance per WebSocket session | WebSocket connections |

### 1.3 Advanced Dependency Injection

#### Constructor Injection (Recommended)
```java
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    // Implicit @Autowired since Spring 4.3
    public OrderService(PaymentService paymentService, 
                       InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }
}
```

#### Method Injection with @Lookup
```java
public abstract class OrderProcessor {
    
    public void processOrder(Order order) {
        // Get a new prototype bean instance
        OrderValidator validator = createOrderValidator();
        validator.validate(order);
    }
    
    @Lookup
    protected abstract OrderValidator createOrderValidator();
}
```

## 2. Advanced Configuration

### 2.1 Conditional Bean Registration
```java
@Configuration
public class AppConfig {
    
    @Bean
    @ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
    
    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.create(factory);
    }
}
```

### 2.2 Environment Profiles
```java
@Configuration
@Profile("production")
public class ProductionConfig {
    
    @Bean
    public DataSource dataSource() {
        // Production data source
    }
}

@Configuration
@Profile("!production")
public class DevelopmentConfig {
    
    @Bean
    public DataSource dataSource() {
        // Development data source
    }
}

// Activate profiles
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles("production");
        app.run(args);
    }
}
```

## 3. AOP (Aspect-Oriented Programming)

### 3.1 Custom Aspect Example
```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before " + joinPoint.getSignature().getName());
    }
    
    @AfterReturning(
        pointcut = "execution(* com.example.service.*.*(..))",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("After returning " + joinPoint.getSignature().getName() + 
                         " with result " + result);
    }
    
    @Around("execution(* com.example.service.*.*(..))")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        long endTime = System.currentTimeMillis();
        
        System.out.println("Method " + pjp.getSignature().getName() + 
                         " executed in " + (endTime - startTime) + "ms");
        return result;
    }
    
    @AfterThrowing(
        pointcut = "execution(* com.example.service.*.*(..))",
        throwing = "ex"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        System.out.println("Exception in " + joinPoint.getSignature().getName() + 
                         " with message: " + ex.getMessage());
    }
}
```

### 3.2 AOP Best Practices
1. **Use specific pointcuts**: Narrow down join points
2. **Order aspects properly**: Use @Order annotation
3. **Avoid circular dependencies**: Don't advise methods called from within the same class
4. **Consider performance**: Pointcut matching has overhead
5. **Use @AspectJ syntax**: More powerful than XML configuration

## 4. Transaction Management

### 4.1 Declarative Transactions
```java
@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    
    public OrderService(OrderRepository orderRepository, 
                       PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }
    
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.DEFAULT,
        timeout = 30,
        readOnly = false,
        rollbackFor = {PaymentException.class},
        noRollbackFor = {NotificationException.class}
    )
    public Order createOrder(Order order) {
        // Transactional method
        order = orderRepository.save(order);
        paymentService.processPayment(order);
        return order;
    }
    
    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
```

### 4.2 Programmatic Transactions
```java
@Service
public class OrderService {
    
    private final PlatformTransactionManager transactionManager;
    private final OrderRepository orderRepository;
    
    public OrderService(PlatformTransactionManager transactionManager,
                       OrderRepository orderRepository) {
        this.transactionManager = transactionManager;
        this.orderRepository = orderRepository;
    }
    
    public Order createOrder(Order order) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        transactionTemplate.setTimeout(30);
        
        return transactionTemplate.execute(status -> {
            try {
                Order savedOrder = orderRepository.save(order);
                // Additional operations
                return savedOrder;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException("Transaction failed", e);
            }
        });
    }
}
```

## 5. Spring Expression Language (SpEL)

### 5.1 Common Use Cases
```java
@Component
public class SpELExamples {
    
    // Literal expressions
    @Value("#{'Hello ' + 'World'}")
    private String greeting;
    
    // Method invocation
    @Value("#{T(java.lang.Math).random() * 100}")
    private double randomNumber;
    
    // Collection selection
    @Value("#{users.![name]}")
    private List<String> userNames;
    
    // Conditional expressions
    @Value("#{systemProperties['user.country'] == 'US' ? 'USD' : 'EUR'}")
    private String currency;
    
    // Safe navigation operator
    @Value("#{user?.address?.city}")
    private String userCity;
    
    // Collection filtering
    public List<User> getAdmins(List<User> users) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("filter(role == 'ADMIN')");
        return (List<User>) exp.getValue(users);
    }
}
```

## 6. Spring's Resource Abstraction

### 6.1 Resource Loading
```java
@Service
public class ResourceService {
    
    private final ResourceLoader resourceLoader;
    
    public ResourceService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public void processResource() throws IOException {
        // Load from classpath
        Resource classpathResource = resourceLoader.getResource("classpath:config.properties");
        
        // Load from file system
        Resource fileResource = resourceLoader.getResource("file:/path/to/file.txt");
        
        // Load from URL
        Resource urlResource = resourceLoader.getResource("https://example.com/api/data");
        
        // Read resource content
        try (InputStream is = classpathResource.getInputStream()) {
            // Process the input stream
        }
    }
}
```

## 7. Event Handling

### 7.1 Custom Events
```java
// 1. Define custom event
public class OrderCreatedEvent extends ApplicationEvent {
    private final Order order;
    
    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }
    
    public Order getOrder() {
        return order;
    }
}

// 2. Publish event
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    
    @Transactional
    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        
        // Publish custom event
        eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder));
        
        return savedOrder;
    }
}

// 3. Listen to event
@Component
public class OrderCreatedEventListener {
    
    @EventListener
    @Async
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        // Async processing of the event
        Order order = event.getOrder();
        // Send notification, update metrics, etc.
    }
    
    @EventListener(condition = "#event.order.amount > 1000")
    public void handleLargeOrder(OrderCreatedEvent event) {
        // Handle only large orders
    }
}
```

## 8. Type Conversion and Validation

### 8.1 Custom Converters
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToOrderStatusConverter());
        registry.addConverter(new OrderStatusToStringConverter());
    }
}

public class StringToOrderStatusConverter implements Converter<String, OrderStatus> {
    @Override
    public OrderStatus convert(String source) {
        return OrderStatus.valueOf(source.toUpperCase());
    }
}

// Usage in controller
@GetMapping("/orders")
public List<Order> getOrdersByStatus(@RequestParam("status") OrderStatus status) {
    return orderService.findByStatus(status);
}
```

### 8.2 Bean Validation
```java
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody OrderRequest request) {
        // Process valid request
    }
    
    @GetMapping("/{id}")
    public Order getOrder(
            @PathVariable @Min(1) Long id,
            @RequestParam @Pattern(regexp = "^[A-Z]{2}-\\d{4}$") String code) {
        return orderService.getOrder(id, code);
    }
}

// Custom validator
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidOrderStatusValidator.class)
public @interface ValidOrderStatus {
    String message() default "Invalid order status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class ValidOrderStatusValidator implements ConstraintValidator<ValidOrderStatus, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            OrderStatus status = OrderStatus.valueOf(value.toUpperCase());
            return status != OrderStatus.UNKNOWN;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
```

## 9. Testing

### 9.1 Integration Testing
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase
public class OrderServiceIntegrationTest {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void testCreateOrder() {
        // Given
        Customer customer = new Customer("John Doe", "john@example.com");
        entityManager.persist(customer);
        
        OrderRequest request = new OrderRequest(customer.getId(), 
            List.of("item1", "item2"));
        
        // When
        Order order = orderService.createOrder(request);
        
        // Then
        assertNotNull(order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(2, order.getItems().size());
    }
    
    @Test
    public void testCreateOrderWithInvalidCustomer() {
        // Given
        OrderRequest request = new OrderRequest(999L, List.of("item1"));
        
        // When/Then
        assertThrows(CustomerNotFoundException.class, () -> {
            orderService.createOrder(request);
        });
    }
}
```

## 10. Performance Considerations

### 10.1 Bean Scoping
- Use prototype scope for stateful beans
- Be cautious with session/request scoped beans in non-web contexts
- Consider thread-safety for singleton beans

### 10.2 Lazy Initialization
```java
@Configuration
@Lazy // All beans in this configuration will be lazily initialized
public class LazyConfig {
    
    @Bean
    @Lazy(false) // Eager initialization despite class-level @Lazy
    public EagerBean eagerBean() {
        return new EagerBean();
    }
}

// application.properties
spring.main.lazy-initialization=true
```

### 10.3 Caching
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "orders");
    }
}

@Service
public class ProductService {
    
    @Cacheable("products")
    public Product getProduct(Long id) {
        // Expensive operation
        return productRepository.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "products", key = "#product.id")
    public void updateProduct(Product product) {
        productRepository.save(product);
    }
    
    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        // Method to clear the entire cache
    }
}
```

## 11. Common Interview Questions

### Q1: Explain the difference between @Component, @Service, @Repository, and @Controller annotations.
- **@Component**: Generic stereotype for any Spring-managed component
- **@Service**: Specialization of @Component for service layer
- **@Repository**: Specialization of @Component for data access layer, enables exception translation
- **@Controller**: Specialization of @Component for web controllers

### Q2: What is the difference between @Autowired and @Resource?
- **@Autowired**: Spring-specific, by type, can be combined with @Qualifier
- **@Resource**: JSR-250 standard, by name, then by type

### Q3: How does Spring handle circular dependencies?
- Constructor injection doesn't support circular dependencies
- Setter/field injection uses a three-level cache of bean instances
- Best practice: Refactor to avoid circular dependencies

### Q4: Explain the difference between @Transactional and @Transactional(propagation = Propagation.REQUIRES_NEW)
- **@Transactional**: Joins existing transaction or creates a new one if none exists
- **@Transactional(propagation = Propagation.REQUIRES_NEW)**: Always creates a new transaction, suspending the current one if it exists

### Q5: What is the difference between @Mock and @MockBean?
- **@Mock**: From Mockito, creates a mock object
- **@MockBean**: From Spring Boot, creates a mock and adds/replaces it in the application context

## 12. Best Practices

1. **Use constructor injection** for required dependencies
2. **Avoid field injection** for better testability
3. **Keep controllers thin** by delegating business logic to services
4. **Use appropriate transaction boundaries** at the service layer
5. **Leverage Spring's exception handling** with @ControllerAdvice
6. **Profile your application** to identify performance bottlenecks
7. **Use Spring Boot's auto-configuration** but know how to customize it
8. **Keep configuration externalized** using application.properties/yml
9. **Write integration tests** for critical paths
10. **Monitor your application** with Spring Boot Actuator
