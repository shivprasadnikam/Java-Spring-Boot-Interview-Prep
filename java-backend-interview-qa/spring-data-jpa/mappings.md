# Spring Data JPA Mappings — Interview Q&A

## Q1. `@OneToMany` vs `@ManyToOne`: which side should be the owner?

Answer:
- The **owning side** is the one with the foreign key in the DB (usually `@ManyToOne`).
- In JPA, only the owning side updates the relationship.
- Use `mappedBy` on the inverse side to avoid extra join tables.
- Avoid making `@OneToMany` the owner unless you want a join table.
- Follow-up: What happens if both sides try to manage the relationship?

## Q2. Whats the difference between `mappedBy` and a join table?

Answer:
- `mappedBy` links to the owning field and avoids extra join tables.
- Join table creates a separate mapping table, useful for `@ManyToMany`.
- Use join tables for many-to-many or when you cant add FK columns.
- Avoid unnecessary join tables for simple parent-child models.
- Follow-up: How do you add extra columns to a many-to-many relation?

## Q3. Cascade types: when to use and when to avoid?

Answer:
- `CascadeType.ALL` is convenient but dangerous if used blindly.
- Use `PERSIST`/`MERGE` for aggregates where parent controls child lifecycle.
- Avoid cascading `REMOVE` across shared entities.
- Example: Order -> OrderItems (cascade OK), User -> Role (avoid remove).
- Follow-up: How is cascade different from orphan removal?

## Q4. Orphan removal: whats the trap?

Answer:
- `orphanRemoval=true` deletes child rows when removed from the collection.
- Use it for true composition (child cannot exist without parent).
- Avoid it for shared entities or audit-sensitive data.
- Trap: removing from collection triggers delete even if you didnt intend it.
- Follow-up: How do you soft-delete children instead?

## Q5. Fetch type EAGER vs LAZY: whats the safe default?

Answer:
- Default to **LAZY** for collections to avoid N+1 and big joins.
- Use **EAGER** sparingly for small, always-needed references.
- Avoid LAZY outside transactions to prevent `LazyInitializationException`.
- Follow-up: How do you solve N+1 without switching to EAGER?

## Q6. Bidirectional mappings: what common issues do you see?

Answer:
- Infinite recursion in `toString()` or JSON serialization.
- Fix with `@JsonManagedReference` / `@JsonBackReference` or DTOs.
- Use helper methods to keep both sides in sync.
- Avoid exposing entities directly in APIs.
- Follow-up: Why does Hibernate not manage both sides automatically?

## Q7. `@ManyToMany`: when is it a bad idea?

Answer:
- It hides a join table that often needs extra columns (created_at, status).
- Use an explicit join entity instead of `@ManyToMany` for real systems.
- Avoid `@ManyToMany` when you need audit or business attributes.
- Follow-up: How would you model user-role with extra fields?

## Q8. `@Embedded` and `@Embeddable`: when are they useful?

Answer:
- They model value objects inside an entity without separate tables.
- Example: Address fields embedded in `Customer`.
- Use for immutable, reusable components.
- Avoid if the embedded object has its own lifecycle or relationships.
- Follow-up: How do you override column names in embedded objects?

## Q9. `@ElementCollection`: when is it appropriate?

Answer:
- Used for collections of basic/value types stored in a separate table.
- Good for small lists like tags or phone numbers.
- Avoid for large collections or when you need entity-level behavior.
- Follow-up: Whats the performance implication of `@ElementCollection`?

## Q10. Inheritance mapping: SINGLE_TABLE vs JOINED vs TABLE_PER_CLASS?

Answer:
- SINGLE_TABLE: fastest reads, null columns; good for small hierarchies.
- JOINED: normalized schema, slower joins; good for complex domains.
- TABLE_PER_CLASS: duplicates columns, slow unions; rarely worth it.
- Choose based on query patterns and table size.
- Follow-up: How does inheritance affect indexing strategy?

## Q11. Composite keys: `@EmbeddedId` vs `@IdClass`?

Answer:
- `@EmbeddedId` is cleaner and encapsulates the key object.
- `@IdClass` keeps fields in the entity but is more verbose.
- Use composite keys only if the business truly needs them.
- Avoid composite keys for convenience; they complicate relationships.
- Follow-up: How do you map relationships with composite keys?

## Q12. `@MapsId`: when do you need it?

Answer:
- It maps a shared primary key between parent and child.
- Common in one-to-one relationships where child uses parent ID.
- Use for strict 1-1 composition (e.g., User and UserProfile).
- Avoid when independent lifecycle is possible.
- Follow-up: What happens if you save the child before the parent?
