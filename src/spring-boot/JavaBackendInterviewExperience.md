# Java Backend Developer Interview Experience

## 📝 Round 1 – Project & Technical Questions

### 1. Introduce Yourself
```
[Your introduction should include:
- Current role and experience
- Key technologies you're proficient in
- Major projects you've worked on
- Your interest in the role you're interviewing for]
```

### 2. Project Overview
```
[Prepare a 2-3 minute summary of your most relevant project including:
- Project purpose and business value
- Technical stack used
- Your specific contributions
- Challenges faced and how you overcame them]
```

### 3. REST API Endpoint
```java
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
}
```

### 4. JPA and Hibernate
**JPA (Java Persistence API)** is a Java specification for ORM that standardizes how Java applications interact with databases. It provides:
- Object-relational mapping
- JPQL (Java Persistence Query Language)
- Entity relationships
- Caching

**Hibernate** is the most popular JPA implementation that provides:
- Automatic table creation
- Lazy loading
- Caching mechanisms
- Database vendor independence

### 5. OneToMany and ManyToOne Relationship
```java
// Department.java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
}

// Employee.java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
```

### 6. Fetch in JPA
- **FetchType.LAZY**: Loads the relationship only when explicitly accessed
- **FetchType.EAGER**: Loads the relationship immediately with the parent entity

### 7. Cascade in JPA
Cascade defines how operations on the parent entity propagate to child entities:
- **CascadeType.ALL**: All operations cascade
- **CascadeType.PERSIST**: Save operation cascades
- **CascadeType.MERGE**: Update operation cascades
- **CascadeType.REMOVE**: Delete operation cascades
- **CascadeType.REFRESH**: Refresh operation cascades

### 8. JWT Token
A JSON Web Token (JWT) is a compact, URL-safe means of representing claims between two parties. It consists of three parts:
- Header (algorithm and token type)
- Payload (claims)
- Signature

### 9. JWT Implementation
```java
// Add dependencies to pom.xml
// implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
// implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
// implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'

@Service
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

### 10. @Transactional
`@Transactional` is a Spring annotation that defines the scope of a single database transaction. It ensures that all database operations within the method are executed within the same transaction.

### 11. Propagation in Transactions
- **REQUIRED**: Default. Uses existing transaction or creates new if none exists
- **REQUIRES_NEW**: Always creates a new transaction
- **SUPPORTS**: Executes non-transactionally unless a transaction exists
- **NOT_SUPPORTED**: Executes non-transactionally, suspending any existing transaction
- **MANDATORY**: Must be called within an existing transaction
- **NEVER**: Must not be called with a transaction
- **NESTED**: Executes within a nested transaction if a transaction exists

## 🔍 Round 1 – Scenario-Based Questions

### 12. Custom JPA Query
```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    @Query("SELECT e FROM Employee e WHERE e.age > :age AND e.salary > :salary")
    List<Employee> findByAgeAndSalaryGreaterThan(
        @Param("age") int age, 
        @Param("salary") double salary
    );
}
```

### 13. Transactional Behavior
Yes, within the same transaction, you can get the updated value in the next line because:
- The transaction hasn't been committed yet
- The persistence context is aware of the changes
- The updated entity is managed by the persistence context

## 💻 Round 1 – Java Concepts & Programs

### 14. Streams in Java
Streams in Java 8+ provide a functional approach to process collections of objects. Key features:
- Not a data structure
- Doesn't change the original data source
- Lazy execution
- Can be processed in parallel

### 15. Functional Interface
An interface with exactly one abstract method. Examples:
- `Runnable`
- `Callable`
- `Comparator`
- `Predicate`
- `Function`
- `Supplier`
- `Consumer`

### 16. Find 2nd Max Without Streams
```java
public static int findSecondMax(int[] arr) {
    if (arr.length < 2) {
        throw new IllegalArgumentException("Array must have at least 2 elements");
    }
    
    int first = Integer.MIN_VALUE;
    int second = Integer.MIN_VALUE;
    
    for (int num : arr) {
        if (num > first) {
            second = first;
            first = num;
        } else if (num > second && num != first) {
            second = num;
        }
    }
    
    return second;
}
```

### 17. Find Students with Marks > 80
```java
List<Student> topStudents = students.stream()
    .filter(student -> student.getMarks() > 80)
    .collect(Collectors.toList());
```

### 18. Max Marks Using sorted()
```java
Optional<Student> topStudent = students.stream()
    .sorted((s1, s2) -> Integer.compare(s2.getMarks(), s1.getMarks()))
    .findFirst();
```

### 19. Max Marks Using max()
```java
Optional<Student> topStudent = students.stream()
    .max(Comparator.comparingInt(Student::getMarks));

// Or using method reference
OptionalInt maxMarks = students.stream()
    .mapToInt(Student::getMarks)
    .max();
```

## 🌐 Round 2 – Microservices & Advanced Concepts

### 20. Microservices Communication
- **Synchronous**: REST, gRPC, GraphQL
- **Asynchronous**: Message queues (RabbitMQ, Kafka), Event Sourcing
- **Service Discovery**: Eureka, Consul, Zookeeper
- **API Gateway**: Spring Cloud Gateway, Zuul

### 21. Alternatives to RestTemplate
- **WebClient**: Non-blocking, reactive client
- **Feign Client**: Declarative REST client
- **gRPC**: High-performance RPC framework
- **GraphQL**: Query language for APIs
- **RSocket**: Reactive streams over the network

### 22. Sync vs Async Communication
**Synchronous**:
- Request/response model
- Client waits for response
- Simpler error handling
- Tight coupling

**Asynchronous**:
- Event-driven
- No waiting
- Better scalability
- Loose coupling
- More complex error handling

### 23. RabbitMQ Working
RabbitMQ is a message broker that implements AMQP protocol:
1. **Producer** sends messages to an **Exchange**
2. Exchange routes messages to **Queues** based on bindings and routing keys
3. **Consumers** receive messages from queues
4. Messages can be acknowledged (ack) or rejected (nack)

### 24. Production Issue Troubleshooting
1. **Reproduce**: Try to reproduce the issue in lower environments
2. **Logs**: Check application logs (ELK, Splunk, CloudWatch)
3. **Metrics**: Monitor application metrics (Prometheus, Grafana)
4. **APM**: Use APM tools (New Relic, AppDynamics)
5. **Debugging**: Add debug logs or use remote debugging
6. **Rollback**: If needed, rollback to previous version
7. **Root Cause**: Identify root cause and implement fix
8. **Post-mortem**: Document the issue and solution

### 25. SLF4J Framework
Simple Logging Facade for Java (SLF4J) is an abstraction for various logging frameworks. Usage:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
    
    public void myMethod() {
        logger.debug("Debug message");
        logger.info("Info message");
        logger.error("Error message", exception);
    }
}
```

### 26. JWT Token (Repeated from earlier)
[See answer in section 8]

### 27. Authentication vs Authorization
- **Authentication**: Verifying who someone is (e.g., username/password)
- **Authorization**: Verifying what specific resources a user can access

### 28. HTTP Status Codes
- **401 Unauthorized**: Authentication failed or not provided
- **403 Forbidden**: Authenticated but not authorized to access the resource

### 29. Order of Status Codes
**401 Unauthorized** occurs before **403 Forbidden** because:
1. First, the system checks if the user is authenticated (401)
2. Then, it checks if the authenticated user has permission (403)
