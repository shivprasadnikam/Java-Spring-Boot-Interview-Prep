# Java 8+ Features - Advanced Guide

## Lambda Expressions

### 1. Basic Syntax
```java
// Before Java 8
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running");
    }
};

// Java 8+
Runnable r = () -> System.out.println("Running");
```

### 2. Functional Interfaces
**Q: What is @FunctionalInterface?**
- An interface with exactly one abstract method
- Can have multiple default/static methods
- Examples: Runnable, Callable, Comparator

```java
@FunctionalInterface
interface StringProcessor {
    String process(String str);
    
    default void log(String str) {
        System.out.println("Processing: " + str);
    }
}
```

## Stream API

### 3. Stream Operations
**Q: Intermediate vs Terminal Operations**
- **Intermediate**: filter(), map(), sorted(), distinct()
- **Terminal**: forEach(), collect(), reduce(), count()

```java
List<String> names = Arrays.asList("John", "Alice", "Bob", "Anna");

// Get unique names starting with 'A', sorted, and collect to list
List<String> result = names.stream()
    .filter(name -> name.startsWith("A"))
    .distinct()
    .sorted()
    .collect(Collectors.toList());
```

### 4. Parallel Streams
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

// Sequential stream
int sum = numbers.stream().reduce(0, Integer::sum);

// Parallel stream
int parallelSum = numbers.parallelStream().reduce(0, Integer::sum);
```

## Optional Class

### 5. Handling Nulls
```java
public String getUserName(User user) {
    return Optional.ofNullable(user)
        .map(User::getName)
        .orElse("Default");
}

// Chaining Optionals
public String getStreetName(Order order) {
    return Optional.ofNullable(order)
        .map(Order::getCustomer)
        .map(Customer::getAddress)
        .map(Address::getStreet)
        .orElse("Unknown");
}
```

## Date/Time API (java.time)

### 6. Working with Dates
```java
// Current date
LocalDate today = LocalDate.now();

// Specific date
LocalDate date = LocalDate.of(2023, Month.SEPTEMBER, 16);

// Date manipulation
LocalDate nextWeek = today.plusWeeks(1);

// Date difference
Period period = Period.between(today, date);
```

## Default and Static Methods

### 7. Interface Evolution
```java
interface Vehicle {
    String getBrand();
    
    // Default method
    default String turnAlarmOn() {
        return "Turning vehicle alarm on";
    }
    
    // Static method
    static int getHorsePower(int rpm, int torque) {
        return (rpm * torque) / 5252;
    }
}
```

## Method References

### 8. Types of Method References
```java
// Static method
Function<String, Integer> parser = Integer::parseInt;

// Instance method of a particular object
String str = "Hello";
Supplier<Integer> length = str::length;

// Instance method of an arbitrary object
Function<String, String> upperCase = String::toUpperCase;

// Constructor reference
Supplier<List<String>> listSupplier = ArrayList::new;
```

## Collectors

### 9. Advanced Collectors
```java
List<Employee> employees = // ...

// Group by department
Map<Department, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// Calculate average salary by department
Map<Department, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.averagingDouble(Employee::getSalary)
    ));

// Partition employees by salary threshold
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 100000));
```

## Java 9-17 Features

### 10. Java 9-17 Highlights
```java
// Java 9: Factory methods for collections
List<String> list = List.of("a", "b", "c");
Set<String> set = Set.of("a", "b", "c");
Map<String, Integer> map = Map.of("a", 1, "b", 2);

// Java 10: Local variable type inference
var list = new ArrayList<String>();

// Java 11: String methods
String multiline = "A\nB\nC";
List<String> lines = multiline.lines().toList();

// Java 14: Switch expressions
String dayType = switch (day) {
    case "MON", "TUE", "WED", "THU", "FRI" -> "Weekday";
    case "SAT", "SUN" -> "Weekend";
    default -> throw new IllegalArgumentException("Invalid day: " + day);
};

// Java 15: Text blocks
String json = """
    {
        "name": "John",
        "age": 30
    }
    """;

// Java 16: Pattern matching for instanceof
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}

// Java 17: Sealed classes
public abstract sealed class Shape
    permits Circle, Rectangle, Triangle {
    // ...
}
```

## Performance Considerations

### 11. Stream Performance
- **Parallel Streams**: Use for large datasets and CPU-bound operations
- **Primitive Streams**: Use IntStream, LongStream, DoubleStream for better performance
- **Avoid Stateful Operations**: sorted(), distinct() can be expensive
- **Short-circuiting**: Use findFirst() instead of findAny() when order matters

### 12. Memory Efficiency
- **Reuse Collectors**: Store collectors in static final fields
- **Method References**: More efficient than lambdas in some cases
- **Avoid Autoboxing**: Use primitive streams when possible

## Common Pitfalls

### 13. Common Mistakes
1. **Infinite Streams**:
   ```java
   // Infinite stream - will run forever
   Stream.iterate(0, i -> i + 1).forEach(System.out::println);
   ```

2. **Stateful Lambda Expressions**:
   ```java
   // Bad - stateful lambda
   int[] counter = {0};
   list.forEach(e -> counter[0]++);
   
   // Good - use reduction
   int count = list.stream().mapToInt(e -> 1).sum();
   ```

3. **Null Handling with Optionals**:
   ```java
   // Bad - defeats the purpose of Optional
   if (optional.isPresent()) {
       return optional.get();
   } else {
       return "default";
   }
   
   // Good
   return optional.orElse("default");
   ```

## Interview Questions

### 14. Common Questions
1. **Q: Difference between map() and flatMap()?**
   ```java
   // map: transforms each element
   List<Integer> lengths = words.stream()
       .map(String::length)
       .collect(Collectors.toList());
   
   // flatMap: flattens nested structures
   List<String> chars = words.stream()
       .flatMap(word -> Arrays.stream(word.split("")))
       .collect(Collectors.toList());
   ```

2. **Q: How to create a custom collector?**
   ```java
   public static <T> Collector<T, ?, List<T>> toSortedList(Comparator<? super T> c) {
       return Collector.of(
           ArrayList::new,
           List::add,
           (left, right) -> { left.addAll(right); return left; },
           list -> { list.sort(c); return list; }
       );
   }
   ```

3. **Q: Difference between findFirst() and findAny()?**
   - findFirst(): Returns the first element (deterministic in parallel)
   - findAny(): Returns any element (more efficient in parallel)

## Practical Exercises

### 15. Coding Challenges
1. **Word Frequency Count**
   ```java
   // Count word frequencies in a list, ignoring case, sorted by count descending
   Map<String, Long> wordFreq = words.stream()
       .map(String::toLowerCase)
       .collect(Collectors.groupingBy(
           Function.identity(),
           Collectors.counting()
       ))
       .entrySet().stream()
       .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
       .collect(Collectors.toMap(
           Map.Entry::getKey,
           Map.Entry::getValue,
           (e1, e2) -> e1,
           LinkedHashMap::new
       ));
   ```

2. **Partition Primes**
   ```java
   // Partition numbers into primes and non-primes
   public Map<Boolean, List<Integer>> partitionPrimes(int n) {
       return IntStream.rangeClosed(2, n)
           .boxed()
           .collect(Collectors.partitioningBy(this::isPrime));
   }
   
   private boolean isPrime(int number) {
       int root = (int) Math.sqrt(number);
       return IntStream.rangeClosed(2, root)
           .noneMatch(i -> number % i == 0);
   }
   ```

## Best Practices

### 16. Code Style
1. **Method Chaining**: Keep streams readable with proper indentation
   ```java
   // Good
   List<String> result = items.stream()
       .filter(item -> item != null)
       .map(String::toUpperCase)
       .sorted()
       .collect(Collectors.toList());
   ```

2. **Avoid Side Effects**: Keep lambdas pure
   ```java
   // Bad - has side effect
   List<String> result = new ArrayList<>();
   items.forEach(item -> result.add(process(item)));
   
   // Good - no side effects
   List<String> result = items.stream()
       .map(this::process)
       .collect(Collectors.toList());
   ```

3. **Use Method References**: When they improve readability
   ```java
   // Instead of:
   .map(s -> s.toUpperCase())
   
   // Use:
   .map(String::toUpperCase)
   ```

## Performance Tuning

### 17. Stream Performance Tips
1. **Primitive Streams**: Use IntStream, LongStream, DoubleStream for better performance
2. **Avoid Auto-boxing**: Use mapToInt, mapToLong, etc.
3. **Limit Infinite Streams**: Always use limit() with infinite streams
4. **Parallel with Caution**: Only use parallel streams for CPU-intensive operations

### 18. Memory Efficiency
1. **Reuse Collectors**: Store collectors in static final fields
2. **Avoid Collecting to List**: Use forEach() when order doesn't matter
3. **Use Primitive Collections**: Consider Eclipse Collections or GS Collections for large datasets

## Java 17 Specifics

### 19. Sealed Classes
```java
public sealed interface Shape 
    permits Circle, Rectangle, Triangle {
    double area();
}

public final class Circle implements Shape {
    private final double radius;
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

### 20. Pattern Matching for switch
```java
String formatted = switch (obj) {
    case Integer i -> String.format("int %d", i);
    case String s -> String.format("String %s", s);
    case null, default -> "Unknown";
};
```

## Final Tips

### 21. Interview Preparation
1. **Practice Common Operations**: filtering, mapping, reducing, grouping
2. **Understand Lazy Evaluation**: Streams are lazy until terminal operation
3. **Know the Collectors API**: groupingBy, partitioningBy, joining, etc.
4. **Be Ready for Edge Cases**: empty streams, null values, parallel streams

### 22. Common Gotchas
1. **Streams Are Not Reusable**: Once consumed, a stream cannot be reused
2. **Order Matters**: Some operations (like filter) should come before others
3. **Watch for Side Effects**: Avoid modifying the source of a stream
4. **Performance Traps**: Be aware of operations that require multiple passes (like sorted, distinct)
