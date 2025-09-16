# Air India Client - Java Developer Interview (3 YOE)

## Interview Overview
- **Position**: Java Developer
- **Experience Level**: 3 Years
- **Focus Areas**: Java, Spring Boot, AWS, OOPs, Collections, Multithreading

## Interview Questions & Sample Answers

### 1. Self-Introduction
**Question**: Can you give a brief self-introduction?

**Sample Answer**:
```
"I'm a Java Developer with 3 years of experience in building scalable applications. 
I specialize in Spring Boot, Microservices, and cloud technologies. 
In my current role at [Company], I've worked on [mention 1-2 key projects] 
where I implemented [mention key technologies/features].
I'm passionate about writing clean, efficient code and solving complex problems."
```

### 2. Project Experience
**Question**: Explain your project experience in detail.

**Key Points to Cover**:
- Project overview and business goals
- Your role and responsibilities
- Technologies and tools used
- Challenges faced and how you overcame them
- Impact/outcome of the project

### 3. AWS Components
**Question**: Which AWS components have you worked with?

**Common AWS Services**:
- **EC2**: For virtual servers
- **S3**: For object storage
- **RDS**: For managed databases
- **Lambda**: For serverless computing
- **SQS/SNS**: For messaging and notifications
- **CloudFront**: For content delivery
- **IAM**: For access management

### 4. OOPs Concepts with Examples

**a. Encapsulation**
```java
public class Employee {
    private String name;  // Private field
    
    // Public getter and setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

**b. Inheritance**
```java
class Vehicle {
    void run() { System.out.println("Vehicle is running"); }
}

class Car extends Vehicle {
    @Override
    void run() { System.out.println("Car is running safely"); }
}
```

**c. Polymorphism**
```java
interface Shape {
    void draw();
}

class Circle implements Shape {
    public void draw() { System.out.println("Drawing Circle"); }
}

class Square implements Shape {
    public void draw() { System.out.println("Drawing Square"); }
}
```

**d. Abstraction**
```java
abstract class Animal {
    abstract void makeSound();
    
    void sleep() {
        System.out.println("Sleeping...");
    }
}
```

### 5. Collections Framework

**Question**: Which do you use more often: List or Set, and why?

**Answer**:
- **List**: When order matters and duplicates are allowed
  - Example: `List<String> names = new ArrayList<>();`
- **Set**: When unique elements are required
  - Example: `Set<String> uniqueNames = new HashSet<>();`

**Question**: Difference between LinkedList, ArrayList, HashMap, and ConcurrentHashMap

| Collection | Ordering | Duplicates | Thread-Safe | Best For |
|------------|----------|------------|-------------|----------|
| ArrayList | Yes | Yes | No | Random access, frequent reads |
| LinkedList | Yes | Yes | No | Frequent insertions/deletions |
| HashMap | No | No (keys) | No | Key-value pairs, fast lookups |
| ConcurrentHashMap | No | No (keys) | Yes | Thread-safe operations |

### 6. Thread Safety

**Question**: What does thread-safe mean in Java?
- A class/method is thread-safe if it can be safely used by multiple threads simultaneously without any race conditions.
- Example: `StringBuffer` (thread-safe) vs `StringBuilder` (not thread-safe)

**Question**: How does multithreading work in Java?
- Multiple threads can execute code concurrently
- Threads share the same memory space
- Can be created by extending `Thread` class or implementing `Runnable`

```java
class MyThread extends Thread {
    public void run() {
        System.out.println("Thread is running");
    }
}

// Usage:
MyThread t1 = new MyThread();
t1.start();
```

### 7. Java 8 Features

**Question**: Explain lambda expressions in Java 8 with use cases.

```java
// Before Java 8
Collections.sort(list, new Comparator<String>() {
    @Override
    public int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }
});

// With Lambda
Collections.sort(list, (s1, s2) -> s1.compareTo(s2));

// ForEach with Lambda
list.forEach(item -> System.out.println(item));

// Method Reference
list.forEach(System.out::println);
```

### 8. Spring Boot Concepts

**Question**: Difference between @QueryParam, @RequestParam, and @PathVariable

| Annotation | Usage | Example |
|------------|-------|---------|
| `@RequestParam` | For query parameters | `/users?id=123` |
| `@PathVariable` | For path variables | `/users/{id}` |
| `@QueryParam` | JAX-RS equivalent of @RequestParam | Not used in Spring |

**Example**:
```java
@GetMapping("/users/{id}")
public User getUser(
    @PathVariable Long id,
    @RequestParam(required = false) String name) {
    // Implementation
}
```

**Question**: Explain Stereotype annotations
- `@Component`: Generic stereotype for any Spring-managed component
- `@Service`: Indicates a service layer component
- `@Repository`: Indicates a DAO/repository component
- `@Controller`: Indicates a web controller
- `@RestController`: Specialized `@Controller` for REST APIs

**Question**: What is Dependency Injection?
- Design pattern where objects receive their dependencies from an external source
- Types:
  - Constructor Injection (recommended)
  - Setter Injection
  - Field Injection

### 9. Database Questions
Common topics:
- Normalization
- Indexes
- Joins
- Transactions (ACID properties)
- NoSQL vs SQL

### 10. Coding Exercise
**Problem**: Create a list of employees and filter salaries less than 5000 using Java Streams

```java
import java.util.*;
import java.util.stream.*;

class Employee {
    private String name;
    private double salary;
    
    // Constructor, getters, setters
    
    @Override
    public String toString() {
        return "Employee{name='" + name + "', salary=" + salary + '}';
    }
}

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("John", 4500),
            new Employee("Alice", 5200),
            new Employee("Bob", 4800),
            new Employee("Eve", 3800)
        );
        
        List<Employee> filtered = employees.stream()
            .filter(e -> e.getSalary() < 5000)
            .collect(Collectors.toList());
            
        System.out.println("Employees with salary < 5000:");
        filtered.forEach(System.out::println);
    }
}
```

## Additional Preparation Tips

1. **Practice Coding**
   - Solve problems on LeetCode/HackerRank
   - Focus on collections, multithreading, and Java 8 features

2. **Spring Boot**
   - Understand dependency injection
   - Know common annotations
   - Practice building REST APIs

3. **System Design**
   - Be ready to discuss database design
   - Understand scalability concepts

4. **Behavioral Questions**
   - Prepare STAR method answers
   - Have examples ready for teamwork, challenges, etc.

## Resources
- [Java Documentation](https://docs.oracle.com/en/java/)
- [Spring Guides](https://spring.io/guides)
- [Baeldung Java/Spring Tutorials](https://www.baeldung.com/)

Good luck with your interview! 🚀
