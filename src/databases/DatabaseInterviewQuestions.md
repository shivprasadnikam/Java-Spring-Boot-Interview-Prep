# Database & JPA Interview Questions 🗄️

## **🗄️ Most Asked Database & JPA Questions (2025 Edition)**

## **JPA/Hibernate Core (High Priority)**

### **Q1: JPA Repositories & CRUD**
**Answer:**
```java
// Basic JPA Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Query methods - Spring Data generates implementation
    List<User> findByName(String name);
    List<User> findByEmailContaining(String email);
    List<User> findByAgeGreaterThan(int age);
    
    // Custom query with @Query
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    // Native query
    @Query(value = "SELECT * FROM users WHERE created_date >= :date", nativeQuery = true)
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    // Modifying query
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    int updateUserStatus(@Param("id") Long id, @Param("active") boolean active);
    
    // Pagination and sorting
    Page<User> findByDepartment(String department, Pageable pageable);
}

// Service layer usage
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<User> getUsers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable);
    }
    
    public User createUser(CreateUserRequest request) {
        User user = new User(request.getName(), request.getEmail());
        return userRepository.save(user); // INSERT
    }
    
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setName(request.getName());
        return userRepository.save(user); // UPDATE
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id); // DELETE
    }
}
```

### **Q2: @Transactional & propagation**
**Answer:**
```java
@Service
public class OrderService {
    
    // Default: REQUIRED propagation
    @Transactional
    public void createOrder(OrderRequest request) {
        // Uses existing transaction or creates new one
        Order order = new Order(request);
        orderRepository.save(order);
        
        // If exception occurs, entire transaction rolls back
        paymentService.processPayment(order);
    }
    
    // REQUIRES_NEW - always creates new transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOrderAttempt(OrderRequest request) {
        // Independent transaction - won't rollback if parent fails
        AuditLog log = new AuditLog("Order attempt", request.getUserId());
        auditRepository.save(log);
    }
    
    // NESTED - creates savepoint
    @Transactional(propagation = Propagation.NESTED)
    public void updateInventory(Long productId, int quantity) {
        // Can rollback to savepoint without affecting parent transaction
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException());
        
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(); // Rolls back to savepoint
        }
        
        product.setQuantity(product.getQuantity() - quantity);
    }
    
    // Read-only transaction
    @Transactional(readOnly = true)
    public List<Order> getOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }
    
    // Custom rollback conditions
    @Transactional(rollbackFor = {BusinessException.class}, 
                   noRollbackFor = {ValidationException.class})
    public void processComplexOrder(OrderRequest request) {
        // Rolls back only for BusinessException
        // ValidationException won't trigger rollback
    }
}

// Propagation types:
// REQUIRED (default) - Use existing or create new
// REQUIRES_NEW - Always create new transaction
// NESTED - Create savepoint in existing transaction
// SUPPORTS - Use existing if present, non-transactional otherwise
// NOT_SUPPORTED - Execute non-transactionally
// NEVER - Throw exception if transaction exists
// MANDATORY - Throw exception if no transaction exists
```

### **Q3: Lazy vs Eager fetching**
**Answer:**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // LAZY (default for @OneToMany) - loaded on demand
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    
    // EAGER - loaded immediately with parent
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;
}

@Service
@Transactional
public class UserService {
    
    public void demonstrateLazyLoading() {
        User user = userRepository.findById(1L).get();
        // Orders not loaded yet - proxy object created
        
        // This triggers lazy loading - separate SQL query
        int orderCount = user.getOrders().size(); // LazyInitializationException if outside transaction
    }
    
    // Solution 1: Use @Transactional to keep session open
    @Transactional(readOnly = true)
    public UserDto getUserWithOrders(Long userId) {
        User user = userRepository.findById(userId).get();
        // Access lazy properties within transaction
        List<OrderDto> orders = user.getOrders().stream()
            .map(OrderDto::from)
            .collect(Collectors.toList());
        
        return new UserDto(user.getName(), orders);
    }
    
    // Solution 2: Use JOIN FETCH to load eagerly
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);
    
    // Solution 3: Use @EntityGraph
    @EntityGraph(attributePaths = {"orders", "department"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithOrdersAndDepartment(@Param("id") Long id);
}
```

### **Q4: N+1 select problem**
**Answer:**
```java
// PROBLEM: N+1 queries
@Entity
public class User {
    @Id
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

// This code causes N+1 queries
List<User> users = userRepository.findAll(); // 1 query
for (User user : users) {
    System.out.println(user.getOrders().size()); // N queries (one per user)
}

// SOLUTION 1: JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();

// SOLUTION 2: @EntityGraph
@EntityGraph(attributePaths = {"orders"})
@Query("SELECT u FROM User u")
List<User> findAllUsersWithOrders();

// SOLUTION 3: @BatchSize
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    @BatchSize(size = 10) // Fetch in batches of 10
    private List<Order> orders;
}

// SOLUTION 4: DTO Projection
@Query("SELECT new com.example.dto.UserOrderCount(u.id, u.name, COUNT(o)) " +
       "FROM User u LEFT JOIN u.orders o GROUP BY u.id, u.name")
List<UserOrderCount> findUserOrderCounts();
```

### **Q5: Hibernate caching (L1/L2)**
**Answer:**
```java
// L1 Cache (Session Cache) - enabled by default
@Service
@Transactional
public class UserService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public void demonstrateL1Cache() {
        // First call - hits database
        User user1 = entityManager.find(User.class, 1L);
        
        // Second call - returns from L1 cache (same session)
        User user2 = entityManager.find(User.class, 1L);
        
        // user1 == user2 (same object reference)
        System.out.println(user1 == user2); // true
        
        // Clear L1 cache
        entityManager.clear();
        
        // This will hit database again
        User user3 = entityManager.find(User.class, 1L);
    }
}

// L2 Cache (SessionFactory Cache) - needs configuration
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    @Id
    private Long id;
    
    private String name;
    private BigDecimal price;
    
    // Cached collection
    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Review> reviews;
}

// Configuration for L2 cache
@Configuration
public class HibernateConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManagerFactory().getObject());
    }
    
    // application.yml
    // spring:
    //   jpa:
    //     properties:
    //       hibernate:
    //         cache:
    //           use_second_level_cache: true
    //           region.factory_class: org.hibernate.cache.ehcache.EhCacheRegionFactory
}

// Query cache
@Repository
public class ProductRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Product> findExpensiveProducts() {
        return entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.price > :price", Product.class)
            .setParameter("price", new BigDecimal("1000"))
            .setHint("org.hibernate.cacheable", true) // Enable query cache
            .getResultList();
    }
}
```

### **Q6: Multiple DB configurations in Spring Boot**
**Answer:**
```java
// Primary database configuration
@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.repository.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {
    
    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource) {
        return builder
            .dataSource(dataSource)
            .packages("com.example.entity.primary")
            .persistenceUnit("primary")
            .build();
    }
    
    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

// Secondary database configuration
@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.repository.secondary",
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager"
)
public class SecondaryDataSourceConfig {
    
    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondaryDataSource") DataSource dataSource) {
        return builder
            .dataSource(dataSource)
            .packages("com.example.entity.secondary")
            .persistenceUnit("secondary")
            .build();
    }
    
    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

// application.yml
spring:
  datasource:
    primary:
      url: jdbc:mysql://localhost:3306/primary_db
      username: user1
      password: pass1
      driver-class-name: com.mysql.cj.jdbc.Driver
    secondary:
      url: jdbc:postgresql://localhost:5432/secondary_db
      username: user2
      password: pass2
      driver-class-name: org.postgresql.Driver

// Usage in services
@Service
public class UserService {
    
    @Autowired
    @Qualifier("primaryTransactionManager")
    private PlatformTransactionManager primaryTxManager;
    
    @Transactional("primaryTransactionManager")
    public void saveUserToPrimary(User user) {
        primaryUserRepository.save(user);
    }
    
    @Transactional("secondaryTransactionManager")
    public void saveUserToSecondary(User user) {
        secondaryUserRepository.save(user);
    }
}
```

### **Q7: JPA Entity Lifecycle and Cascade Types**
**Answer:**
```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Cascade operations to order items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    // Don't cascade to user (managed separately)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Helper methods for bidirectional relationship
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}

// Entity states demonstration
@Service
@Transactional
public class OrderService {
    
    public void demonstrateEntityStates() {
        // 1. NEW/TRANSIENT - not associated with persistence context
        Order order = new Order();
        order.setAmount(100.0);
        
        // 2. MANAGED/PERSISTENT - associated with persistence context
        Order managedOrder = orderRepository.save(order);
        managedOrder.setAmount(150.0); // Will be updated automatically
        
        // 3. DETACHED - was managed but persistence context closed
        // (happens when method ends and @Transactional commits)
        
        // 4. REMOVED - marked for deletion
        orderRepository.delete(managedOrder);
    }
}
```

### **Q8: Spring Data JPA Specifications & Criteria API**
**Answer:**
```java
// Entity
@Entity
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private boolean active;
    private LocalDateTime createdDate;
}

// Repository with Specification support
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
}

// Specifications
public class ProductSpecifications {
    
    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> 
            name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
    
    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> 
            category == null ? null : criteriaBuilder.equal(root.get("category"), category);
    }
    
    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null) return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
        };
    }
    
    public static Specification<Product> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("active"));
    }
    
    public static Specification<Product> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> 
            date == null ? null : criteriaBuilder.greaterThan(root.get("createdDate"), date);
    }
}

// Service using Specifications
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> searchProducts(ProductSearchCriteria criteria, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecifications.isActive())
            .and(ProductSpecifications.hasName(criteria.getName()))
            .and(ProductSpecifications.hasCategory(criteria.getCategory()))
            .and(ProductSpecifications.priceBetween(criteria.getMinPrice(), criteria.getMaxPrice()))
            .and(ProductSpecifications.createdAfter(criteria.getCreatedAfter()));
        
        return productRepository.findAll(spec, pageable);
    }
    
    // Complex specification with joins
    public List<Product> findProductsWithReviews(int minRating) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            Join<Product, Review> reviewJoin = root.join("reviews", JoinType.INNER);
            return criteriaBuilder.greaterThanOrEqualTo(reviewJoin.get("rating"), minRating);
        };
        
        return productRepository.findAll(spec);
    }
}
```

### **Q9: Database Connection Pooling & Performance**
**Answer:**
```java
// HikariCP Configuration (Spring Boot default)
@Configuration
public class DatabaseConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        
        // Connection pool settings
        config.setMaximumPoolSize(20); // Max connections
        config.setMinimumIdle(5); // Min idle connections
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setLeakDetectionThreshold(60000); // 1 minute
        
        // Performance settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        return config;
    }
}

// application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
      pool-name: HikariPool-1
      
  jpa:
    properties:
      hibernate:
        # JDBC batching
        jdbc.batch_size: 25
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
        
        # Query optimization
        generate_statistics: true
        format_sql: true
        use_sql_comments: true
        
        # Connection provider
        connection.provider_disables_autocommit: true

// Monitoring connection pool
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();
            
            return Health.up()
                .withDetail("active", poolBean.getActiveConnections())
                .withDetail("idle", poolBean.getIdleConnections())
                .withDetail("waiting", poolBean.getThreadsAwaitingConnection())
                .withDetail("total", poolBean.getTotalConnections())
                .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

### **Q10: Database Migration with Flyway/Liquibase**
**Answer:**
```java
// Flyway Configuration
@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayConfig {
    
    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .cleanDisabled(true) // Disable clean in production
            .load();
    }
}

// Migration file: V1__Create_user_table.sql
/*
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_date (created_date)
);
*/

// Migration file: V2__Add_user_profile.sql
/*
CREATE TABLE user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    date_of_birth DATE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_profile (user_id)
);
*/

// Conditional migration: V3__Add_audit_columns.sql
/*
ALTER TABLE users 
ADD COLUMN created_by VARCHAR(50),
ADD COLUMN updated_by VARCHAR(50);

UPDATE users SET created_by = 'system', updated_by = 'system' WHERE created_by IS NULL;
*/

// application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
    clean-disabled: true
    schemas: myapp
    table: schema_version
    
// Custom migration callback
@Component
public class FlywayCallback implements Callback {
    
    private static final Logger logger = LoggerFactory.getLogger(FlywayCallback.class);
    
    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_MIGRATE;
    }
    
    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }
    
    @Override
    public void handle(Event event, Context context) {
        logger.info("Migration completed successfully. Current version: {}", 
                   context.getConfiguration().getTarget());
    }
}
```

### **Q11: Transaction Management & Isolation Levels**
        chargeCard(request);
        updateOrderStatus(request.getOrderId());
    }
    
    // Read-only transaction for better performance
    @Transactional(readOnly = true)
    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
    
    // Custom rollback conditions
    @Transactional(rollbackFor = {PaymentException.class, ValidationException.class})
    public void processComplexPayment(PaymentRequest request) {
        // Rolls back only for specified exceptions
    }
    
    // Different propagation levels
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logPaymentAttempt(PaymentRequest request) {
        // Always creates new transaction (independent of caller)
        auditRepository.save(new PaymentAudit(request));
    }
    
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendNotification(String message) {
        // Suspends current transaction
        emailService.send(message);
    }
}

// Programmatic transaction management
@Service
public class OrderService {
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    public void processOrderWithProgrammaticTx(OrderRequest request) {
        transactionTemplate.execute(status -> {
            try {
                Order order = createOrder(request);
                processPayment(order);
                return order;
            } catch (PaymentException e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }
}
```

### **Q4: JPA Locking Strategies**
**Answer:**
```java
@Entity
public class Product {
    @Id
    private Long id;
    
    private String name;
    private Integer quantity;
    
    // Optimistic locking
    @Version
    private Long version;
}

@Repository
public class ProductRepository extends JpaRepository<Product, Long> {
    
    // Pessimistic read lock
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithReadLock(@Param("id") Long id);
    
    // Pessimistic write lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithWriteLock(@Param("id") Long id);
}

@Service
@Transactional
public class InventoryService {
    
    // Optimistic locking example
    public void updateQuantityOptimistic(Long productId, int newQuantity) {
        try {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            
            product.setQuantity(newQuantity);
            productRepository.save(product); // Version check happens here
            
        } catch (OptimisticLockException e) {
            throw new ConcurrentUpdateException("Product was updated by another user");
        }
    }
    
    // Pessimistic locking example
    public void updateQuantityPessimistic(Long productId, int quantityToReduce) {
        Product product = productRepository.findByIdWithWriteLock(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        
        if (product.getQuantity() < quantityToReduce) {
            throw new InsufficientStockException("Not enough stock");
        }
        
        product.setQuantity(product.getQuantity() - quantityToReduce);
        // No need to call save() - managed entity will be updated
    }
}
```

## **Database Performance & Optimization**

### **Q5: Database Connection Pool Configuration**
**Answer:**
```java
// application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
      connection-test-query: SELECT 1

// Custom configuration
@Configuration
public class DatabaseConfig {
    
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Pool sizing
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        
        // Connection management
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        // Monitoring
        config.setLeakDetectionThreshold(60000);
        config.setRegisterMbeans(true);
        
        return new HikariDataSource(config);
    }
    
    // Connection pool monitoring
    @Bean
    public HealthIndicator dbHealthIndicator(DataSource dataSource) {
        return new DataSourceHealthIndicator(dataSource, "SELECT 1");
    }
}
```

### **Q6: Query Optimization Techniques**
**Answer:**
```java
@Repository
public class OptimizedUserRepository {
    
    // Use indexes effectively
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
    // Database index: CREATE INDEX idx_user_email_active ON users(email, active);
    
    // Pagination for large datasets
    @Query("SELECT u FROM User u WHERE u.createdDate >= :date ORDER BY u.createdDate DESC")
    Page<User> findRecentUsers(@Param("date") LocalDateTime date, Pageable pageable);
    
    // Projection to reduce data transfer
    @Query("SELECT new com.example.dto.UserSummary(u.id, u.name, u.email) FROM User u")
    List<UserSummary> findUserSummaries();
    
    // Native query for complex operations
    @Query(value = "SELECT u.*, COUNT(o.id) as order_count " +
                   "FROM users u LEFT JOIN orders o ON u.id = o.user_id " +
                   "WHERE u.created_date >= :date " +
                   "GROUP BY u.id " +
                   "HAVING COUNT(o.id) > :minOrders",
           nativeQuery = true)
    List<Object[]> findActiveUsersWithOrderCount(@Param("date") LocalDateTime date,
                                                @Param("minOrders") int minOrders);
    
    // Batch operations
    @Modifying
    @Query("UPDATE User u SET u.lastLoginDate = :date WHERE u.id IN :userIds")
    int updateLastLoginDate(@Param("userIds") List<Long> userIds, 
                           @Param("date") LocalDateTime date);
}

// Caching strategies
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearUserCache() {
        // Clears all user cache entries
    }
}
```

### **Q7: Database Migration with Flyway**
**Answer:**
```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_users_email ON users(email);

-- V2__Create_orders_table.sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

-- V3__Add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

```java
// application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

## **Advanced Database Concepts**

### **Q8: Handling Database Deadlocks**
**Answer:**
```java
@Service
@Transactional
public class TransferService {
    
    // Potential deadlock scenario
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Always acquire locks in consistent order to prevent deadlock
        Long firstId = Math.min(fromAccountId, toAccountId);
        Long secondId = Math.max(fromAccountId, toAccountId);
        
        Account firstAccount = accountRepository.findByIdWithLock(firstId);
        Account secondAccount = accountRepository.findByIdWithLock(secondId);
        
        Account fromAccount = fromAccountId.equals(firstId) ? firstAccount : secondAccount;
        Account toAccount = toAccountId.equals(firstId) ? firstAccount : secondAccount;
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
    }
    
    // Retry mechanism for deadlock recovery
    @Retryable(
        value = {CannotAcquireLockException.class, DeadlockLoserDataAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void transferMoneyWithRetry(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        transferMoney(fromAccountId, toAccountId, amount);
    }
}
```

### **Q9: Database Partitioning Strategy**
**Answer:**
```sql
-- Horizontal partitioning by date
CREATE TABLE orders_2023 (
    CHECK (created_date >= '2023-01-01' AND created_date < '2024-01-01')
) INHERITS (orders);

CREATE TABLE orders_2024 (
    CHECK (created_date >= '2024-01-01' AND created_date < '2025-01-01')
) INHERITS (orders);

-- Partition function
CREATE OR REPLACE FUNCTION orders_insert_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (NEW.created_date >= '2024-01-01' AND NEW.created_date < '2025-01-01') THEN
        INSERT INTO orders_2024 VALUES (NEW.*);
    ELSIF (NEW.created_date >= '2023-01-01' AND NEW.created_date < '2024-01-01') THEN
        INSERT INTO orders_2023 VALUES (NEW.*);
    ELSE
        RAISE EXCEPTION 'Date out of range. Fix the orders_insert_trigger() function!';
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
```

### **Q10: Database Monitoring and Health Checks**
**Answer:**
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try {
            // Check connection pool
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDS = (HikariDataSource) dataSource;
                HikariPoolMXBean poolBean = hikariDS.getHikariPoolMXBean();
                
                int activeConnections = poolBean.getActiveConnections();
                int totalConnections = poolBean.getTotalConnections();
                
                Health.Builder builder = Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("activeConnections", activeConnections)
                    .withDetail("totalConnections", totalConnections)
                    .withDetail("idleConnections", poolBean.getIdleConnections());
                
                // Warning if connection pool usage > 80%
                if (activeConnections > totalConnections * 0.8) {
                    builder.status("WARNING")
                           .withDetail("warning", "High connection pool usage");
                }
                
                return builder.build();
            }
            
            // Basic connectivity check
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    return Health.up().withDetail("database", "Available").build();
                }
            }
            
        } catch (Exception e) {
            return Health.down(e).withDetail("database", "Unavailable").build();
        }
        
        return Health.unknown().build();
    }
}

// Performance monitoring
@Component
public class DatabaseMetrics {
    
    private final MeterRegistry meterRegistry;
    private final DataSource dataSource;
    
    public DatabaseMetrics(MeterRegistry meterRegistry, DataSource dataSource) {
        this.meterRegistry = meterRegistry;
        this.dataSource = dataSource;
        
        // Register custom metrics
        Gauge.builder("database.connections.active")
             .register(meterRegistry, this, DatabaseMetrics::getActiveConnections);
    }
    
    private double getActiveConnections(DatabaseMetrics metrics) {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            return hikariDS.getHikariPoolMXBean().getActiveConnections();
        }
        return 0;
    }
}
```
