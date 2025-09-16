# Mastercard - Java Developer Interview (3 YOE)

## Interview Overview
- **Position**: Java Backend Developer
- **Experience Level**: 3 Years
- **Focus Areas**: Java, Spring Boot, System Design, Problem Solving
- **Interview Rounds**: 3-4 (Technical + HR)

## Interview Process

### 1. Online Assessment (90 minutes)
**Components**:
- 2-3 Coding Problems (Medium-Hard difficulty)
- MCQs on Core Java, Data Structures, and System Design

**Sample Problems**:
1. **Array Manipulation**
   - Find the 3rd largest mountain in an array
   - Design a Mountain class and implement the solution

2. **Algorithm Optimization**
   - Given an array, find min and max efficiently
   - Time complexity analysis

### 2. Technical Round 1 (60 minutes)
**Topics Covered**:

1. **Core Java**
   - OOPs concepts with real-world examples
   - Exception Handling best practices
   - Collections Framework internals
   - Multithreading and Concurrency

2. **Coding Problem**
   - Implement a thread-safe Singleton class
   - Design a rate limiter

### 3. Technical Round 2 (60-75 minutes)
**System Design**:
- Design a Payment Gateway (focus on scalability and security)
- Design a Coding Platform (if mentioned in your experience)

**Discussion Points**:
- API Design
- Database Schema
- Caching Strategy
- Load Balancing
- Security Considerations

### 4. HR/Managerial Round (30-45 minutes)
**Common Questions**:
- Tell me about yourself
- Why Mastercard?
- Explain your projects in detail
- Challenges faced and how you overcame them
- Teamwork and conflict resolution examples

## Technical Questions & Answers

### 1. Core Java

**Q: Explain Java Memory Model**
```java
// Example of memory leak
public class MemoryLeakExample {
    private static final List<Double> list = new ArrayList<>();
    
    public void populateList() {
        for (int i = 0; i < 10000000; i++) {
            list.add(Math.random());
        }
        System.out.println("Debug Point 2");
    }
    
    public static void main(String[] args) {
        new MemoryLeakExample().populateList();
        // list remains in memory even after method execution
    }
}
```

**Q: Thread-safe Singleton**
```java
public class ThreadSafeSingleton {
    private static volatile ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton() {}
    
    public static ThreadSafeSingleton getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}
```

### 2. Spring Boot

**Q: How does Spring Boot Auto-configuration work?**
- Uses `@EnableAutoConfiguration`
- Scans classpath for `META-INF/spring.factories`
- Applies configurations based on available dependencies

**Q: Explain Spring Security flow**
1. Authentication Filter
2. Authentication Manager
3. Authentication Provider
4. User Details Service
5. Security Context

### 3. System Design

**Design a Payment Gateway**

**Components**:
1. **API Gateway**: Request routing, rate limiting
2. **Payment Service**: Core payment processing
3. **Fraud Detection**: ML-based fraud analysis
4. **Notification Service**: Email/SMS notifications
5. **Database**: Transaction storage

**Considerations**:
- Idempotency
- Retry mechanism
- Idempotency keys
- Distributed transactions
- Eventual consistency

## Coding Problems

### 1. Find 3rd Largest Mountain
```java
class Mountain {
    private String name;
    private double height;
    
    public Mountain(String name, double height) {
        this.name = name;
        this.height = height;
    }
    
    // Getters, setters, toString()
}

public class MountainFinder {
    public static Mountain findThirdLargestMountain(List<Mountain> mountains) {
        return mountains.stream()
            .sorted((m1, m2) -> Double.compare(m2.getHeight(), m1.getHeight()))
            .skip(2)
            .findFirst()
            .orElse(null);
    }
    
    public static void main(String[] args) {
        List<Mountain> mountains = Arrays.asList(
            new Mountain("Everest", 8848),
            new Mountain("K2", 8611),
            new Mountain("Kangchenjunga", 8586),
            new Mountain("Lhotse", 8516)
        );
        
        Mountain thirdLargest = findThirdLargestMountain(mountains);
        System.out.println("Third largest mountain: " + thirdLargest);
    }
}
```

### 2. Rate Limiter Implementation
```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class RateLimiter {
    private final int limit;
    private final long timeWindowInMillis;
    private final AtomicInteger counter;
    private long lastRefillTime;
    
    public RateLimiter(int limit, long timeWindowInMillis) {
        this.limit = limit;
        this.timeWindowInMillis = timeWindowInMillis;
        this.counter = new AtomicInteger(0);
        this.lastRefillTime = System.currentTimeMillis();
    }
    
    public synchronized boolean allowRequest() {
        refill();
        if (counter.get() < limit) {
            counter.incrementAndGet();
            return true;
        }
        return false;
    }
    
    private void refill() {
        long now = System.currentTimeMillis();
        if (now - lastRefillTime > timeWindowInMillis) {
            counter.set(0);
            lastRefillTime = now;
        }
    }
}
```

## Interview Tips

1. **Technical Preparation**:
   - Focus on Java 8+ features
   - Practice system design questions
   - Understand payment domain concepts
   - Review distributed systems principles

2. **Coding Round**:
   - Write clean, modular code
   - Handle edge cases
   - Optimize time/space complexity
   - Add comments for complex logic

3. **System Design**:
   - Start with requirements clarification
   - Define API contracts
   - Discuss trade-offs
   - Consider scalability and reliability

4. **Behavioral Questions**:
   - Use STAR method for answers
   - Prepare examples of leadership and teamwork
   - Be ready to discuss your projects in depth

## Resources
- [Mastercard Engineering Blog](https://mastercard.github.io/)
- [Java Documentation](https://docs.oracle.com/en/java/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [LeetCode Mastercard Questions](https://leetcode.com/company/mastercard/)

Good luck with your interview! 🚀
