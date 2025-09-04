package com.interview.prep.corejava.oop;

/**
 * Comprehensive OOP concepts demonstration for interview preparation
 */

// 1. ABSTRACTION - Abstract class example
abstract class Vehicle {
    protected String brand;
    protected int year;
    
    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract void start();
    
    // Concrete method - can be inherited or overridden
    public void displayInfo() {
        System.out.println(brand + " " + year);
    }
}

// 2. INHERITANCE - Single inheritance
class Car extends Vehicle {
    private int doors;
    
    public Car(String brand, int year, int doors) {
        super(brand, year); // Call parent constructor
        this.doors = doors;
    }
    
    // 3. POLYMORPHISM - Method overriding
    @Override
    public void start() {
        System.out.println("Car engine started with key");
    }
    
    // Method overloading - same method name, different parameters
    public void accelerate() {
        System.out.println("Car accelerating normally");
    }
    
    public void accelerate(int speed) {
        System.out.println("Car accelerating to " + speed + " km/h");
    }
    
    public void accelerate(int speed, boolean turbo) {
        String mode = turbo ? "turbo" : "normal";
        System.out.println("Car accelerating to " + speed + " km/h in " + mode + " mode");
    }
}

class ElectricCar extends Car {
    private int batteryCapacity;
    
    public ElectricCar(String brand, int year, int doors, int batteryCapacity) {
        super(brand, year, doors);
        this.batteryCapacity = batteryCapacity;
    }
    
    // Polymorphism - different implementation of start()
    @Override
    public void start() {
        System.out.println("Electric car started silently");
    }
    
    public void chargeBattery() {
        System.out.println("Charging battery: " + batteryCapacity + " kWh");
    }
}

// 4. ENCAPSULATION - Data hiding with getters/setters
class BankAccount {
    private String accountNumber; // Private - cannot be accessed directly
    private double balance;
    private String ownerName;
    
    public BankAccount(String accountNumber, String ownerName) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = 0.0;
    }
    
    // Controlled access through methods
    public double getBalance() {
        return balance;
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: $" + amount);
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount);
            return true;
        }
        return false;
    }
    
    // Read-only access to account number
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
}

// Interface for multiple inheritance-like behavior
interface Flyable {
    void fly();
    
    // Java 8+ default method
    default void land() {
        System.out.println("Landing safely");
    }
}

interface Swimmable {
    void swim();
}

// Multiple interface implementation
class Duck implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("Duck flying in the sky");
    }
    
    @Override
    public void swim() {
        System.out.println("Duck swimming in water");
    }
}

// Composition over inheritance example
class Engine {
    private String type;
    private int horsepower;
    
    public Engine(String type, int horsepower) {
        this.type = type;
        this.horsepower = horsepower;
    }
    
    public void start() {
        System.out.println(type + " engine started (" + horsepower + " HP)");
    }
    
    public String getType() {
        return type;
    }
    
    public int getHorsepower() {
        return horsepower;
    }
}

class Motorcycle {
    private Engine engine; // Composition - "has-a" relationship
    private String model;
    
    public Motorcycle(String model, Engine engine) {
        this.model = model;
        this.engine = engine; // Motorcycle HAS an Engine
    }
    
    public void start() {
        System.out.println("Starting " + model);
        engine.start(); // Delegating to composed object
    }
    
    public Engine getEngine() {
        return engine;
    }
}

// Demonstration class
public class OOPConcepts {
    public static void main(String[] args) {
        System.out.println("=== OOP Concepts Demonstration ===\n");
        
        // 1. Inheritance and Polymorphism
        System.out.println("1. Inheritance and Polymorphism:");
        Vehicle car = new Car("Toyota", 2023, 4);
        Vehicle electricCar = new ElectricCar("Tesla", 2023, 4, 100);
        
        // Polymorphism - same method call, different behavior
        car.start();
        electricCar.start();
        
        // Method overloading
        Car myCar = (Car) car;
        myCar.accelerate();
        myCar.accelerate(60);
        myCar.accelerate(100, true);
        
        System.out.println();
        
        // 2. Encapsulation
        System.out.println("2. Encapsulation:");
        BankAccount account = new BankAccount("12345", "John Doe");
        account.deposit(1000);
        account.withdraw(200);
        System.out.println("Balance: $" + account.getBalance());
        
        System.out.println();
        
        // 3. Multiple Interface Implementation
        System.out.println("3. Multiple Interface Implementation:");
        Duck duck = new Duck();
        duck.fly();
        duck.swim();
        duck.land(); // Default method from interface
        
        System.out.println();
        
        // 4. Composition over Inheritance
        System.out.println("4. Composition over Inheritance:");
        Engine v8Engine = new Engine("V8", 450);
        Motorcycle harley = new Motorcycle("Harley Davidson", v8Engine);
        harley.start();
        
        System.out.println("Engine type: " + harley.getEngine().getType());
    }
}

/*
 * MOST ASKED INTERVIEW QUESTIONS (2025 Edition):
 * 
 * Q: What are the four pillars of OOP?
 * A: 1. Encapsulation - Data hiding and controlled access
 *    2. Inheritance - Code reuse and "is-a" relationships
 *    3. Polymorphism - Same interface, different implementations
 *    4. Abstraction - Hiding implementation details
 * 
 * Q: Abstract class vs Interface – when to use which?
 * A: Abstract classes - When you have common code to share, "is-a" relationship, need constructors
 *    Interfaces - When you need multiple inheritance, "can-do" relationship, pure contracts
 *    Key difference: Abstract class can have state and constructors, Interface cannot (until Java 8 default methods)
 * 
 * Q: Difference between method overloading and overriding?
 * A: Overloading - Same method name, different parameters (compile-time polymorphism)
 *    Overriding - Same method signature, different implementation (runtime polymorphism)
 * 
 * Q: Composition vs Inheritance?
 * A: Inheritance - "is-a" relationship, tight coupling, single inheritance in Java
 *    Composition - "has-a" relationship, loose coupling, more flexible, supports multiple relationships
 *    Best Practice: Favor composition over inheritance for better maintainability
 * 
 * Q: final vs finally vs finalize()
 * A: final - keyword for constants, preventing inheritance/overriding
 *    finally - block that always executes (except System.exit())
 *    finalize() - deprecated method called by GC before object destruction
 * 
 * Q: equals() vs ==
 * A: == - compares references (memory addresses) for objects, values for primitives
 *    equals() - compares content/values, can be overridden for custom comparison logic
 *    Always override hashCode() when overriding equals()
 */
