# Java Collections - Advanced Interview Questions (3+ YOE)

## Core Concepts

### 1. HashMap Internals
**Q: Explain the internal working of HashMap in Java 8+**
- Uses array of Node<K,V> (buckets)
- Uses hashCode() to determine bucket
- Handles collisions using linked list (converts to Tree if threshold > 8)
- Load factor (default 0.75) triggers rehashing
- Thread-unsafe, allows one null key

**Q: What is the significance of the threshold value in HashMap?**
- Default threshold = capacity * load factor (16 * 0.75 = 12)
- When size > threshold, HashMap resizes (2x)
- Affects performance - too high causes more collisions, too low wastes memory

### 2. Concurrent Collections
**Q: Difference between ConcurrentHashMap and Collections.synchronizedMap()**
- **ConcurrentHashMap**: 
  - Thread-safe without locking entire map
  - Uses segment locking (Java 7) or Node locking (Java 8+)
  - Higher throughput for concurrent access
  - Doesn't throw ConcurrentModificationException
- **SynchronizedMap**:
  - Wraps map with synchronized methods
  - Locks entire map during operations
  - Lower performance under contention

**Q: How does ConcurrentHashMap achieve thread-safety?**
- Java 7: Segment locking (16 segments by default)
- Java 8+: 
  - Uses Node[] table with volatile fields
  - Synchronizes on first node of bucket
  - Uses synchronized blocks instead of ReentrantLock
  - Implements lock striping for better concurrency

## Performance & Optimization

### 3. Collection Performance
**Q: Compare ArrayList vs LinkedList performance**
- **ArrayList**:
  - O(1) for get/set (random access)
  - O(n) for add/remove in middle
  - Better for read-heavy operations
  - Contiguous memory allocation
- **LinkedList**:
  - O(n) for get/set
  - O(1) for add/remove at ends
  - Better for frequent insertions/deletions
  - Extra memory for node pointers

**Q: When would you use TreeMap over HashMap?**
- When you need:
  - Sorted iteration of keys
  - Range queries (subMap, headMap, tailMap)
  - Predictable iteration order
- TreeMap provides O(log n) time complexity for most operations
- Uses Red-Black tree internally

## Advanced Topics

### 4. Fail-Fast vs Fail-Safe Iterators
**Q: Explain with examples**
- **Fail-Fast** (ArrayList, HashMap):
  ```java
  List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
  for (String s : list) {
      list.remove(s); // Throws ConcurrentModificationException
  }
  ```
  - Uses modCount to detect concurrent modifications
  - Throws ConcurrentModificationException

- **Fail-Safe** (ConcurrentHashMap, CopyOnWriteArrayList):
  ```java
  List<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));
  for (String s : list) {
      list.remove(s); // No exception
  }
  ```
  - Operates on snapshot of collection
  - No ConcurrentModificationException

### 5. Java 8+ Collection Enhancements
**Q: What are the new methods in Java 8 Collections?**
- compute(), computeIfAbsent(), computeIfPresent()
- merge()
- forEach()
- removeIf()
- replaceAll()
- getOrDefault()

## Practical Scenarios

### 6. Memory Leaks in Collections
**Q: How can collections cause memory leaks?**
- Holding object references when no longer needed
- Using mutable objects as keys in HashMap
- Not clearing collections in session objects
- Static collections holding objects

**Solution**:
- Use WeakHashMap for caches
- Clear collections when done
- Use remove() instead of setting to null
- Consider using soft/weak references

### 7. Custom Collections
**Q: How would you implement a Least Recently Used (LRU) Cache?**
```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

## Best Practices

### 8. Collection Initialization
**Q: What's the most efficient way to create a small, fixed-size list?**
```java
// Best for small, fixed-size lists
List<String> list = Arrays.asList("a", "b", "c");

// Java 9+ - immutable list
List<String> list = List.of("a", "b", "c");

// For mutable list with known size
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
```

### 9. Choosing the Right Collection
**Q: How do you decide which collection to use?**
1. Need key-value pairs?
   - Yes → Need ordering? 
     - Yes → TreeMap
     - No → Need thread-safety?
       - Yes → ConcurrentHashMap
       - No → HashMap
2. Need to store only values?
   - Need uniqueness? 
     - Yes → Need ordering?
       - Yes → TreeSet
       - No → Need thread-safety?
         - Yes → CopyOnWriteArraySet/ConcurrentHashMap.newKeySet()
         - No → HashSet
   - No → Need random access by index?
     - Yes → ArrayList
     - No → Frequent insertions/deletions?
       - Yes → LinkedList
       - No → ArrayList

## Performance Tuning

### 10. Collection Optimization
**Q: How would you optimize a HashMap with millions of entries?**
1. Set appropriate initial capacity
   ```java
   // Prevents resizing if size is known
   Map<String, String> map = new HashMap<>(1_000_000);
   ```
2. Use appropriate load factor
   - Higher load factor → less memory, more collisions
   - Lower load factor → more memory, fewer collisions
3. Use immutable keys
4. Implement proper hashCode() and equals()
5. Consider using specialized collections (e.g., Trove, FastUtil)
6. For primitive types, consider primitive collections
7. Use parallel streams for bulk operations when appropriate

## Concurrency Patterns

### 11. Thread-Safe Collections
**Q: Compare CopyOnWriteArrayList vs Collections.synchronizedList()**
- **CopyOnWriteArrayList**:
  - Creates new copy on modification
  - No locks during iteration
  - Better for read-heavy scenarios
  - High memory overhead for write operations
  ```java
  List<String> list = new CopyOnWriteArrayList<>();
  ```
  
- **SynchronizedList**:
  - Locks entire list during operations
  - Better for write-heavy scenarios
  - Lower memory overhead
  - Blocks during iteration
  ```java
  List<String> list = Collections.synchronizedList(new ArrayList<>());
  ```

## Real-world Scenarios

### 12. Caching Strategies
**Q: How would you implement an in-memory cache with TTL?**
```java
public class TTLCache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlInMillis;
    
    public TTLCache(long ttlInMillis) {
        this.ttlInMillis = ttlInMillis;
    }
    
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        
        if (System.currentTimeMillis() - entry.timestamp > ttlInMillis) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    private static class CacheEntry<V> {
        final V value;
        final long timestamp;
        
        CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
```

## Common Pitfalls

### 13. Common Collection Mistakes
**Q: What are some common pitfalls when working with Java Collections?**
1. Using raw types instead of generics
2. Modifying collections during iteration
3. Not implementing equals() and hashCode() correctly
4. Using thread-unsafe collections in concurrent environments
5. Memory leaks from strong references in caches
6. Not considering the cost of auto-boxing with primitive types
7. Using LinkedList when ArrayList would be more efficient
8. Not setting initial capacity for large collections
9. Using Vector/Hashtable instead of ConcurrentHashMap/Collections.synchronizedList()
10. Not considering the cost of defensive copies

## Java 17 Updates

### 14. New Collection Features
**Q: What's new in Java 17 Collections?**
- New methods in Collection interface:
  - `toList()` - returns unmodifiable list
  - `toArray(IntFunction<T[]> generator)` - array conversion
- New methods in List:
  - `of(E... elements)` - immutable list creation
  - `copyOf(Collection)` - immutable copy
- Sealed classes for collection implementations
- Pattern matching for instanceof with collections
- Enhanced null handling with Optional in stream operations

## Performance Comparison

### 15. Collection Performance Characteristics
| Operation  | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|------------|-----------|------------|---------|---------|---------|---------|
| get()      | O(1)      | O(n)       | O(1)    | O(log n)| O(1)    | O(log n)|
| add()      | O(1)      | O(1)       | O(1)    | O(log n)| O(1)    | O(log n)|
| remove()   | O(n)      | O(1)       | O(1)    | O(log n)| O(1)    | O(log n)|
| contains() | O(n)      | O(n)       | O(1)    | O(log n)| O(1)    | O(log n)|
| next()     | O(1)      | O(1)       | O(h/n)  | O(log n)| O(h/n)  | O(log n)|

## Memory Footprint

### 16. Memory Considerations
**Q: How do different collections impact memory usage?**
- **ArrayList**: 4 bytes per reference + array overhead
- **LinkedList**: 24 bytes per element (12B header + 8B next + 4B prev)
- **HashMap**: 32 bytes per entry (array + entry objects)
- **HashSet**: Backed by HashMap (16 bytes overhead per element)
- **ArrayDeque**: Most memory-efficient queue implementation
- **EnumSet/EnumMap**: Extremely compact for enum keys

## Garbage Collection Impact

### 17. GC Considerations
**Q: How do collections affect garbage collection?**
- Large collections can cause GC pauses
- Long-lived collections can lead to promotion to old generation
- Weak/Soft references can help with cache implementations
- Consider using -XX:+UseG1GC for better large heap handling
- Monitor with JVisualVM or similar tools

## Practical Exercises

### 18. Coding Challenges
1. Implement a thread-safe BlockingQueue
2. Create a MultiMap (Map<K, List<V>>) with atomic operations
3. Implement a Least Frequently Used (LFU) cache
4. Write a method to find duplicates in a collection
5. Implement a custom iterator that filters elements

## Interview Tips

### 19. Common Interview Questions
1. How does HashMap handle collisions?
2. Difference between HashMap and ConcurrentHashMap?
3. When would you use TreeMap over HashMap?
4. How does ArrayList grow dynamically?
5. Explain the fail-fast iterator concept
6. How would you sort a collection of objects?
7. What's the difference between Set and List?
8. How do you make a collection unmodifiable?
9. What's the difference between Iterator and ListIterator?
10. How would you implement a priority queue?
