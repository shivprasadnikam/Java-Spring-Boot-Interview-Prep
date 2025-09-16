# Java Multithreading - Advanced Interview Questions (3+ YOE)

## Core Concepts

### 1. Thread Lifecycle & States
**Q: Explain Java thread states and transitions**
- **NEW**: Thread created but not started
- **RUNNABLE**: Thread is executing in JVM
- **BLOCKED**: Waiting for monitor lock
- **WAITING**: Indefinitely waiting for another thread
- **TIMED_WAITING**: Waiting for specific time
- **TERMINATED**: Thread has completed execution

**Q: Difference between BLOCKED and WAITING states?**
- **BLOCKED**: Waiting to acquire monitor lock (synchronized)
- **WAITING**: Waiting indefinitely for notification (wait(), join())
- **TIMED_WAITING**: Waiting with timeout (sleep(ms), wait(ms), join(ms))

### 2. Thread Creation & Execution
**Q: Different ways to create threads in Java?**
1. Extend Thread class
   ```java
   class MyThread extends Thread {
       public void run() {
           // thread code
       }
   }
   new MyThread().start();
   ```

2. Implement Runnable
   ```java
   class MyRunnable implements Runnable {
       public void run() {
           // thread code
       }
   }
   new Thread(new MyRunnable()).start();
   ```

3. Using ExecutorService (Preferred)
   ```java
   ExecutorService executor = Executors.newFixedThreadPool(10);
   executor.submit(() -> {
       // task code
   });
   ```

## Concurrency Utilities

### 3. Executor Framework
**Q: Compare different thread pools**
- **FixedThreadPool**: Fixed number of threads
- **CachedThreadPool**: Creates threads as needed
- **ScheduledThreadPool**: For scheduled tasks
- **WorkStealingPool**: Work-stealing algorithm (Java 8+)
- **SingleThreadExecutor**: Single worker thread

**Q: How does ThreadPoolExecutor work?**
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,  // Minimum threads to keep
    maxPoolSize,   // Maximum threads to create
    keepAliveTime, // Timeout for idle threads
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>() // Work queue
);
```

### 4. Concurrent Collections
**Q: When to use ConcurrentHashMap vs Collections.synchronizedMap()?**
- **ConcurrentHashMap**:
  - Better for high concurrency
  - Doesn't block for reads
  - Allows concurrent modifications
- **synchronizedMap**:
  - Simpler implementation
  - Blocks entire map during operations
  - Better for low-concurrency scenarios

## Synchronization & Locks

### 5. Advanced Locking
**Q: Difference between ReentrantLock and synchronized?**
- **ReentrantLock**:
  - More flexible (tryLock, lockInterruptibly)
  - Fairness option
  - Can query if held by current thread
  - Must manually unlock in finally
- **synchronized**:
  - Simpler syntax
  - Automatic release on exit
  - No timeout support
  - No fairness guarantee

**Q: What is ReadWriteLock?**
```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();

// Multiple threads can read simultaneously
rwLock.readLock().lock();
try {
    // read operation
} finally {
    rwLock.readLock().unlock();
}

// Only one thread can write
trwLock.writeLock().lock();
try {
    // write operation
} finally {
    rwLock.writeLock().unlock();
}
```

## Advanced Topics

### 6. Atomic Variables
**Q: How do Atomic classes work?**
- Use CAS (Compare-And-Swap) operations
- No locking (better performance)
- Classes: AtomicInteger, AtomicLong, AtomicReference
- Example:
  ```java
  AtomicInteger counter = new AtomicInteger(0);
  counter.incrementAndGet();  // Thread-safe increment
  ```

### 7. CompletableFuture
**Q: How to handle async operations?**
```java
CompletableFuture.supplyAsync(() -> fetchData())
    .thenApply(data -> process(data))
    .thenAccept(result -> System.out.println(result))
    .exceptionally(ex -> {
        System.err.println("Error: " + ex.getMessage());
        return null;
    });
```

## Performance & Debugging

### 8. Thread Dump Analysis
**Q: How to analyze thread dumps?**
1. **Deadlocks**: Look for BLOCKED threads and monitor locks
2. **Contention**: Check WAITING/BLOCKED states
3. **Hung Threads**: Look for RUNNABLE in the same stack trace
4. **Tools**: jstack, VisualVM, YourKit

### 9. Common Issues
**Q: How to prevent deadlocks?**
1. Lock ordering
2. Lock timeouts
3. Deadlock detection
4. Avoid nested locks
5. Use higher-level concurrency utilities

## Java Memory Model

### 10. Happens-Before Relationship
**Q: What is happens-before in Java?**
- Defines ordering of memory operations
- Ensures visibility between threads
- Key rules:
  - Program order
  - Monitor lock rules
  - Volatile variable rule
  - Thread start/join rules

## Practical Scenarios

### 11. Producer-Consumer Pattern
```java
BlockingQueue<Item> queue = new LinkedBlockingQueue<>();

// Producer
new Thread(() -> {
    while (true) {
        Item item = produceItem();
        queue.put(item);  // Blocks if full
    }
}).start();

// Consumer
new Thread(() -> {
    while (true) {
        Item item = queue.take();  // Blocks if empty
        processItem(item);
    }
}).start();
```

### 12. Rate Limiting
```java
class RateLimiter {
    private final Semaphore semaphore;
    private final int maxPermits;
    private final long period;
    private ScheduledExecutorService scheduler;

    public RateLimiter(int permits, long period, TimeUnit unit) {
        this.semaphore = new Semaphore(permits);
        this.maxPermits = permits;
        this.period = unit.toNanos(period);
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::releasePermit, 
            period, period, TimeUnit.NANOSECONDS);
    }

    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    private void releasePermit() {
        int permitsToRelease = maxPermits - semaphore.availablePermits();
        semaphore.release(permitsToRelease);
    }
}
```

## Best Practices

### 13. Thread Safety Guidelines
1. Use immutable objects
2. Use thread-safe collections
3. Prefer ExecutorService over raw Threads
4. Use higher-level concurrency utilities
5. Document thread-safety guarantees
6. Avoid excessive synchronization
7. Use volatile for flags
8. Prefer Atomic variables over synchronization

### 14. Debugging Thread Issues
1. Use thread dumps
2. Use VisualVM/YourKit for profiling
3. Enable thread contention logging
4. Use logging with thread names
5. Test with different thread counts

## Java 17 Updates

### 15. New Concurrency Features
- Virtual Threads (Project Loom)
- Structured Concurrency (JEP 428)
- Pattern Matching for switch (JEP 406)
- Sealed Classes for better concurrency control
- New methods in CompletableFuture

## Performance Optimization

### 16. Thread Pool Tuning
1. Right-size thread pools
2. Use appropriate queue types
3. Set proper rejection policies
4. Monitor thread pool metrics
5. Consider work-stealing pools

### 17. False Sharing
**Q: What is false sharing and how to prevent it?**
- Occurs when threads modify variables that share the same cache line
- Solution: Use padding or @Contended annotation
  ```java
  class Counter {
      @jdk.internal.vm.annotation.Contended
      volatile long count1;
      
      @jdk.internal.vm.annotation.Contended
      volatile long count2;
  }
  ```

## Real-world Patterns

### 18. Circuit Breaker Pattern
```java
class CircuitBreaker {
    private final int failureThreshold;
    private final long timeout;
    private int failures = 0;
    private long lastFailureTime = 0;
    private boolean isOpen = false;

    public CircuitBreaker(int failureThreshold, long timeout) {
        this.failureThreshold = failureThreshold;
        this.timeout = timeout;
    }

    public void execute(Runnable command) {
        if (isOpen) {
            if (System.currentTimeMillis() - lastFailureTime > timeout) {
                isOpen = false; // Half-open state
            } else {
                throw new CircuitBreakerOpenException();
            }
        }

        try {
            command.run();
            reset();
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }

    private void recordFailure() {
        failures++;
        lastFailureTime = System.currentTimeMillis();
        if (failures >= failureThreshold) {
            isOpen = true;
        }
    }

    private void reset() {
        failures = 0;
        isOpen = false;
    }
}
```

## Common Interview Questions

### 19. Thread Dump Analysis
**Q: How would you analyze a deadlock?**
1. Take thread dump (jstack or VisualVM)
2. Look for BLOCKED threads
3. Find cyclic dependency in monitor locks
4. Analyze stack traces to find the root cause

### 20. Memory Consistency
**Q: What is the volatile keyword and when to use it?**
- Ensures visibility of changes across threads
- Prevents instruction reordering
- Use cases:
  - Flags (boolean flags)
  - Single writer scenarios
  - Immutable object publication

## Practical Exercises

### 21. Coding Challenges
1. Implement a ThreadPool from scratch
2. Create a RateLimiter with token bucket algorithm
3. Implement a blocking queue using wait/notify
4. Write a deadlock detection utility
5. Create a task scheduler with priority

## Interview Tips

### 22. Common Gotchas
1. Always unlock in finally block
2. Be careful with double-checked locking
3. Avoid excessive synchronization
4. Understand happens-before relationships
5. Test with multiple threads
6. Monitor thread pool metrics in production
7. Handle thread pool rejections properly
8. Be aware of thread-local memory leaks
9. Use proper exception handling in threads
10. Consider using CompletableFuture for async operations
