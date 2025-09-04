# Spring Boot Core Interview Questions 🍃

## **🌱 Most Asked Spring Boot Questions (2025 Edition)**

## **Spring Boot Fundamentals (High Priority)**

### **Q1: What happens during Spring Boot application startup?**
**Answer:**
1. **SpringApplication.run()** called
2. **Environment preparation** - loads properties files
3. **ApplicationContext creation** - web or non-web context
4. **Auto-configuration** - @EnableAutoConfiguration magic
5. **Component scanning** - finds @Component, @Service, etc.
6. **Bean creation** - instantiates beans in dependency order
7. **Post-processors** - BeanPostProcessors modify beans
8. **Application ready** - fires ApplicationReadyEvent

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MyApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setAdditionalProfiles("dev");
        app.run(args);
    }
}
```

### **Q2: Explain Dependency Injection in Spring Boot**
**Answer:**
```java
// Constructor injection (recommended)
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    public OrderService(PaymentService paymentService, 
                       InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }
}

// Field injection (not recommended)
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService; // Hard to test
}

// Setter injection (rarely used)
@Service
public class OrderService {
    private PaymentService paymentService;
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

### **Q3: Bean scopes in Spring Boot**
**Answer:**
```java
// Singleton (default) - one instance per container
@Component
@Scope("singleton")
public class DatabaseConnection {
    // Shared instance
}

// Prototype - new instance every time
@Component
@Scope("prototype")
public class RequestProcessor {
    // New instance for each injection
}

// Request scope (web applications)
@Component
@Scope("request")
public class UserContext {
    // One instance per HTTP request
}

// Session scope (web applications)
@Component
@Scope("session")
public class ShoppingCart {
    // One instance per HTTP session
}

// Custom scope
@Component
@Scope("thread")
public class ThreadLocalService {
    // Custom scope implementation
}
```

### **Q4: @Component vs @Service vs @Repository vs @Controller**
**Answer:**
```java
// @Component - Generic stereotype annotation
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email.contains("@");
    }
}

// @Service - Business logic layer
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(CreateUserRequest request) {
        // Business logic here
        return userRepository.save(new User(request));
    }
}

// @Repository - Data access layer
@Repository
public class UserRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    public User findByEmail(String email) {
        return entityManager.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class)
            .setParameter("email", email)
            .getSingleResult();
    }
}

// @Controller - Web layer (returns view names)
@Controller
public class UserController {
    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users"; // Returns view name
    }
}

// @RestController = @Controller + @ResponseBody
@RestController
public class UserRestController {
    @GetMapping("/api/users")
    public List<User> getUsers() {
        return userService.getAllUsers(); // Returns JSON
    }
}
```

**Key differences:**
- **@Component**: Generic stereotype, base for other annotations
- **@Service**: Business logic, transaction boundaries
- **@Repository**: Data access, exception translation
- **@Controller**: Web layer, returns view names
- **@RestController**: REST APIs, returns JSON/XML

### **Q5: Auto-configuration – how it works**
**Answer:**
```java
// Spring Boot checks classpath and creates beans automatically
// Example: If H2 is on classpath, creates DataSource bean

@Configuration
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url")
    public DataSource dataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create()
            .url(properties.getUrl())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .build();
    }
}

// Custom auto-configuration
@Configuration
@ConditionalOnClass(MyService.class)
@ConditionalOnProperty(name = "myservice.enabled", havingValue = "true")
public class MyServiceAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```

## **Configuration & Properties**

### **Q6: IoC & Dependency Injection (constructor/setter/field)**
**Answer:**
```java
// Constructor injection (RECOMMENDED)
@Service
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    // Single constructor - @Autowired optional
    public OrderService(PaymentService paymentService, InventoryService inventoryService) {
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }
    
    // Benefits: Immutable, fail-fast, easy testing
}

// Field injection (NOT RECOMMENDED)
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService; // Hard to test, mutable
    
    @Autowired
    private InventoryService inventoryService;
}

// Setter injection (RARELY USED)
@Service
public class OrderService {
    private PaymentService paymentService;
    private InventoryService inventoryService;
    
    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @Autowired
    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
}

// Handling circular dependencies
@Service
public class ServiceA {
    private final ServiceB serviceB;
    
    public ServiceA(@Lazy ServiceB serviceB) { // Break circular dependency
        this.serviceB = serviceB;
    }
}
```

**Best practices:**
- Use constructor injection for mandatory dependencies
- Use setter injection for optional dependencies
- Avoid field injection in production code

### **Q7: @Autowired & circular dependencies**
**Answer:**
```java
// Circular dependency problem
@Service
public class ServiceA {
    @Autowired
    private ServiceB serviceB; // ServiceB depends on ServiceA
}

@Service
public class ServiceB {
    @Autowired
    private ServiceA serviceA; // Creates circular dependency
}

// Solution 1: Use @Lazy
@Service
public class ServiceA {
    private final ServiceB serviceB;
    
    public ServiceA(@Lazy ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}

// Solution 2: Use setter injection
@Service
public class ServiceA {
    private ServiceB serviceB;
    
    @Autowired
    public void setServiceB(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}

// Solution 3: Refactor design (BEST)
@Service
public class ServiceA {
    private final CommonService commonService;
    
    public ServiceA(CommonService commonService) {
        this.commonService = commonService;
    }
}

@Service
public class ServiceB {
    private final CommonService commonService;
    
    public ServiceB(CommonService commonService) {
        this.commonService = commonService;
    }
}
```

### **Q8: Bean scopes (singleton, prototype, request, session)**
**Answer:**
```java
// Singleton (default) - one instance per container
@Component
@Scope("singleton") // or @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DatabaseConnection {
    // Shared instance across application
}

// Prototype - new instance every time
@Component
@Scope("prototype") // or @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RequestProcessor {
    // New instance for each injection
}

// Request scope (web applications)
@Component
@Scope("request") // or @Scope(WebApplicationContext.SCOPE_REQUEST)
public class UserContext {
    // One instance per HTTP request
}

// Session scope (web applications)
@Component
@Scope("session") // or @Scope(WebApplicationContext.SCOPE_SESSION)
public class ShoppingCart {
    // One instance per HTTP session
}

// Custom scope with proxy
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedService {
    // Proxy created for injection into singleton beans
}
```

### **Q9: Profiles (@Profile)**
**Answer:**
```java
// Profile-specific beans
@Configuration
@Profile("dev")
public class DevConfiguration {
    @Bean
    public DataSource dataSource() {
        return new H2DataSource(); // In-memory for development
    }
}

@Configuration
@Profile("prod")
public class ProdConfiguration {
    @Bean
    public DataSource dataSource() {
        return new MySQLDataSource(); // Production database
    }
}

// Multiple profiles
@Service
@Profile({"dev", "test"})
public class MockEmailService implements EmailService {
    public void sendEmail(String to, String subject, String body) {
        System.out.println("Mock email sent to: " + to);
    }
}

// Exclude profiles
@Service
@Profile("!prod") // Active in all profiles except prod
public class DebugService {
    // Debug functionality
}

// Programmatic profile activation
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles("dev", "mysql");
        app.run(args);
    }
}
```

### **Q10: CommandLineRunner vs ApplicationRunner**
**Answer:**
```java
// CommandLineRunner - receives raw String arguments
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunner executed with args: " + Arrays.toString(args));
        
        // Initialize data, setup caches, etc.
        if (args.length > 0 && "--init-data".equals(args[0])) {
            initializeDatabase();
        }
    }
    
    private void initializeDatabase() {
        // Database initialization logic
    }
}

// ApplicationRunner - receives parsed ApplicationArguments
@Component
public class ConfigurationValidator implements ApplicationRunner {
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationRunner executed");
        
        // Access parsed arguments
        if (args.containsOption("validate-config")) {
            validateConfiguration();
        }
        
        // Access non-option arguments
        List<String> nonOptionArgs = args.getNonOptionArgs();
        System.out.println("Non-option args: " + nonOptionArgs);
    }
    
    private void validateConfiguration() {
        // Configuration validation logic
    }
}

// Order of execution
@Component
@Order(1) // Executes first
public class FirstRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("First runner");
    }
}

@Component
@Order(2) // Executes second
public class SecondRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Second runner");
    }
}
```

**Key differences:**
- **CommandLineRunner**: Raw String[] arguments, simpler
- **ApplicationRunner**: Parsed ApplicationArguments, more features
- **Use CommandLineRunner for**: Simple argument processing
- **Use ApplicationRunner for**: Complex argument parsing with options

### **Q11: Different ways to configure Spring Boot application**
**Answer:**
```java
// 1. application.properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
logging.level.com.example=DEBUG

// 2. application.yml (preferred)
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: user
    password: password
logging:
  level:
    com.example: DEBUG

// 3. @ConfigurationProperties
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
    private String name;
    private String version;
    private Database database = new Database();
    
    // getters and setters
    
    public static class Database {
        private String url;
        private int maxConnections = 10;
        // getters and setters
    }
}

// 4. Environment-specific properties
// application-dev.yml, application-prod.yml, application-test.yml

// 5. Command line arguments
java -jar app.jar --server.port=9090 --spring.profiles.active=prod
```

### **Q12: Profile-based configuration**
**Answer:**
```java
// Activate profiles
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev,mysql");
        SpringApplication.run(Application.class, args);
    }
}

// Profile-specific beans
@Configuration
@Profile("dev")
public class DevConfiguration {
    @Bean
    public DataSource dataSource() {
        return new H2DataSource(); // In-memory for development
    }
}

@Configuration
@Profile("prod")
public class ProdConfiguration {
    @Bean
    public DataSource dataSource() {
        return new MySQLDataSource(); // Production database
    }
}

// Conditional on profile
@Service
@Profile("!prod") // Not in production
public class MockEmailService implements EmailService {
    public void sendEmail(String to, String subject, String body) {
        System.out.println("Mock email sent to: " + to);
    }
}
```

## **REST API Development**

### **Q13: @Controller vs @RestController**
**Answer:**
```java
// @Controller - Traditional MVC, returns view names
@Controller
public class UserController {
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users"; // Returns view name (users.html/jsp)
    }
    
    @PostMapping("/users")
    public String createUser(@ModelAttribute CreateUserRequest request, 
                           RedirectAttributes redirectAttributes) {
        User user = userService.createUser(request);
        redirectAttributes.addFlashAttribute("message", "User created successfully");
        return "redirect:/users"; // Redirect to users page
    }
    
    // For AJAX requests, need @ResponseBody
    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getUsersJson() {
        return userService.getAllUsers(); // Returns JSON
    }
}

// @RestController = @Controller + @ResponseBody
@RestController
@RequestMapping("/api/v1")
public class UserRestController {
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // Returns JSON automatically
    }
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.getId())
            .toUri();
        return ResponseEntity.created(location).body(user);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = new ErrorResponse("Validation failed", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
```

**Key differences:**
- **@Controller**: Returns view names, used with template engines (Thymeleaf, JSP)
- **@RestController**: Returns data (JSON/XML), used for REST APIs
- **@RestController** = **@Controller** + **@ResponseBody** on every method

### **Q14: REST Controller best practices**
**Answer:**
```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> users = userService.findUsers(search, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable @Min(1) Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(user.getId())
            .toUri();
        return ResponseEntity.created(location).body(user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

### **Q15: Global exception handling with @ControllerAdvice**
**Answer:**
```java
// Global exception handler
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Entity Not Found")
            .message(ex.getMessage())
            .path(getCurrentPath())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input parameters")
            .validationErrors(errors)
            .path(getCurrentPath())
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(getCurrentPath())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// Custom exceptions
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
```

## **Validation**

### **Q16: Checked vs Unchecked exceptions**
**Answer:**
```java
// Checked exceptions - must be handled or declared
public class UserService {
    
    // Method declares checked exception
    public User findUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }
    
    // Caller must handle checked exception
    public void processUser(Long userId) {
        try {
            User user = findUserById(userId); // Must handle or declare
            // Process user
        } catch (UserNotFoundException e) {
            log.error("User processing failed", e);
            throw new ProcessingException("Failed to process user", e);
        }
    }
}

// Unchecked exceptions - runtime exceptions
public class OrderService {
    
    public void createOrder(OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null"); // Unchecked
        }
        
        if (request.getAmount() <= 0) {
            throw new BusinessValidationException("Order amount must be positive"); // Unchecked
        }
        
        // No need to declare these exceptions
    }
}

// Custom exceptions
public class UserNotFoundException extends Exception { // Checked
    public UserNotFoundException(String message) {
        super(message);
    }
}

public class BusinessValidationException extends RuntimeException { // Unchecked
    public BusinessValidationException(String message) {
        super(message);
    }
}

// Spring Boot exception handling
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class) // Checked exception
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BusinessValidationException.class) // Unchecked exception
    public ResponseEntity<ErrorResponse> handleBusinessValidation(BusinessValidationException ex) {
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
```

**Key differences:**
- **Checked**: Must be handled or declared, compile-time checking
- **Unchecked**: Optional handling, runtime exceptions
- **Use checked for**: Recoverable conditions, expected failures
- **Use unchecked for**: Programming errors, business rule violations

### **Q17: Bean validation in Spring Boot**
**Answer:**
```java
// Request DTO with validation
public class CreateUserRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must be less than 120")
    private Integer age;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String phone;
    
    @Valid
    @NotNull(message = "Address is required")
    private AddressDto address;
    
    // getters and setters
}

// Nested validation
public class AddressDto {
    
    @NotBlank(message = "Street is required")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid ZIP code")
    private String zipCode;
    
    // getters and setters
}

// Custom validation
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) return true; // Let @NotBlank handle null
        return !userRepository.existsByEmail(email);
    }
}
```

### **Q18: Spring Boot Testing**
**Answer:**
```java
// Unit test for service layer
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
        User user = new User("John", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // When
        UserDto result = userService.createUser(request);
        
        // Then
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(emailService).sendWelcomeEmail("john@example.com");
    }
}

// Integration test for web layer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldCreateUserAndReturnCreated() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
        
        // When
        ResponseEntity<UserDto> response = restTemplate.postForEntity(
            "/api/v1/users", request, UserDto.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("John");
        assertThat(userRepository.count()).isEqualTo(1);
    }
}

// Web layer test with MockMvc
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUserWhenFound() throws Exception {
        // Given
        UserDto user = new UserDto(1L, "John", "john@example.com");
        when(userService.findById(1L)).thenReturn(user);
        
        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
```
