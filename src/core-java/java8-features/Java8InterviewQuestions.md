# Java 8+ Features Interview Questions ☕

## **🚀 Most Asked Java 8+ Questions (2025 Edition)**

## **Stream API (Most Important)**

### **Q1: Explain Stream API and its benefits**
**Answer:**
- **What**: Functional-style operations on collections
- **Benefits**: Readable code, lazy evaluation, parallel processing, immutable operations

```java
List<String> names = Arrays.asList("John", "Jane", "Jack", "Jill");

// Traditional approach
List<String> result = new ArrayList<>();
for (String name : names) {
    if (name.startsWith("J") && name.length() > 3) {
        result.add(name.toUpperCase());
    }
}

// Stream approach
List<String> result = names.stream()
    .filter(name -> name.startsWith("J"))
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### **Q2: Intermediate vs Terminal operations**
**Answer:**
- **Intermediate**: Return Stream, lazy evaluation (filter, map, sorted)
- **Terminal**: Return result, trigger execution (collect, forEach, reduce)

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

numbers.stream()
    .filter(n -> n > 2)     // Intermediate
    .map(n -> n * 2)        // Intermediate
    .sorted()               // Intermediate
    .collect(Collectors.toList()); // Terminal - execution starts here
```

### **Q3: Common Stream operations with examples**
**Answer:**
```java
List<Employee> employees = getEmployees();

// Filtering
List<Employee> seniors = employees.stream()
    .filter(emp -> emp.getAge() > 30)
    .collect(Collectors.toList());

// Mapping
List<String> names = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.toList());

// Grouping
Map<String, List<Employee>> byDepartment = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

// Finding
Optional<Employee> highestPaid = employees.stream()
    .max(Comparator.comparing(Employee::getSalary));

// Reducing
double totalSalary = employees.stream()
    .mapToDouble(Employee::getSalary)
    .sum();

// Parallel processing
List<String> processedNames = employees.parallelStream()
    .map(this::expensiveOperation)
    .collect(Collectors.toList());
```

## **Lambda Expressions**

### **Q4: Lambda expressions vs Anonymous classes**
**Answer:**
```java
// Anonymous class
Runnable r1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello World");
    }
};

// Lambda expression
Runnable r2 = () -> System.out.println("Hello World");

// Functional interface with lambda
Comparator<String> comp = (s1, s2) -> s1.compareTo(s2);
// Method reference (even cleaner)
Comparator<String> comp2 = String::compareTo;
```

### **Q5: Method References types**
**Answer:**
```java
List<String> names = Arrays.asList("John", "Jane", "Jack");

// Static method reference
names.stream().map(String::toUpperCase);

// Instance method reference
names.stream().map(String::length);

// Constructor reference
names.stream().map(StringBuilder::new);

// Instance method of particular object
PrintStream out = System.out;
names.forEach(out::println);
```

## **Optional Class**

### **Q6: Optional best practices**
**Answer:**
```java
// DON'T do this
public Optional<String> getName() {
    return Optional.of(null); // Will throw NPE
}

// DO this
public Optional<String> getName() {
    return name != null ? Optional.of(name) : Optional.empty();
    // Or simply: return Optional.ofNullable(name);
}

// Usage patterns
Optional<String> name = getName();

// Bad - defeats the purpose
if (name.isPresent()) {
    System.out.println(name.get());
}

// Good - functional approach
name.ifPresent(System.out::println);

// Chaining operations
String result = name
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase)
    .orElse("DEFAULT");

// Complex chaining
Optional<String> email = getUser(id)
    .flatMap(User::getProfile)
    .map(Profile::getEmail);
```

## **Date/Time API**

### **Q7: New Date/Time API vs old Date**
**Answer:**
```java
// Old API problems - mutable, not thread-safe, confusing
Date date = new Date();
date.setTime(System.currentTimeMillis()); // Mutable!

// New API - immutable, thread-safe, clear
LocalDate today = LocalDate.now();
LocalDateTime now = LocalDateTime.now();
ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));

// Operations
LocalDate tomorrow = today.plusDays(1);
LocalDate lastWeek = today.minusWeeks(1);

// Formatting
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
String formattedDate = today.format(formatter);

// Parsing
LocalDate parsed = LocalDate.parse("25-12-2023", formatter);

// Working with different time zones
ZonedDateTime istTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
ZonedDateTime utcTime = istTime.withZoneSameInstant(ZoneId.of("UTC"));
```

## **Interface Default Methods**

### **Q8: Default methods and diamond problem**
**Answer:**
```java
interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

interface B {
    default void hello() {
        System.out.println("Hello from B");
    }
}

// Diamond problem - must override
class C implements A, B {
    @Override
    public void hello() {
        A.super.hello(); // Explicitly call A's method
        // Or provide custom implementation
    }
}
```

## **Comparator vs Comparable**

### **Q9: Comparator vs Comparable – when to use which?**
**Answer:**
```java
// Comparable - natural ordering, implemented by the class itself
public class Employee implements Comparable<Employee> {
    private String name;
    private int age;
    private double salary;
    
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.age, other.age); // Natural order by age
    }
}

// Comparator - external comparison logic, multiple sorting strategies
List<Employee> employees = getEmployees();

// Sort by salary (descending)
Comparator<Employee> bySalary = Comparator.comparingDouble(Employee::getSalary).reversed();
employees.sort(bySalary);

// Sort by multiple criteria
Comparator<Employee> multiCriteria = Comparator
    .comparing(Employee::getDepartment)
    .thenComparing(Employee::getSalary, Comparator.reverseOrder())
    .thenComparing(Employee::getName);

employees.sort(multiCriteria);

// Method reference usage
employees.sort(Comparator.comparing(Employee::getName));
```

**When to use:**
- **Comparable**: Single, natural ordering for the class
- **Comparator**: Multiple sorting strategies, external sorting logic

## **Collectors**

### **Q10: Advanced Collectors usage**
**Answer:**
```java
List<Employee> employees = getEmployees();

// Grouping by multiple fields
Map<String, Map<String, List<Employee>>> grouped = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.groupingBy(Employee::getLevel)
    ));

// Partitioning
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(emp -> emp.getSalary() > 50000));

// Custom collector
String names = employees.stream()
    .map(Employee::getName)
    .collect(Collectors.joining(", ", "[", "]"));

// Statistics
DoubleSummaryStatistics salaryStats = employees.stream()
    .collect(Collectors.summarizingDouble(Employee::getSalary));

System.out.println("Average: " + salaryStats.getAverage());
System.out.println("Max: " + salaryStats.getMax());
```

## **CompletableFuture**

### **Q10: CompletableFuture – async programming**
**Answer:**
CompletableFuture provides asynchronous programming capabilities with better composability than Future.

**Key differences from Future:**
- Non-blocking operations
- Composable with thenApply, thenCompose
- Exception handling with exceptionally
- Can be completed manually

### **Q11: Asynchronous programming with CompletableFuture**
**Answer:**
```java
// Simple async operation
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        // Simulate long-running task
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        return "Hello";
    });

// Chaining operations
CompletableFuture<String> result = future
    .thenApply(s -> s + " World")
    .thenApply(String::toUpperCase);

// Combining multiple futures
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> combined = future1
    .thenCombine(future2, (s1, s2) -> s1 + " " + s2);

// Exception handling
CompletableFuture<String> withErrorHandling = CompletableFuture
    .supplyAsync(() -> {
        if (Math.random() > 0.5) {
            throw new RuntimeException("Random error");
        }
        return "Success";
    })
    .exceptionally(throwable -> {
        System.err.println("Error: " + throwable.getMessage());
        return "Default value";
    });

// Waiting for all futures
CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);
allOf.thenRun(() -> System.out.println("All completed"));
```
