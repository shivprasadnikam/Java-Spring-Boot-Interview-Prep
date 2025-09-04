# Coding Challenges - Common Interview Problems 💻

## **Array & String Problems**

### **Q1: Two Sum**
**Problem:** Given an array of integers and a target sum, return indices of two numbers that add up to the target.

```java
public class TwoSum {
    
    // Optimal solution using HashMap - O(n) time, O(n) space
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        
        throw new IllegalArgumentException("No solution found");
    }
    
    // Test
    public static void main(String[] args) {
        TwoSum solution = new TwoSum();
        int[] result = solution.twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(Arrays.toString(result)); // [0, 1]
    }
}
```

### **Q2: Valid Parentheses**
**Problem:** Given a string containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.

```java
public class ValidParentheses {
    
    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> mapping = Map.of(')', '(', '}', '{', ']', '[');
        
        for (char c : s.toCharArray()) {
            if (mapping.containsKey(c)) {
                // Closing bracket
                if (stack.isEmpty() || stack.pop() != mapping.get(c)) {
                    return false;
                }
            } else {
                // Opening bracket
                stack.push(c);
            }
        }
        
        return stack.isEmpty();
    }
    
    // Test
    public static void main(String[] args) {
        ValidParentheses solution = new ValidParentheses();
        System.out.println(solution.isValid("()[]{}"));  // true
        System.out.println(solution.isValid("([)]"));    // false
    }
}
```

### **Q3: Longest Substring Without Repeating Characters**
**Problem:** Find the length of the longest substring without repeating characters.

```java
public class LongestSubstring {
    
    // Sliding window approach - O(n) time, O(min(m,n)) space
    public int lengthOfLongestSubstring(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0, maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            
            // Shrink window until no duplicates
            while (window.contains(rightChar)) {
                window.remove(s.charAt(left));
                left++;
            }
            
            window.add(rightChar);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    // Test
    public static void main(String[] args) {
        LongestSubstring solution = new LongestSubstring();
        System.out.println(solution.lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println(solution.lengthOfLongestSubstring("pwwkew"));   // 3
    }
}
```

## **Linked List Problems**

### **Q4: Reverse Linked List**
**Problem:** Reverse a singly linked list.

```java
public class ReverseLinkedList {
    
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }
    
    // Iterative solution - O(n) time, O(1) space
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        
        while (current != null) {
            ListNode nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }
        
        return prev;
    }
    
    // Recursive solution - O(n) time, O(n) space
    public ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        ListNode reversedHead = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        
        return reversedHead;
    }
    
    // Helper method to print list
    public void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " -> ");
            head = head.next;
        }
        System.out.println("null");
    }
}
```

### **Q5: Detect Cycle in Linked List**
**Problem:** Determine if a linked list has a cycle.

```java
public class LinkedListCycle {
    
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }
    
    // Floyd's Cycle Detection (Tortoise and Hare) - O(n) time, O(1) space
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        ListNode slow = head;
        ListNode fast = head.next;
        
        while (slow != fast) {
            if (fast == null || fast.next == null) {
                return false;
            }
            slow = slow.next;
            fast = fast.next.next;
        }
        
        return true;
    }
    
    // Find the start of the cycle
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }
        
        // Phase 1: Detect if cycle exists
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) break;
        }
        
        if (fast == null || fast.next == null) {
            return null; // No cycle
        }
        
        // Phase 2: Find cycle start
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        
        return slow;
    }
}
```

## **Tree Problems**

### **Q6: Binary Tree Level Order Traversal**
**Problem:** Return the level order traversal of a binary tree's nodes' values.

```java
public class BinaryTreeLevelOrder {
    
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }
    
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            
            result.add(currentLevel);
        }
        
        return result;
    }
    
    // Test
    public static void main(String[] args) {
        BinaryTreeLevelOrder solution = new BinaryTreeLevelOrder();
        
        // Create tree: [3,9,20,null,null,15,7]
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        
        System.out.println(solution.levelOrder(root)); // [[3],[9,20],[15,7]]
    }
}
```

### **Q7: Validate Binary Search Tree**
**Problem:** Determine if a given binary tree is a valid BST.

```java
public class ValidateBST {
    
    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }
    
    public boolean isValidBST(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private boolean validate(TreeNode node, long minVal, long maxVal) {
        if (node == null) return true;
        
        if (node.val <= minVal || node.val >= maxVal) {
            return false;
        }
        
        return validate(node.left, minVal, node.val) && 
               validate(node.right, node.val, maxVal);
    }
    
    // Alternative: In-order traversal approach
    private Integer prev = null;
    
    public boolean isValidBSTInorder(TreeNode root) {
        if (root == null) return true;
        
        if (!isValidBSTInorder(root.left)) return false;
        
        if (prev != null && root.val <= prev) return false;
        prev = root.val;
        
        return isValidBSTInorder(root.right);
    }
}
```

## **Dynamic Programming**

### **Q8: Climbing Stairs**
**Problem:** You're climbing a staircase with n steps. You can climb either 1 or 2 steps at a time. How many distinct ways can you climb to the top?

```java
public class ClimbingStairs {
    
    // Bottom-up DP - O(n) time, O(1) space
    public int climbStairs(int n) {
        if (n <= 2) return n;
        
        int prev2 = 1, prev1 = 2;
        
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }
    
    // Memoization approach - O(n) time, O(n) space
    public int climbStairsMemo(int n) {
        Map<Integer, Integer> memo = new HashMap<>();
        return helper(n, memo);
    }
    
    private int helper(int n, Map<Integer, Integer> memo) {
        if (n <= 2) return n;
        if (memo.containsKey(n)) return memo.get(n);
        
        int result = helper(n - 1, memo) + helper(n - 2, memo);
        memo.put(n, result);
        return result;
    }
}
```

### **Q9: House Robber**
**Problem:** You are a robber planning to rob houses along a street. You cannot rob two adjacent houses. What is the maximum amount of money you can rob?

```java
public class HouseRobber {
    
    // DP approach - O(n) time, O(1) space
    public int rob(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        
        int prev2 = 0, prev1 = 0;
        
        for (int num : nums) {
            int current = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }
    
    // Test
    public static void main(String[] args) {
        HouseRobber solution = new HouseRobber();
        System.out.println(solution.rob(new int[]{2, 7, 9, 3, 1})); // 12
        System.out.println(solution.rob(new int[]{1, 2, 3, 1}));    // 4
    }
}
```

## **System Design Coding**

### **Q10: LRU Cache Implementation**
**Problem:** Design and implement a data structure for Least Recently Used (LRU) cache.

```java
public class LRUCache {
    
    class Node {
        int key, value;
        Node prev, next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head, tail;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // Create dummy head and tail nodes
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        
        // Move to head (most recently used)
        moveToHead(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        Node node = cache.get(key);
        
        if (node != null) {
            // Update existing node
            node.value = value;
            moveToHead(node);
        } else {
            // Add new node
            Node newNode = new Node(key, value);
            
            if (cache.size() >= capacity) {
                // Remove least recently used (tail.prev)
                Node tail = removeTail();
                cache.remove(tail.key);
            }
            
            cache.put(key, newNode);
            addToHead(newNode);
        }
    }
    
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    private Node removeTail() {
        Node lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }
    
    // Test
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // 1
        cache.put(3, 3);                  // evicts key 2
        System.out.println(cache.get(2)); // -1 (not found)
        cache.put(4, 4);                  // evicts key 1
        System.out.println(cache.get(1)); // -1 (not found)
        System.out.println(cache.get(3)); // 3
        System.out.println(cache.get(4)); // 4
    }
}
```

## **Spring Boot Specific Challenges**

### **Q11: Custom Annotation with AOP**
**Problem:** Create a custom annotation for method execution time logging.

```java
// Custom annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    String value() default "";
}

// Aspect implementation
@Aspect
@Component
public class ExecutionTimeAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);
    
    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            String methodName = joinPoint.getSignature().getName();
            String customMessage = logExecutionTime.value();
            
            logger.info("Method: {} {} executed in {} ms", 
                       methodName, 
                       customMessage.isEmpty() ? "" : "(" + customMessage + ")",
                       executionTime);
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("Method: {} failed after {} ms", 
                        joinPoint.getSignature().getName(), 
                        (endTime - startTime));
            throw e;
        }
    }
}

// Usage
@Service
public class UserService {
    
    @LogExecutionTime("User creation process")
    public User createUser(CreateUserRequest request) {
        // Simulate processing time
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        return new User(request.getName(), request.getEmail());
    }
    
    @LogExecutionTime
    public List<User> getAllUsers() {
        // Simulate database query
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        return Arrays.asList(new User("John", "john@example.com"));
    }
}
```

### **Q12: Custom Spring Boot Starter**
**Problem:** Create a custom Spring Boot starter for rate limiting.

```java
// Auto-configuration class
@Configuration
@ConditionalOnProperty(name = "rate-limiter.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterService rateLimiterService(RateLimiterProperties properties) {
        return new RateLimiterService(properties);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterInterceptor rateLimiterInterceptor(RateLimiterService rateLimiterService) {
        return new RateLimiterInterceptor(rateLimiterService);
    }
    
    @Configuration
    @ConditionalOnWebApplication
    public static class WebMvcConfiguration implements WebMvcConfigurer {
        
        @Autowired
        private RateLimiterInterceptor rateLimiterInterceptor;
        
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(rateLimiterInterceptor);
        }
    }
}

// Properties class
@ConfigurationProperties(prefix = "rate-limiter")
@Data
public class RateLimiterProperties {
    private boolean enabled = true;
    private int defaultLimit = 100;
    private Duration window = Duration.ofMinutes(1);
    private Map<String, EndpointConfig> endpoints = new HashMap<>();
    
    @Data
    public static class EndpointConfig {
        private int limit;
        private Duration window;
    }
}

// spring.factories file (src/main/resources/META-INF/spring.factories)
// org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
// com.example.ratelimiter.RateLimiterAutoConfiguration
```

## **Interview Tips**

### **Before Coding:**
1. **Clarify requirements** - Ask about edge cases, constraints
2. **Discuss approach** - Explain your solution strategy
3. **Consider time/space complexity** - Mention Big O notation

### **While Coding:**
1. **Think out loud** - Explain your thought process
2. **Write clean code** - Use meaningful variable names
3. **Handle edge cases** - Check for null, empty inputs

### **After Coding:**
1. **Test your solution** - Walk through with examples
2. **Discuss optimizations** - Can it be improved?
3. **Consider scalability** - How would this work with large inputs?
