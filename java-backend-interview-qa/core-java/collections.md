# Core Java Collections — Interview Q&A

## Q1. ArrayList vs LinkedList: when do you choose each?

Answer:
- ArrayList: contiguous memory, fast random access, slower inserts in middle.
- LinkedList: fast inserts/removals in middle, slow random access, more memory.
- Use ArrayList for read-heavy lists and index lookups.
- Avoid LinkedList unless you truly need frequent mid-list inserts.
- Follow-up: Why does LinkedList perform poorly with CPU caches?

## Q2. HashMap vs ConcurrentHashMap: what changes under the hood?

Answer:
- HashMap is not thread-safe; concurrent writes can corrupt structure.
- ConcurrentHashMap uses segmented locks / CAS for concurrency.
- Use ConcurrentHashMap in shared caches and multi-threaded services.
- Avoid synchronizing a HashMap with global locks (contention bottleneck).
- Follow-up: What is the impact of `computeIfAbsent` in high concurrency?

## Q3. TreeMap vs HashMap: when does ordering matter?

Answer:
- TreeMap is sorted by key (Red-Black tree), O(log n) operations.
- HashMap is O(1) average but unordered.
- Use TreeMap for range queries and ordered iteration.
- Avoid TreeMap for large hot-path lookups (slower than HashMap).
- Follow-up: How do you sort HashMap entries efficiently?

## Q4. What causes HashMap performance issues in production?

Answer:
- Poor hash distribution causing collisions and long chains.
- Large rehash operations when resizing.
- Use well-designed `hashCode` and right initial capacity.
- Avoid storing mutable keys.
- Follow-up: What happens in Java 8 when buckets become too large?

## Q5. Why are fail-fast iterators important?

Answer:
- They detect concurrent modification during iteration.
- Helps fail early instead of producing inconsistent results.
- Use iterator removal instead of modifying list directly in loop.
- Avoid ignoring `ConcurrentModificationException`.
- Follow-up: Are ConcurrentHashMap iterators fail-fast?

## Q6. `List` vs `Set`: what does it imply for data modeling?

Answer:
- List allows duplicates and preserves order.
- Set ensures uniqueness; order depends on implementation.
- Use Set for uniqueness constraints (roles, tags).
- Avoid Set if you need stable index-based access.
- Follow-up: How do you keep insertion order in a Set?

## Q7. `Collections.synchronizedList` vs `CopyOnWriteArrayList`?

Answer:
- SynchronizedList: full lock on every operation.
- CopyOnWriteArrayList: creates a new array on writes, lock-free reads.
- Use CopyOnWrite for read-heavy, write-light data (listeners).
- Avoid CopyOnWrite for frequent writes (high memory churn).
- Follow-up: Why is CopyOnWrite safe without locks?

## Q8. How do you prevent memory leaks with collections?

Answer:
- Clear references when objects are no longer needed.
- Avoid static caches with unbounded growth.
- Use `WeakHashMap` for cache-like usage.
- Avoid using collections as long-lived stores without eviction.
- Follow-up: When is `WeakHashMap` unsafe?

## Q9. Why is `HashSet` faster than `TreeSet`?

Answer:
- HashSet is backed by HashMap with O(1) average operations.
- TreeSet is backed by TreeMap with O(log n) operations.
- Use TreeSet when you need sorted order or range queries.
- Avoid TreeSet for large hot-path membership checks.
- Follow-up: How do you keep order while maintaining uniqueness?

## Q10. What is the difference between `Queue` and `Deque`?

Answer:
- Queue is FIFO; Deque supports FIFO and LIFO.
- Use Deque for stack-like operations without using Stack class.
- Avoid Stack (legacy, synchronized).
- Follow-up: How do you build a bounded queue in Java?

## Q11. Whats the impact of `equals` and `hashCode` on collections?

Answer:
- Hash-based collections rely on both for correctness.
- Incorrect implementations cause missing entries or duplicates.
- Use immutable fields in `equals`/`hashCode`.
- Avoid using database IDs before persistence.
- Follow-up: How does Lombok affect `equals`/`hashCode`?

## Q12. Why can autoboxing hurt performance in collections?

Answer:
- Boxing creates extra objects and GC pressure.
- Use primitive collections where possible (or specialized libs).
- Avoid heavy loops with boxed types in hot paths.
- Follow-up: When is boxing acceptable?
