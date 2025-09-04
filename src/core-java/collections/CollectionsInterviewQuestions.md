# Collections Framework Interview Questions 📚

## **Most Asked Questions (12+ LPA Level)**

### **Q1: ArrayList vs LinkedList - When to use each?**
**Answer:**
- **ArrayList**: Better for random access (get/set operations), less memory overhead
- **LinkedList**: Better for frequent insertions/deletions, especially at beginning/middle

**Performance Comparison:**
- ArrayList: get() = O(1), add() = O(1) amortized, insert() = O(n)
- LinkedList: get() = O(n), add() = O(1), insert() = O(1)

### **Q2: How does HashMap handle collisions?**
**Answer:**
1. **Before Java 8**: Uses chaining with linked list
2. **Java 8+**: Uses balanced tree (Red-Black tree) when bucket has 8+ elements
3. **Hash function**: Uses key's hashCode() and applies additional hashing
4. **Load factor**: Default 0.75 - rehashes when 75% full

### **Q3: ConcurrentHashMap vs HashMap vs Hashtable?**
**Answer:**
- **HashMap**: Not thread-safe, allows null keys/values, best performance
- **Hashtable**: Thread-safe (synchronized methods), no null keys/values, poor performance
- **ConcurrentHashMap**: Thread-safe (segment locking), no null keys/values, better performance than Hashtable

### **Q4: Explain fail-fast vs fail-safe iterators**
**Answer:**
- **Fail-fast**: Throw ConcurrentModificationException if collection modified during iteration (ArrayList, HashMap)
- **Fail-safe**: Work on copy of collection, don't throw exception (ConcurrentHashMap, CopyOnWriteArrayList)

### **Q5: How to make a custom object work as HashMap key?**
**Answer:**
```java
public class Employee {
    private String name;
    private int id;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return id == employee.id && Objects.equals(name, employee.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
```

**Rules:**
- If `equals()` returns true, `hashCode()` must return same value
- If `hashCode()` returns same value, `equals()` may return false
- Immutable fields should be used in both methods

## **Coding Questions**

### **Q6: Remove duplicates from ArrayList**
```java
// Method 1: Using LinkedHashSet (maintains order)
List<String> list = Arrays.asList("a", "b", "a", "c", "b");
List<String> unique = new ArrayList<>(new LinkedHashSet<>(list));

// Method 2: Using Java 8 Streams
List<String> unique = list.stream()
                         .distinct()
                         .collect(Collectors.toList());
```

### **Q7: Sort employees by multiple criteria**
```java
List<Employee> employees = getEmployees();

// Sort by age, then by name
employees.sort(Comparator.comparingInt(Employee::getAge)
                        .thenComparing(Employee::getName));

// Sort by salary descending, then by name ascending
employees.sort(Comparator.comparingDouble(Employee::getSalary).reversed()
                        .thenComparing(Employee::getName));
```

### **Q8: Find second highest element in list**
```java
List<Integer> numbers = Arrays.asList(1, 5, 3, 9, 7, 9, 2);

Integer secondHighest = numbers.stream()
    .distinct()
    .sorted(Collections.reverseOrder())
    .skip(1)
    .findFirst()
    .orElse(null);
```

## **Advanced Questions**

### **Q9: Implement LRU Cache using LinkedHashMap**
```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // accessOrder = true
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

### **Q10: Thread-safe operations on collections**
```java
// Synchronized collections
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
Map<String, String> syncMap = Collections.synchronizedMap(new HashMap<>());

// Concurrent collections (better performance)
List<String> concurrentList = new CopyOnWriteArrayList<>();
Map<String, String> concurrentMap = new ConcurrentHashMap<>();

// Proper iteration on synchronized collections
synchronized(syncList) {
    Iterator<String> it = syncList.iterator();
    while(it.hasNext()) {
        System.out.println(it.next());
    }
}
```
