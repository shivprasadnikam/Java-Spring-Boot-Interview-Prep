# Java OOP - Advanced Interview Questions (3+ YOE)

## Core OOP Concepts

### 1. Encapsulation
**Q: How does Java achieve encapsulation?**
- Using private fields and public getter/setter methods
- Example:
  ```java
  public class Account {
      private double balance;
      
      public double getBalance() {
          return balance;
      }
      
      public void deposit(double amount) {
          if (amount > 0) {
              balance += amount;
          }
      }
  }
  ```

### 2. Inheritance & Polymorphism
**Q: Explain method overriding vs overloading**
- **Overriding**: 
  - Runtime polymorphism
  - Same method signature in child class
  - `@Override` annotation
- **Overloading**:
  - Compile-time polymorphism
  - Same method name, different parameters
  - Can vary return type and access modifiers

## Advanced OOP Concepts

### 3. Composition over Inheritance
**Q: Why prefer composition?**
- More flexible
- Avoids fragile base class problem
- Example:
  ```java
  // Instead of:
  class Car extends Engine {}
  
  // Prefer:
  class Car {
      private Engine engine;
  }
  ```

### 4. SOLID Principles
**Q: Explain with examples**
1. **S**ingle Responsibility
   ```java
   // Bad
   class User {
       void saveToDatabase() {}
       void sendEmail() {}
   }
   
   // Good
   class User {}
   class UserRepository { void save(User user) {} }
   class EmailService { void sendEmail(User user) {} }
   ```

2. **O**pen/Closed
   ```java
   // Bad
   class Rectangle {
       void draw() { /* draw rectangle */ }
   }
   
   // Good
   interface Shape { void draw(); }
   class Rectangle implements Shape {}
   class Circle implements Shape {}
   ```

3. **L**iskov Substitution
   ```java
   // Bad
   class Bird {
       void fly() {}
   }
   class Penguin extends Bird {} // Can't fly!
   
   // Good
   interface Bird {}
   interface FlyingBird extends Bird {
       void fly();
   }
   ```

4. **I**nterface Segregation
   ```java
   // Bad
   interface Worker {
       void work();
       void eat();
   }
   
   // Good
   interface Workable { void work(); }
   interface Eatable { void eat(); }
   ```

5. **D**ependency Inversion
   ```java
   // Bad
   class LightBulb {
       void turnOn() {}
   }
   
   // Good
   interface Switchable {
       void turnOn();
   }
   class LightBulb implements Switchable {}
   ```

## Design Patterns

### 5. Singleton Pattern
**Q: Thread-safe singleton implementations**
1. **Eager Initialization**
   ```java
   public class Singleton {
       private static final Singleton INSTANCE = new Singleton();
       
       private Singleton() {}
       
       public static Singleton getInstance() {
           return INSTANCE;
       }
   }
   ```

2. **Double-Checked Locking**
   ```java
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
   ```

3. **Enum Singleton**
   ```java
   public enum Singleton {
       INSTANCE;
       
       public void doSomething() {}
   }
   ```

### 6. Factory Pattern
```java
interface Animal {
    String makeSound();
}

class Dog implements Animal {
    public String makeSound() { return "Woof!"; }
}

class AnimalFactory {
    public static Animal createAnimal(String type) {
        return switch(type.toLowerCase()) {
            case "dog" -> new Dog();
            case "cat" -> new Cat();
            default -> throw new IllegalArgumentException("Unknown animal");
        };
    }
}
```

## Advanced Java Features

### 7. Records (Java 16+)
```java
public record Point(int x, int y) {
    // Compact constructor for validation
    public Point {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be positive");
        }
    }
    
    // Additional methods
    public double distanceFromOrigin() {
        return Math.hypot(x, y);
    }
}
```

### 8. Sealed Classes (Java 17+)
```java
public sealed interface Shape 
    permits Circle, Rectangle, Triangle {
    double area();
}

public final class Circle implements Shape {
    private final double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
```

## Object Class Methods

### 9. equals() and hashCode()
**Q: Contract between equals() and hashCode()**
- If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true
- If `a.hashCode() == b.hashCode()`, then `a.equals(b)` may or may not be true
- Example implementation:
  ```java
  public class Person {
      private final String name;
      private final int age;
      
      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          Person person = (Person) o;
          return age == person.age && 
                 Objects.equals(name, person.name);
      }
      
      @Override
      public int hashCode() {
          return Objects.hash(name, age);
      }
  }
  ```

### 10. clone() Method
**Q: Deep vs Shallow Copy**
- **Shallow Copy**: Copies references to objects
- **Deep Copy**: Creates new copies of all objects
- Example:
  ```java
  class Address implements Cloneable {
      String city;
      
      @Override
      protected Object clone() throws CloneNotSupportedException {
          return super.clone();
      }
  }
  
  class Person implements Cloneable {
      String name;
      Address address;
      
      @Override
      protected Object clone() throws CloneNotSupportedException {
          Person cloned = (Person) super.clone();
          cloned.address = (Address) address.clone(); // Deep copy
          return cloned;
      }
  }
  ```

## Advanced Topics

### 11. Immutable Objects
**Q: How to create immutable objects?**
1. Make class final
2. Make all fields final and private
3. No setter methods
4. Return defensive copies of mutable objects
5. Make constructors private, use factory methods

```java
public final class ImmutablePerson {
    private final String name;
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, List<String> hobbies) {
        this.name = name;
        this.hobbies = new ArrayList<>(hobbies);
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getHobbies() {
        return new ArrayList<>(hobbies);
    }
}
```

### 12. Builder Pattern
```java
public class Pizza {
    private final String dough;
    private final String sauce;
    private final List<String> toppings;
    
    private Pizza(Builder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.toppings = builder.toppings;
    }
    
    public static class Builder {
        private final String dough;
        private String sauce = "";
        private List<String> toppings = new ArrayList<>();
        
        public Builder(String dough) {
            this.dough = dough;
        }
        
        public Builder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }
        
        public Builder addTopping(String topping) {
            toppings.add(topping);
            return this;
        }
        
        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Usage:
Pizza pizza = new Pizza.Builder("thin")
    .sauce("tomato")
    .addTopping("cheese")
    .addTopping("mushrooms")
    .build();
```

## Design Principles

### 13. Law of Demeter
**Q: What is it and why is it important?**
- Principle of least knowledge
- A method should only call:
  1. Methods on its own class
  2. Methods on objects it creates
  3. Methods on its parameters
  4. Methods on its instance variables
- Example:
  ```java
  // Bad
  class Customer {
      Wallet wallet;
      
      public float getWalletMoney() {
          return wallet.getMoney();
      }
  }
  
  // Good
  class Customer {
      private Wallet wallet;
      
      public float pay(float amount) {
          return wallet.withdraw(amount);
      }
  }
  ```

### 14. Favor Composition over Inheritance
**Q: When to use each?**
- **Use Inheritance** when:
  - There's a clear "is-a" relationship
  - You need to override methods
  - You want to provide a common interface
  
- **Use Composition** when:
  - You want to reuse code without tight coupling
  - You need to change behavior at runtime
  - You want to use multiple behaviors (multiple inheritance)

## Java-Specific Features

### 15. Default Methods in Interfaces
```java
interface PaymentProcessor {
    void processPayment(double amount);
    
    default void validatePayment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        validatePayment(amount);
        // Process payment
    }
}
```

### 16. Static and Default Methods in Interfaces
- **Static methods**: Utility methods related to the interface
- **Default methods**: Provide default implementation
- Example:
  ```java
  interface Logger {
      void log(String message);
      
      static Logger getConsoleLogger() {
          return message -> System.out.println("CONSOLE: " + message);
      }
      
      default void logError(String error) {
          log("ERROR: " + error);
      }
  }
  ```

## Common Interview Questions

### 17. Abstract Class vs Interface
| Feature          | Abstract Class         | Interface                |
|------------------|------------------------|--------------------------|
| Multiple Inheritance | No                 | Yes                     |
| State            | Can have state         | No instance fields (until Java 8) |
| Constructors     | Can have constructors  | No constructors          |
| Default Methods  | Yes                    | Yes (since Java 8)       |
| Access Modifiers | Any                    | public (implicitly)      |
| When to use      | Share code             | Define contract          |

### 18. Covariant Return Types
```java
class Animal {
    Animal reproduce() {
        return new Animal();
    }
}

class Dog extends Animal {
    @Override
    Dog reproduce() {  // Covariant return type
        return new Dog();
    }
}
```

## Best Practices

### 19. Effective Java Guidelines
1. Favor composition over inheritance
2. Minimize mutability
3. Prefer interfaces to abstract classes
4. Use checked exceptions for recoverable conditions
5. Document thread safety
6. Make defensive copies when needed
7. Override toString()
8. Implement Comparable for value classes

### 20. Common Pitfalls
1. Forgetting to override equals() and hashCode() together
2. Returning arrays or collections directly from getters
3. Not making defensive copies of mutable objects
4. Using inheritance when composition would be better
5. Not documenting thread safety
6. Overusing Object.clone()
7. Not implementing Comparable for value objects

## Practical Exercises

### 21. Design Problems
1. Design a parking lot system
2. Design a deck of cards
3. Design a chess game
4. Design an online shopping system
5. Design a file system

### 22. Code Review Scenarios
1. Spot the memory leak
2. Identify thread safety issues
3. Find SOLID principle violations
4. Suggest design pattern applications
5. Performance optimization opportunities

## Java 17+ Features

### 23. Pattern Matching for instanceof
```java
// Old way
if (obj instanceof String) {
    String s = (String) obj;
    // use s
}

// New way
if (obj instanceof String s) {
    // use s directly
}
```

### 24. Records with Validation
```java
public record User(String username, String email) {
    public User {
        Objects.requireNonNull(username);
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
```

## Interview Tips

### 25. System Design Questions
1. Start with requirements clarification
2. Define core objects and their relationships
3. Identify key components and their interactions
4. Consider scalability and performance
5. Discuss potential bottlenecks
6. Propose solutions and trade-offs

### 26. Coding Questions
1. Clarify input/output requirements
2. Write test cases first
3. Start with a brute force solution
4. Optimize time/space complexity
5. Handle edge cases
6. Write clean, modular code

## Final Notes

### 27. Common Interview Patterns
1. Two Pointers
2. Sliding Window
3. Binary Search
4. Depth-First Search
5. Breadth-First Search
6. Dynamic Programming
7. Backtracking
8. Merge Intervals
9. Topological Sort
10. Union Find

### 28. Must-Know Algorithms
1. Sorting algorithms (QuickSort, MergeSort)
2. Graph algorithms (Dijkstra, A*)
3. Tree traversals (Inorder, Preorder, Postorder)
4. String manipulation (KMP, Rabin-Karp)
5. Dynamic Programming (Knapsack, LCS)
