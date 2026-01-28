# Java Stream API — Interview Q&A

## Q1. Why were streams introduced in Java 8?

Answer:
- To process collections declaratively with pipeline operations.
- Enables parallelism and lazy evaluation for large datasets.
- Use when readability and data transformations matter.
- Avoid streams for very tight loops in hot paths.
- Follow-up: Whats the difference between internal and external iteration?

## Q2. What is the difference between intermediate and terminal operations?

Answer:
- Intermediate: build the pipeline (map, filter, flatMap) and are lazy.
- Terminal: trigger execution (collect, reduce, forEach).
- Use multiple intermediates to build readable flows.
- Avoid calling terminal ops repeatedly on same stream.
- Follow-up: Why is a stream not reusable after a terminal op?

## Q3. How does lazy evaluation work in streams?

Answer:
- Intermediate ops are not executed until a terminal op runs.
- Allows short-circuiting (findFirst, anyMatch).
- Use for performance in large datasets.
- Avoid side effects that rely on execution order.
- Follow-up: Which operations are short-circuiting?

## Q4. `map` vs `flatMap`: when do you use each?

Answer:
- `map` transforms one element to one element.
- `flatMap` flattens nested streams (one-to-many).
- Use `flatMap` for collections of collections.
- Avoid `flatMap` when a simple `map` works.
- Follow-up: How does `flatMap` affect null handling?

## Q5. Why should you avoid side effects in stream operations?

Answer:
- Side effects break referential transparency and parallel safety.
- Use `map` to transform, not mutate external state.
- Avoid modifying shared collections inside streams.
- Follow-up: What are safe alternatives for accumulation?

## Q6. `collect` vs `reduce`: how do you choose?

Answer:
- `reduce` is for associative reductions; `collect` for mutable accumulation.
- Use `collect` to build lists/maps or custom containers.
- Avoid `reduce` with mutable state.
- Follow-up: Why is `reduce` tricky for parallel streams?

## Q7. How do you avoid `Collectors.toMap` pitfalls?

Answer:
- Provide merge function for duplicate keys.
- Use a map supplier for specific map types.
- Avoid default `toMap` with duplicates (throws exception).
- Follow-up: How do you preserve insertion order in `toMap`?

## Q8. What is the difference between `findFirst` and `findAny`?

Answer:
- `findFirst` respects encounter order; `findAny` may return any element.
- Use `findAny` for parallel streams for better performance.
- Avoid `findFirst` if order doesnt matter.
- Follow-up: How does encounter order affect performance?

## Q9. When should you use parallel streams?

Answer:
- Use for CPU-bound operations on large datasets.
- Avoid for I/O-bound tasks or small collections.
- Be careful with shared mutable state.
- Follow-up: Which thread pool do parallel streams use by default?

## Q10. How do you handle checked exceptions in streams?

Answer:
- Wrap in runtime exceptions or use helper methods.
- Avoid swallowing exceptions inside lambdas.
- Consider moving exception-throwing logic outside stream pipeline.
- Follow-up: What patterns do you use to handle checked exceptions cleanly?

## Q11. What is `Collectors.groupingBy` used for in real systems?

Answer:
- Aggregates records by key (e.g., orders by user, logs by level).
- Use downstream collectors for counts/sums.
- Avoid grouping huge datasets in memory without pagination.
- Follow-up: How do you handle memory pressure in grouping operations?

## Q12. Why can streams be slower than loops in some cases?

Answer:
- Streams add overhead from lambdas and object creation.
- Loops can be optimized by JVM more aggressively.
- Use streams for readability unless profiling shows a hotspot.
- Follow-up: How do you decide between stream and loop in production?

## Q13. What is `peek` and why is it discouraged?

Answer:
- `peek` is for debugging, not side effects.
- Use it to log or inspect during development only.
- Avoid using `peek` for mutation or production logic.
- Follow-up: What alternative patterns avoid `peek` misuse?

## Q14. How do you handle nulls in stream pipelines?

Answer:
- Prefer Optional or filter out nulls early.
- Use `Objects::nonNull` in filters.
- Avoid streams of null elements; it complicates operations.
- Follow-up: How do you use Optional with streams effectively?

## Q15. What are stream best practices in backend services?

Answer:
- Keep pipelines short and readable.
- Use streams for transformations, not side effects.
- Avoid parallel streams in request threads unless proven beneficial.
- Follow-up: How do you profile stream-heavy code?
