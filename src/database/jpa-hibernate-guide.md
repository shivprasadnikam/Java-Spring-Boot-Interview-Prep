# JPA & Hibernate - Complete Guide

## Table of Contents
- [Core Concepts](#core-concepts)
- [Entity Mapping](#entity-mapping)
- [Relationships](#relationships)
- [Inheritance Strategies](#inheritance-strategies)
- [Querying Data](#querying-data)
- [Transactions & Concurrency](#transactions--concurrency)
- [Caching](#caching)
- [Performance Optimization](#performance-optimization)
- [Advanced Topics](#advanced-topics)
- [Best Practices](#best-practices)
- [Interview Questions](#interview-questions)

## Core Concepts

### JPA vs Hibernate
- JPA: Specification (API)
- Hibernate: Implementation (Provider)
- Other providers: EclipseLink, OpenJPA

### Entity Lifecycle
- Transient
- Managed/Persistent
- Detached
- Removed

### Persistence Context
- First Level Cache
- Flush Modes
- Clear & Detach Operations

## Entity Mapping

### Basic Annotations
```java
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Lob
    private String bio;
    
    @Transient
    private String tempField;
}
```

### Primary Keys
- `@Id` and `@GeneratedValue`
- ID Generation Strategies
- Composite Keys

### Value Types
- Embeddable Objects
- Collections of Basic Types
- Custom Types with `@Type`

## Relationships

### One-to-One
```java
@Entity
public class Employee {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workstation_id")
    private Workstation workstation;
}
```

### One-to-Many / Many-to-One
```java
@Entity
public class Department {
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}

@Entity
public class Employee {
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
```

### Many-to-Many
```java
@Entity
public class Employee {
    @ManyToMany
    @JoinTable(
        name = "employee_skills",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;
}
```

## Inheritance Strategies

### Single Table
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type")
public abstract class Employee { /* ... */ }
```

### Joined
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee { /* ... */ }
```

### Table per Class
```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Employee { /* ... */ }
```

## Querying Data

### JPQL (Java Persistence Query Language)
```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.department.name = :deptName")
    List<Employee> findByDepartmentName(@Param("deptName") String deptName);
}
```

### Criteria API
```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
Root<Employee> employee = query.from(Employee.class);
query.select(employee)
    .where(cb.equal(employee.get("department").get("name"), "Engineering"));
```

### Native Queries
```java
@Query(
    value = "SELECT * FROM employees e WHERE e.salary > :minSalary",
    nativeQuery = true
)
List<Employee> findHighEarners(@Param("minSalary") BigDecimal minSalary);
```

## Transactions & Concurrency

### Declarative Transactions
```java
@Service
@Transactional
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Transactional(readOnly = true)
    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSalary(Long employeeId, BigDecimal newSalary) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        employee.setSalary(newSalary);
        employeeRepository.save(employee);
    }
}
```

### Optimistic Locking
```java
@Entity
public class Employee {
    @Version
    private Long version;
    // ...
}
```

### Pessimistic Locking
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT e FROM Employee e WHERE e.id = :id")
Optional<Employee> findByIdForUpdate(@Param("id") Long id);
```

## Caching

### First Level Cache
- Enabled by default
- Session/EntityManager scoped
- No configuration needed

### Second Level Cache
```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee { /* ... */ }
```

### Query Cache
```java
@QueryHints({
    @QueryHint(name = "org.hibernate.cacheable", value = "true"),
    @QueryHint(name = "org.hibernate.cacheRegion", value = "employeeQueries")
})
@Query("SELECT e FROM Employee e WHERE e.department.name = :deptName")
List<Employee> findByDepartmentNameCached(@Param("deptName") String deptName);
```

## Performance Optimization

### N+1 Problem Solutions
- `JOIN FETCH`
- `@EntityGraph`
- `@BatchSize`

### Batch Operations
```java
@Modifying
@Query("UPDATE Employee e SET e.salary = e.salary * 1.1 WHERE e.department.id = :deptId")
int giveRaiseToDepartment(@Param("deptId") Long deptId);
```

### Pagination
```java
Page<Employee> findByDepartment(Department department, Pageable pageable);
```

## Advanced Topics

### Auditing
```java
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedDate
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String modifiedBy;
}
```

### Envers
```java
@Audited
@Entity
public class Employee { /* ... */ }
```

### Multi-tenancy
- Database per tenant
- Schema per tenant
- Discriminator column

## Best Practices

### Do's
- Use lazy loading for relationships
- Implement proper equals() and hashCode()
- Use DTOs for data transfer
- Use optimistic locking
- Implement proper transaction boundaries

### Don'ts
- Avoid N+1 queries
- Don't expose entities directly in REST APIs
- Avoid long-running transactions
- Don't ignore database constraints

## Interview Questions

### Basic
1. What is the difference between JPA and Hibernate?
2. Explain the entity lifecycle in Hibernate.
3. What is the N+1 problem and how do you solve it?

### Advanced
1. How does Hibernate second-level cache work?
2. Explain different inheritance mapping strategies.
3. How would you optimize a slow-running JPA query?

### Practical
1. Design a schema for an e-commerce system with proper relationships.
2. How would you implement soft delete in JPA?
3. Explain how you would handle database migrations in a production environment.

## Resources
- [Hibernate Documentation](https://hibernate.org/orm/documentation/6.1/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vlad Mihalcea's Blog](https://vladmihalcea.com/)
- [Java Persistence with Hibernate](https://www.manning.com/books/java-persistence-with-hibernate-second-edition)

---
*This guide is designed to help senior Java developers master JPA and Hibernate concepts for technical interviews at top product-based companies in Pune.*
