# Java Stream API - Coding Interview Questions

## Table of Contents
- [Basic Level](#basic-level)
- [Intermediate Level](#intermediate-level)
- [Advanced Level](#advanced-level)
- [Real-world Scenarios](#real-world-scenarios)
- [Performance Considerations](#performance-considerations)

## Basic Level

### 1. Find the sum of all elements in a list
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
int sum = numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
// Output: 15
```

### 2. Convert list of strings to uppercase
```java
List<String> names = Arrays.asList("alice", "bob", "charlie");
List<String> upperCaseNames = names.stream()
                                 .map(String::toUpperCase)
                                 .collect(Collectors.toList());
// Output: [ALICE, BOB, CHARLIE]
```

### 3. Find the maximum number in a list
```java
List<Integer> numbers = Arrays.asList(10, 5, 20, 15, 30);
int max = numbers.stream()
                .max(Integer::compare)
                .orElse(0);
// Output: 30
```

### 4. Filter even numbers from a list
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evens = numbers.stream()
                           .filter(n -> n % 2 == 0)
                           .collect(Collectors.toList());
// Output: [2, 4, 6]
```

### 5. Remove duplicates from a list
```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 4, 4, 5);
List<Integer> unique = numbers.stream()
                            .distinct()
                            .collect(Collectors.toList());
// Output: [1, 2, 3, 4, 5]
```

## Intermediate Level

### 1. Find the average of a list of numbers
```java
List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);
double average = numbers.stream()
                      .mapToInt(Integer::intValue)
                      .average()
                      .orElse(0.0);
// Output: 30.0
```

### 2. Group a list of objects by a property
```java
class Person {
    String name;
    String city;
    // constructor, getters, setters
}

List<Person> people = Arrays.asList(
    new Person("Alice", "New York"),
    new Person("Bob", "Boston"),
    new Person("Charlie", "New York")
);

Map<String, List<Person>> peopleByCity = people.stream()
    .collect(Collectors.groupingBy(Person::getCity));
```

### 3. Find the second largest number in a list
```java
List<Integer> numbers = Arrays.asList(10, 5, 20, 15, 30);
int secondLargest = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .skip(1)
    .findFirst()
    .orElse(-1);
// Output: 20
```

### 4. Merge two lists and sort them
```java
List<Integer> list1 = Arrays.asList(1, 3, 5);
List<Integer> list2 = Arrays.asList(2, 4, 6);

List<Integer> merged = Stream.concat(list1.stream(), list2.stream())
    .sorted()
    .collect(Collectors.toList());
// Output: [1, 2, 3, 4, 5, 6]
```

### 5. Check if all elements match a condition
```java
List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);
boolean allEven = numbers.stream()
    .allMatch(n -> n % 2 == 0);
// Output: true
```

## Advanced Level

### 1. Find the first non-repeated character in a string
```java
String input = "aabbccde";
Character result = input.chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
    .entrySet().stream()
    .filter(entry -> entry.getValue() == 1)
    .map(Map.Entry::getKey)
    .findFirst()
    .orElse(null);
// Output: 'd'
```

### 2. Find the longest string in a list
```java
List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
String longest = words.stream()
    .reduce((word1, word2) -> 
        word1.length() > word2.length() ? word1 : word2)
    .orElse("");
// Output: "banana"
```

### 3. Find the frequency of each character in a string
```java
String input = "programming";
Map<Character, Long> frequency = input.chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));
// Output: {p=1, r=2, o=1, g=2, a=1, m=2, i=1, n=1}
```

### 4. Find the first element of the list after sorting
```java
List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9);
int first = numbers.stream()
    .sorted()
    .findFirst()
    .orElse(-1);
// Output: 1
```

### 5. Partition a list into even and odd
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// Output: {false=[1, 3, 5], true=[2, 4, 6]}
```

## Real-world Scenarios

### 1. Find the average salary of employees by department
```java
class Employee {
    String name;
    String department;
    double salary;
    // constructor, getters, setters
}

List<Employee> employees = // ...
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.averagingDouble(Employee::getSalary)
    ));
```

### 2. Find the top N highest paid employees
```java
List<Employee> topN = employees.stream()
    .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
    .limit(5) // top 5
    .collect(Collectors.toList());
```

### 3. Find duplicate elements in a list
```java
List<String> items = Arrays.asList("A", "B", "A", "C", "B", "D");
Set<String> duplicates = items.stream()
    .filter(i -> Collections.frequency(items, i) > 1)
    .collect(Collectors.toSet());
// Output: [A, B]
```

### 4. Find the most frequent element in a list
```java
List<String> items = Arrays.asList("A", "B", "A", "C", "B", "A");
String mostFrequent = items.stream()
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ))
    .entrySet().stream()
    .max(Map.Entry.comparingByValue())
    .map(Map.Entry::getKey)
    .orElse(null);
// Output: "A"
```

### 5. Find the intersection of two lists
```java
List<Integer> list1 = Arrays.asList(1, 2, 3, 4);
List<Integer> list2 = Arrays.asList(3, 4, 5, 6);

List<Integer> intersection = list1.stream()
    .filter(list2::contains)
    .collect(Collectors.toList());
// Output: [3, 4]
```

## Performance Considerations

1. **Use primitive streams** when working with primitives to avoid boxing/unboxing overhead:
   ```java
   // Instead of Stream<Integer>
   IntStream.range(0, 1000).sum();
   ```

2. **Use parallel streams** for large datasets when operations are independent:
   ```java
   List<Integer> result = largeList.parallelStream()
       .filter(x -> x % 2 == 0)
       .collect(Collectors.toList());
   ```

3. **Avoid stateful operations** like `sorted()` and `distinct()` in the middle of the pipeline:
   ```java
   // Less efficient
   list.stream().sorted().filter(x -> x > 10).collect(Collectors.toList());
   
   // More efficient
   list.stream().filter(x -> x > 10).sorted().collect(Collectors.toList());
   ```

4. **Use `findFirst()`** instead of `findAny()` when order matters.

5. **Prefer method references** over lambda expressions for better readability and potential performance benefits:
   ```java
   // Instead of: .map(x -> x.toString())
   .map(Object::toString)
   ```

## Common Pitfalls

1. **Streams are single-use** - Once a terminal operation is called, the stream is consumed.
2. **Lazy evaluation** - Intermediate operations are only executed when a terminal operation is invoked.
3. **Infinite streams** - Be cautious with operations that can create infinite streams like `Stream.iterate()` or `Stream.generate()`.
4. **Exception handling** - Handle checked exceptions properly in lambda expressions.
5. **Stateful operations** - Be careful with stateful operations like `sorted()` and `distinct()` as they require buffering.

## Additional Resources
- [Java 8 Stream API Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)
- [Java 8 Stream Tutorial](https://www.baeldung.com/java-8-streams)
- [Java Stream API Cheat Sheet](https://www.baeldung.com/java-8-streams-cheatsheet)

---
*This guide provides a comprehensive collection of Java Stream API interview questions and answers, covering basic to advanced concepts with practical examples.*
