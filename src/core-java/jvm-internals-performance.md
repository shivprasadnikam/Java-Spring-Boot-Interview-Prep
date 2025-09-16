# JVM Internals & Performance Tuning - Advanced Guide

## JVM Architecture

### 1. Key Components
```
┌───────────────────────────────────────────────────┐
│                  Class Loader Subsystem           │
├─────────────────┬─────────────────┬───────────────┤
│  Loading        │  Linking        │  Initialization
└─────────────────┴─────────────────┴───────────────┘
┌───────────────────────────────────────────────────┐
│                  Runtime Data Areas               │
├───────────┬───────────┬───────────┬──────────────┤
│ Method    │  Heap     │  Stack    │  PC Registers│
│ Area      │           │           │              │
├───────────┼───────────┼───────────┼──────────────┤
│  Native   │  Direct   │           │              │
│  Method   │  Memory   │           │              │
│  Area     │           │           │              │
└───────────┴───────────┴───────────┴──────────────┘
┌───────────────────────────────────────────────────┐
│                  Execution Engine                 │
├─────────────────┬─────────────────┬───────────────┤
│  Interpreter    │  JIT Compiler   │  Garbage      │
│                 │                 │  Collector    │
└─────────────────┴─────────────────┴───────────────┘
```

### 2. Class Loading Process
1. **Loading**: Loads .class files into memory
2. **Linking**:
   - Verification: Ensures .class is valid
   - Preparation: Allocates memory for static variables
   - Resolution: Converts symbolic references to direct references
3. **Initialization**: Executes static initializers

## Memory Management

### 3. Heap Memory Structure
```
┌───────────────────────────────────────────────────┐
│                  Young Generation                  │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────┐  │
│  │    Eden     │  │   S0 (From) │  │ S1 (To)   │  │
│  └─────────────┘  └─────────────┘  └───────────┘  │
├───────────────────────────────────────────────────┤
│                  Old Generation                   │
│  ┌─────────────────────────────────────────────┐  │
│  │                                             │  │
│  └─────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────┤
│                  Metaspace (Java 8+)             │
│  ┌─────────────────────────────────────────────┐  │
│  │                                             │  │
│  └─────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────┘
```

### 4. Garbage Collection Algorithms

#### Serial GC (-XX:+UseSerialGC)
- Single-threaded, stop-the-world collector
- Good for small applications with small data sets

#### Parallel GC (-XX:+UseParallelGC)
- Multi-threaded young generation collector
- Uses multiple threads for minor GC

#### CMS (-XX:+UseConcMarkSweepGC)
- Concurrent Mark Sweep collector
- Low pause times for old generation
- Deprecated in Java 9, removed in Java 14

#### G1 GC (-XX:+UseG1GC)
- Garbage First collector (default since Java 9)
- Concurrent and parallel collector
- Predictable pause times
- Divides heap into equal-sized regions

#### ZGC (-XX:+UseZGC)
- Scalable low-latency GC
- Pause times < 10ms even for large heaps
- Concurrent operations
- Java 11+ (production since Java 15)

#### Shenandoah (-XX:+UseShenandoahGC)
- Ultra-low pause times
- Concurrent compaction
- Java 12+

## Performance Tuning

### 5. Key JVM Flags

#### Memory Settings
```bash
# Initial heap size
-Xms4g

# Maximum heap size
-Xmx8g

# Young generation size
-XX:NewSize=2g

# Max metaspace size
-XX:MaxMetaspaceSize=512m

# Direct memory size
-XX:MaxDirectMemorySize=1g
```

#### GC Logging
```bash
# Enable GC logging
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintGCTimeStamps
-Xloggc:/path/to/gc.log

# Log rotation
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=5
-XX:GCLogFileSize=20M
```

#### Heap Dump
```bash
# On OutOfMemoryError
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/path/to/dump.hprof

# On Ctrl+Break (Windows) or SIGQUIT (Linux)
-XX:+HeapDumpOnCtrlBreak
```

### 6. Common Performance Issues

#### Memory Leaks
```java
// Common leak pattern: Static collections
private static final Map<String, Object> CACHE = new HashMap<>();

public void addToCache(String key, Object value) {
    CACHE.put(key, value);
}
```

#### String Concatenation
```java
// Bad - creates multiple StringBuilder and String objects
String result = "";
for (String str : strings) {
    result += str;
}

// Good - uses single StringBuilder
StringBuilder sb = new StringBuilder();
for (String str : strings) {
    sb.append(str);
}
String result = sb.toString();
```

### 7. JIT Compilation

#### Tiered Compilation
```bash
# Enable tiered compilation (default in Java 8+)
-XX:+TieredCompilation

# Number of compilation threads
-XX:CICompilerCount=4
```

#### JIT Watch
```bash
# Print compilation details
-XX:+PrintCompilation
-XX:+PrintInlining
-XX:+PrintAssembly
```

## Monitoring Tools

### 8. Command Line Tools

#### jps - JVM Process Status
```bash
jps -lvm
```

#### jstat - JVM Statistics
```bash
# GC statistics
jstat -gc <pid> 1000 10

# Class loader statistics
jstat -class <pid> 1000 10
```

#### jmap - Memory Map
```bash
# Heap histogram
jmap -histo:live <pid>

# Heap dump
jmap -dump:live,format=b,file=heap.hprof <pid>
```

#### jstack - Stack Trace
```bash
# Thread dump
jstack -l <pid> > thread_dump.txt
```

### 9. Visual Tools

#### VisualVM
```bash
jvisualvm
```

#### Java Mission Control (JMC)
```bash
jmc
```

#### Async Profiler
```bash
# CPU profiling
./profiler.sh -d 30 -f flamegraph.html <pid>

# Allocation profiling
./profiler.sh -d 30 -e alloc -f alloc.html <pid>
```

## Advanced Topics

### 10. Memory Models

#### Java Memory Model (JMM)
```java
// Happens-before relationships
class Shared {
    int x, y;
    volatile boolean ready;
    
    void writer() {
        x = 1;          // 1
        y = 2;          // 2
        ready = true;   // 3 - volatile write
    }
    
    void reader() {
        if (ready) {    // 4 - volatile read
            System.out.println(x + "," + y); // 5
        }
    }
}
```

### 11. Off-Heap Memory

#### Direct ByteBuffers
```java
// Allocate direct memory
ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024); // 1MB

// Cleaner pattern for direct buffers
try (Cleaner cleaner = Cleaner.create()) {
    ByteBuffer bb = ByteBuffer.allocateDirect(1024);
    cleaner.register(bb, () -> {
        // Cleanup code when buffer becomes phantom reachable
    });
    // Use buffer
}
```

### 12. Class Data Sharing (CDS)
```bash
# Create shared archive
java -Xshare:dump -XX:SharedArchiveFile=app-cds.jsa \
     -XX:SharedClassListFile=classes.lst \
     -cp app.jar

# Run with CDS
java -Xshare:on -XX:SharedArchiveFile=app-cds.jsa -jar app.jar
```

## Performance Patterns

### 13. Object Pooling
```java
public class ObjectPool<T> {
    private final Supplier<T> creator;
    private final Queue<T> pool = new ConcurrentLinkedQueue<>();
    
    public ObjectPool(Supplier<T> creator) {
        this.creator = creator;
    }
    
    public T borrow() {
        T obj = pool.poll();
        return obj != null ? obj : creator.get();
    }
    
    public void release(T obj) {
        pool.offer(obj);
    }
}
```

### 14. Thread Local Storage
```java
private static final ThreadLocal<SimpleDateFormat> dateFormat =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

public String formatDate(Date date) {
    return dateFormat.get().format(date);
}
```

## Troubleshooting

### 15. Common Issues & Solutions

#### High CPU Usage
1. Take multiple thread dumps
2. Look for RUNNABLE threads
3. Check for tight loops or deadlocks

#### Memory Leaks
1. Take heap dumps
2. Analyze with MAT or VisualVM
3. Look for growing collections or caches

#### Long GC Pauses
1. Enable GC logging
2. Analyze GC logs with GCViewer or similar
3. Consider switching to low-pause GC (ZGC, Shenandoah)

#### ClassLoader Leaks
1. Common in application servers
2. Look for static collections in application classes
3. Check thread locals

## Best Practices

### 16. Memory Management
1. **Avoid Finalizers**: Use Cleaner or PhantomReference instead
2. **Use try-with-resources**: For AutoCloseable objects
3. **Size collections appropriately**: Avoid frequent resizing
4. **Be careful with caches**: Use weak/soft references when appropriate

### 17. Performance Optimization
1. **Measure first**: Use profilers to find bottlenecks
2. **Focus on hot spots**: Don't optimize cold code
3. **Understand costs**: Know the cost of operations
4. **Test with realistic data**: Synthetic benchmarks can be misleading

### 18. Garbage Collection
1. **Choose the right GC**: Based on your requirements
2. **Tune heap sizes**: Based on your application's needs
3. **Monitor GC logs**: In production
4. **Avoid System.gc()**: Let the JVM handle collections

## Interview Questions

### 19. Common Questions

#### Q: Explain JVM memory areas
- **Method Area**: Class metadata, static variables, constants
- **Heap**: Object instances
- **Stack**: Local variables, method calls
- **PC Registers**: Thread execution position
- **Native Method Stack**: Native method calls

#### Q: How does G1 GC work?
1. Divides heap into equal-sized regions
2. Concurrent marking phase
3. Evacuation phase (stop-the-world)
4. Compacts live objects
5. Prioritizes regions with most garbage (garbage first)

#### Q: What is the difference between Stack and Heap?
| Stack | Heap |
|-------|------|
| Stores primitive types and references | Stores objects |
| Fixed size per thread | Shared among all threads |
| Fast access | Slower access |
| Memory allocated at compile time | Memory allocated at runtime |
| LIFO structure | Dynamic memory allocation |

### 20. Advanced Questions

#### Q: How would you troubleshoot a memory leak?
1. **Reproduce the issue**: Under controlled conditions
2. **Generate heap dumps**: Using jmap or -XX:+HeapDumpOnOutOfMemoryError
3. **Analyze with MAT/VisualVM**: Look for:
   - Large collections
   - Unnecessary object retention
   - ClassLoader leaks
4. **Check thread dumps**: For thread-local leaks
5. **Review code**: For common leak patterns

#### Q: Explain the double-checked locking pattern in Java
```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {  // First check (no locking)
            synchronized (Singleton.class) {
                if (instance == null) {  // Second check (with locking)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

## Performance Tuning Checklist

1. **Measure Baseline**
   - CPU usage
   - Memory usage
   - Response times
   - Throughput

2. **Identify Bottlenecks**
   - CPU-bound operations
   - I/O operations
   - Memory usage
   - Lock contention

3. **Apply Fixes**
   - Algorithm optimization
   - Data structure optimization
   - Concurrency improvements
   - Memory management

4. **Verify Improvements**
   - A/B testing
   - Load testing
   - Monitoring in production

## Final Notes

### When to Use Which GC
| GC | Use Case | Pause Time Goal | Throughput |
|----|----------|-----------------|------------|
| Serial | Small apps, single CPU | Medium | Medium |
| Parallel | Batch processing | High | High |
| CMS | Low pause time req. | Low | Medium |
| G1 | Balanced | Low | High |
| ZGC | Large heaps, low latency | Very Low | High |
| Shenandoah | Balanced, low latency | Very Low | High |

### Common JVM Flags for Production
```bash
# Memory
-Xms4g -Xmx4g
-XX:MaxMetaspaceSize=512m
-XX:MaxDirectMemorySize=1g

# GC (G1 example)
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:InitiatingHeapOccupancyPercent=45

# Logging
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-XX:+PrintGCTimeStamps
-Xloggc:/path/to/gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=5
-XX:GCLogFileSize=20M

# Dump on OOM
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/path/to/dump.hprof

# Other
-XX:+OptimizeStringConcat
-XX:+UseStringDeduplication
-XX:+UseCompressedOops
-XX:+UseCompressedClassPointers
```

Remember that JVM tuning is highly application-specific. Always test changes in a staging environment before applying them to production.
