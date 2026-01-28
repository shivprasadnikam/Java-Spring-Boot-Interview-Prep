# Core Java Multithreading — Interview Q&A

## Q1. Thread vs Runnable vs Executor: what do you use in production?

Answer:
- Use `ExecutorService` for managed thread pools and lifecycle control.
- `Thread` is low-level; `Runnable` is just a task.
- Avoid manually creating threads in services (leaks, no throttling).
- Follow-up: Whats the difference between `submit` and `execute`?

## Q2. What is the Java Memory Model (JMM) in practical terms?

Answer:
- Defines visibility and ordering guarantees across threads.
- Without synchronization/volatile, changes may not be visible.
- Use `volatile` for visibility, locks for atomicity + visibility.
- Avoid assuming writes are immediately visible across threads.
- Follow-up: Why can two threads see stale data?

## Q3. `synchronized` vs `ReentrantLock`: when do you pick which?

Answer:
- `synchronized` is simpler and less error-prone.
- `ReentrantLock` adds try-lock, fairness, interruptible waits.
- Use `ReentrantLock` for complex locking or timeouts.
- Avoid manual locks unless you need extra features.
- Follow-up: What are deadlocks and how do you avoid them?

## Q4. What is thread safety vs atomicity?

Answer:
- Thread safety: correct behavior under concurrent access.
- Atomicity: operation completes fully without interleaving.
- Use atomic classes for counters; locks for compound updates.
- Avoid assuming `volatile` makes compound operations atomic.
- Follow-up: Why is `count++` not atomic?

## Q5. What is a race condition? Give a backend example.

Answer:
- When the outcome depends on timing between threads.
- Example: two payment threads updating same balance.
- Use locks or DB transactions to prevent inconsistent updates.
- Avoid shared mutable state without coordination.
- Follow-up: How do you reproduce race conditions in tests?

## Q6. How do you design thread pools in Spring Boot?

Answer:
- Use `ThreadPoolTaskExecutor` with bounded queue and sensible sizes.
- Size based on CPU vs I/O workload.
- Avoid unbounded queues (memory blowups) and huge pools (context switching).
- Follow-up: How do you tune pool sizes for I/O heavy tasks?

## Q7. What is `CompletableFuture` used for and what are its traps?

Answer:
- Async pipelines without blocking threads.
- Use for parallel I/O calls and combine results.
- Avoid blocking `get()` inside request thread; it defeats async.
- Follow-up: Which thread pool does `supplyAsync` use by default?

## Q8. What is a `volatile` variable used for?

Answer:
- Ensures visibility of updates across threads.
- Use for flags or single-writer/multi-reader states.
- Avoid using volatile for compound operations.
- Follow-up: How does volatile affect instruction reordering?

## Q9. How do you handle graceful shutdown of executors?

Answer:
- Call `shutdown()`, wait with timeout, then `shutdownNow()`.
- In Spring, use `@PreDestroy` to stop pools.
- Avoid abruptly killing threads; you can lose in-flight work.
- Follow-up: How do you ensure idempotency on retries?

## Q10. What is thread contention and how do you reduce it?

Answer:
- Contention happens when many threads compete for a lock.
- Reduce by using fine-grained locks or lock-free structures.
- Use concurrent collections and avoid synchronized blocks on hot paths.
- Follow-up: Why can too many threads reduce throughput?

## Q11. What is a deadlock and how do you prevent it?

Answer:
- Deadlock: two threads waiting for each others locks.
- Prevent with consistent lock ordering or timeouts.
- Avoid nested locks without ordering discipline.
- Follow-up: How do you detect deadlocks in production?

## Q12. What is the difference between `wait/notify` and `Lock/Condition`?

Answer:
- `wait/notify` is intrinsic and tied to object monitors.
- `Condition` gives multiple wait sets and better control.
- Use `Condition` for complex coordination scenarios.
- Avoid using `wait/notify` in new code unless necessary.
- Follow-up: What is spurious wakeup?
