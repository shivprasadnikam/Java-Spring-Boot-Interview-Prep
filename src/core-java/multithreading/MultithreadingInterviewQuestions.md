# Multithreading & Concurrency Interview Questions 🧵

## **⚡ Most Asked Multithreading Questions (2025 Edition)**

## **Core Concepts (High Priority)**

### **Q1: Thread vs Runnable**
**Answer:**
- **Runnable is better** because:
  - Java supports single inheritance, implementing Runnable allows extending other classes
  - Better separation of concerns - task vs thread management
  - Can be reused with different execution strategies (Thread, ExecutorService, etc.)

```java
// Better approach
public class TaskRunner implements Runnable {
    @Override
    public void run() {
        System.out.println("Task executed by: " + Thread.currentThread().getName());
    }
}

// Usage
TaskRunner task = new TaskRunner();
new Thread(task).start(); // Direct thread
executor.submit(task);    // Thread pool
```

### **Q2: Explain synchronization and its types**
**Answer:**
1. **Method-level synchronization**
2. **Block-level synchronization**
3. **Static synchronization**

```java
public class BankAccount {
    private double balance = 1000;
    
    // Method-level synchronization
    public synchronized void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        }
    }
    
    // Block-level synchronization (better performance)
    public void deposit(double amount) {
        synchronized(this) {
            balance += amount;
        }
    }
    
    // Static synchronization (class-level lock)
    public static synchronized void printBankInfo() {
        System.out.println("Bank information");
    }
}
```

### **Q3: What is deadlock and how to prevent it?**
**Answer:**
**Deadlock occurs when two or more threads wait for each other indefinitely.**

```java
// Deadlock example
public class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    public void method1() {
        synchronized(lock1) {
            System.out.println("Thread1: Holding lock1");
            synchronized(lock2) {
                System.out.println("Thread1: Holding lock1 & lock2");
            }
        }
    }
    
    public void method2() {
        synchronized(lock2) {
            System.out.println("Thread2: Holding lock2");
            synchronized(lock1) { // Deadlock here!
                System.out.println("Thread2: Holding lock1 & lock2");
            }
        }
    }
}

// Prevention: Always acquire locks in same order
public void method2Fixed() {
    synchronized(lock1) { // Same order as method1
        System.out.println("Thread2: Holding lock1");
        synchronized(lock2) {
            System.out.println("Thread2: Holding lock1 & lock2");
        }
    }
}
```

### **Q4: CompletableFuture vs Future - Explain with example**
**Answer:**
```java
// Future - blocking and limited
ExecutorService executor = Executors.newFixedThreadPool(3);
Future<String> future = executor.submit(() -> {
    Thread.sleep(2000);
    return "Hello from Future";
});

String result = future.get(); // Blocks until complete

// CompletableFuture - non-blocking and composable
CompletableFuture<String> completableFuture = CompletableFuture
    .supplyAsync(() -> {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        return "Hello";
    })
    .thenApply(s -> s + " World")
    .thenApply(String::toUpperCase);

completableFuture.thenAccept(System.out::println); // Non-blocking
```

## **Advanced Concurrency**

### **Q5: Explain different types of thread pools**
**Answer:**
```java
// 1. Fixed Thread Pool - fixed number of threads
ExecutorService fixedPool = Executors.newFixedThreadPool(5);

// 2. Cached Thread Pool - creates threads as needed
ExecutorService cachedPool = Executors.newCachedThreadPool();

// 3. Single Thread Executor - single worker thread
ExecutorService singlePool = Executors.newSingleThreadExecutor();

// 4. Scheduled Thread Pool - for delayed/periodic tasks
ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(3);

// Custom thread pool (recommended for production)
ThreadPoolExecutor customPool = new ThreadPoolExecutor(
    5,                      // corePoolSize
    10,                     // maximumPoolSize
    60L,                    // keepAliveTime
    TimeUnit.SECONDS,       // unit
    new LinkedBlockingQueue<>(100), // workQueue
    new ThreadFactoryBuilder()
        .setNameFormat("custom-pool-%d")
        .build()
);
```

### **Q6: Producer-Consumer problem implementation**
**Answer:**
```java
public class ProducerConsumer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;
    private final Object lock = new Object();
    
    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                while (queue.size() == capacity) {
                    lock.wait(); // Wait if queue is full
                }
                
                queue.offer(++value);
                System.out.println("Produced: " + value);
                lock.notifyAll(); // Notify consumers
            }
            Thread.sleep(1000);
        }
    }
    
    public void consume() throws InterruptedException {
        while (true) {
            synchronized (lock) {
                while (queue.isEmpty()) {
                    lock.wait(); // Wait if queue is empty
                }
                
                int value = queue.poll();
                System.out.println("Consumed: " + value);
                lock.notifyAll(); // Notify producers
            }
            Thread.sleep(1500);
        }
    }
}

// Better approach using BlockingQueue
public class ProducerConsumerImproved {
    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
    
    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            queue.put(++value); // Blocks if queue is full
            System.out.println("Produced: " + value);
            Thread.sleep(1000);
        }
    }
    
    public void consume() throws InterruptedException {
        while (true) {
            int value = queue.take(); // Blocks if queue is empty
            System.out.println("Consumed: " + value);
            Thread.sleep(1500);
        }
    }
}
```

### **Q7: Implement thread-safe Singleton pattern**
**Answer:**
```java
// Double-checked locking (most efficient)
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Enum-based (recommended - thread-safe by default)
public enum SingletonEnum {
    INSTANCE;
    
    public void doSomething() {
        // Business logic
    }
}
```

## **Concurrent Collections**

### **Q8: volatile vs Atomic variables**
**Answer:**
```java
public class VolatileVsAtomic {
    
    // volatile - ensures visibility, not atomicity
    private volatile boolean flag = false;
    private volatile int counter = 0; // Not thread-safe for increment
    
    // Atomic - ensures both visibility and atomicity
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    private AtomicBoolean atomicFlag = new AtomicBoolean(false);
    
    public void demonstrateVolatile() {
        // volatile ensures other threads see the change immediately
        flag = true; // Visible to all threads
        
        // But this is NOT thread-safe
        counter++; // Race condition possible
    }
    
    public void demonstrateAtomic() {
        // Atomic operations are thread-safe
        atomicCounter.incrementAndGet(); // Thread-safe increment
        atomicFlag.compareAndSet(false, true); // Thread-safe CAS operation
        
        // Advanced atomic operations
        atomicCounter.updateAndGet(x -> x * 2); // Thread-safe update
        atomicCounter.accumulateAndGet(5, Integer::sum); // Thread-safe accumulation
    }
}
```

**Key differences:**
- **volatile**: Ensures visibility across threads, prevents instruction reordering
- **Atomic**: Ensures both visibility and atomicity using CAS operations
- **Use volatile for**: Simple flags, status variables
- **Use Atomic for**: Counters, numeric operations, complex state changes

### **Q9: ExecutorService → execute() vs submit()**
**Answer:**
```java
public class ExecutorServiceMethods {
    
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    
    public void demonstrateExecuteVsSubmit() {
        // execute() - fire and forget, no return value
        executor.execute(() -> {
            System.out.println("Task executed by: " + Thread.currentThread().getName());
            // Cannot get result or handle exceptions easily
        });
        
        // submit() - returns Future, can get result and handle exceptions
        Future<String> future = executor.submit(() -> {
            Thread.sleep(1000);
            return "Task completed by: " + Thread.currentThread().getName();
        });
        
        try {
            String result = future.get(); // Blocking call
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Task failed: " + e.getMessage());
        }
        
        // submit() with Runnable
        Future<?> voidFuture = executor.submit(() -> {
            System.out.println("Runnable task");
        });
        
        // Check if task is done
        if (voidFuture.isDone()) {
            System.out.println("Task completed");
        }
    }
}
```

**Key differences:**
- **execute()**: Void return, fire-and-forget, exception handling in task
- **submit()**: Returns Future, can get result, better exception handling

### **Q10: ThreadPoolExecutor – fixed vs cached pool**
**Answer:**
```java
public class ThreadPoolTypes {
    
    public void demonstrateThreadPools() {
        // Fixed Thread Pool - fixed number of threads
        ExecutorService fixedPool = Executors.newFixedThreadPool(5);
        // Use case: Known workload, controlled resource usage
        
        // Cached Thread Pool - creates threads as needed
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        // Use case: Many short-lived tasks, unknown workload
        
        // Custom ThreadPoolExecutor for production
        ThreadPoolExecutor customPool = new ThreadPoolExecutor(
            2,                      // corePoolSize
            10,                     // maximumPoolSize
            60L,                    // keepAliveTime
            TimeUnit.SECONDS,       // unit
            new ArrayBlockingQueue<>(100), // workQueue
            new ThreadFactoryBuilder()
                .setNameFormat("custom-pool-%d")
                .setDaemon(true)
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy() // rejectionHandler
        );
        
        // Monitor thread pool
        System.out.println("Active threads: " + customPool.getActiveCount());
        System.out.println("Pool size: " + customPool.getPoolSize());
        System.out.println("Queue size: " + customPool.getQueue().size());
    }
}
```

**Thread Pool Comparison:**
- **Fixed**: Predictable resource usage, good for steady workload
- **Cached**: Dynamic sizing, good for bursty workload
- **Custom**: Full control over behavior, recommended for production

### **Q11: ConcurrentHashMap vs HashMap**
**Answer:**
```java
public class ConcurrentHashMapDemo {
    
    public void demonstrateDifferences() {
        // HashMap - not thread-safe
        Map<String, Integer> hashMap = new HashMap<>();
        // Problems in multithreaded environment:
        // - Data corruption
        // - Infinite loops
        // - Lost updates
        
        // ConcurrentHashMap - thread-safe
        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        
        // Thread-safe operations
        concurrentMap.put("key1", 1);
        concurrentMap.putIfAbsent("key2", 2);
        concurrentMap.compute("key1", (k, v) -> v == null ? 1 : v + 1);
        concurrentMap.merge("key3", 1, Integer::sum);
        
        // Atomic operations
        concurrentMap.computeIfAbsent("key4", k -> expensiveComputation(k));
        concurrentMap.computeIfPresent("key1", (k, v) -> v * 2);
    }
    
    private Integer expensiveComputation(String key) {
        // Simulate expensive operation
        return key.hashCode();
    }
}
```

### **Q12: ConcurrentHashMap internal working**
**Answer:**
- **Java 7**: Uses segment-based locking (16 segments by default)
- **Java 8+**: Uses CAS (Compare-And-Swap) operations and synchronized blocks
- **Key features**: Thread-safe, better performance than Hashtable, no null keys/values

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Thread-safe operations
map.put("key1", 1);
map.putIfAbsent("key2", 2);
map.compute("key1", (k, v) -> v == null ? 1 : v + 1);
map.merge("key3", 1, Integer::sum);
```

### **Q13: CountDownLatch vs CyclicBarrier vs Semaphore**
**Answer:**
```java
// CountDownLatch - one-time use, countdown mechanism
CountDownLatch latch = new CountDownLatch(3);
// 3 threads call latch.countDown(), main thread waits with latch.await()

// CyclicBarrier - reusable, all threads wait for each other
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("All arrived"));
// Each thread calls barrier.await()

// Semaphore - controls access to resource with permits
Semaphore semaphore = new Semaphore(2); // 2 permits
semaphore.acquire(); // Get permit
// Use resource
semaphore.release(); // Return permit
```

## **Real-World Scenarios**

### **Q14: How to handle race conditions in Spring Boot?**
**Answer:**
```java
@Service
public class CounterService {
    private final AtomicInteger counter = new AtomicInteger(0);
    
    // Thread-safe increment
    public int increment() {
        return counter.incrementAndGet();
    }
    
    // For database operations, use optimistic locking
    @Entity
    public class Product {
        @Id
        private Long id;
        
        @Version
        private Long version; // Optimistic locking
        
        private int quantity;
    }
    
    @Transactional
    public void updateQuantity(Long productId, int newQuantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        
        product.setQuantity(newQuantity);
        productRepository.save(product); // Version check happens here
    }
}
```
