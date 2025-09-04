# System Design Interview Questions 🏗️

## **Mid-Level System Design (12+ LPA Focus)**

### **Q1: Design a URL Shortener (like bit.ly)**
**Requirements:**
- Shorten long URLs to 6-8 character codes
- Redirect short URLs to original URLs
- 100M URLs shortened per day
- 100:1 read/write ratio
- Analytics (click tracking)

**Solution:**
```
1. CAPACITY ESTIMATION:
   - Write: 100M/day = 1,200 writes/second
   - Read: 120K reads/second
   - Storage: 100M * 365 * 5 years = 182B URLs
   - Each URL ~500 bytes = 91TB storage

2. SYSTEM ARCHITECTURE:
   ┌─────────────┐    ┌──────────────┐    ┌─────────────┐
   │   Client    │───▶│ Load Balancer│───▶│   API GW    │
   └─────────────┘    └──────────────┘    └─────────────┘
                                                  │
                      ┌─────────────────────────────┼─────────────────────────────┐
                      ▼                             ▼                             ▼
               ┌─────────────┐              ┌─────────────┐              ┌─────────────┐
               │URL Shortener│              │ Analytics   │              │   Cache     │
               │  Service    │              │   Service   │              │  (Redis)    │
               └─────────────┘              └─────────────┘              └─────────────┘
                      │                             │
                      ▼                             ▼
               ┌─────────────┐              ┌─────────────┐
               │  Database   │              │  Analytics  │
               │ (Cassandra) │              │     DB      │
               └─────────────┘              └─────────────┘

3. DATABASE DESIGN:
   URL_MAPPINGS table:
   - short_code (PK): VARCHAR(8)
   - long_url: TEXT
   - user_id: BIGINT
   - created_at: TIMESTAMP
   - expires_at: TIMESTAMP
```

**Implementation:**
```java
@RestController
@RequestMapping("/api/v1")
public class UrlShortenerController {
    
    @Autowired
    private UrlShortenerService urlService;
    
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(@RequestBody ShortenRequest request) {
        String shortUrl = urlService.shortenUrl(request.getLongUrl(), request.getUserId());
        return ResponseEntity.ok(new ShortenResponse(shortUrl));
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, 
                                       HttpServletRequest httpRequest) {
        String longUrl = urlService.getLongUrl(shortCode);
        
        // Async analytics tracking
        analyticsService.trackClick(shortCode, httpRequest);
        
        return ResponseEntity.status(HttpStatus.FOUND)
                           .location(URI.create(longUrl))
                           .build();
    }
}

@Service
public class UrlShortenerService {
    
    @Autowired
    private UrlRepository urlRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private CounterService counterService;
    
    public String shortenUrl(String longUrl, Long userId) {
        // Check if URL already exists for this user
        Optional<UrlMapping> existing = urlRepository.findByLongUrlAndUserId(longUrl, userId);
        if (existing.isPresent()) {
            return buildShortUrl(existing.get().getShortCode());
        }
        
        // Generate unique short code
        String shortCode = generateShortCode();
        
        // Save to database
        UrlMapping mapping = UrlMapping.builder()
            .shortCode(shortCode)
            .longUrl(longUrl)
            .userId(userId)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusYears(1))
            .build();
        
        urlRepository.save(mapping);
        
        // Cache for quick access
        redisTemplate.opsForValue().set(
            "url:" + shortCode, 
            longUrl, 
            Duration.ofHours(24)
        );
        
        return buildShortUrl(shortCode);
    }
    
    public String getLongUrl(String shortCode) {
        // Try cache first
        String longUrl = redisTemplate.opsForValue().get("url:" + shortCode);
        if (longUrl != null) {
            return longUrl;
        }
        
        // Fallback to database
        UrlMapping mapping = urlRepository.findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));
        
        // Check expiration
        if (mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("Short URL has expired");
        }
        
        // Update cache
        redisTemplate.opsForValue().set(
            "url:" + shortCode, 
            mapping.getLongUrl(), 
            Duration.ofHours(24)
        );
        
        return mapping.getLongUrl();
    }
    
    private String generateShortCode() {
        long counter = counterService.getNextCounter();
        return Base62Encoder.encode(counter); // Converts to base62 string
    }
}
```

### **Q2: Design a Chat Application (WhatsApp-like)**
**Requirements:**
- Send/receive messages in real-time
- Group chats
- Message history
- Online/offline status
- 1M daily active users

**Solution:**
```
1. ARCHITECTURE:
   ┌─────────────┐    ┌──────────────┐    ┌─────────────┐
   │Mobile Client│───▶│ Load Balancer│───▶│   API GW    │
   └─────────────┘    └──────────────┘    └─────────────┘
                                                  │
                      ┌─────────────────────────────┼─────────────────────────────┐
                      ▼                             ▼                             ▼
               ┌─────────────┐              ┌─────────────┐              ┌─────────────┐
               │   Message   │              │   User      │              │ WebSocket   │
               │   Service   │              │  Service    │              │  Gateway    │
               └─────────────┘              └─────────────┘              └─────────────┘
                      │                             │                             │
                      ▼                             ▼                             ▼
               ┌─────────────┐              ┌─────────────┐              ┌─────────────┐
               │  Message    │              │    User     │              │ Connection  │
               │     DB      │              │     DB      │              │   Manager   │
               └─────────────┘              └─────────────┘              └─────────────┘

2. DATABASE DESIGN:
   USERS: user_id, username, email, last_seen, status
   CONVERSATIONS: conversation_id, type (direct/group), created_at
   PARTICIPANTS: conversation_id, user_id, joined_at
   MESSAGES: message_id, conversation_id, sender_id, content, timestamp, message_type
```

**Implementation:**
```java
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        Message message = messageService.sendMessage(request);
        return ResponseEntity.ok(MessageResponse.from(message));
    }
    
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Message> messages = messageService.getMessages(conversationId, pageable);
        
        Page<MessageResponse> response = messages.map(MessageResponse::from);
        return ResponseEntity.ok(response);
    }
}

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private ConnectionManager connectionManager;
    
    @Autowired
    private MessageService messageService;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUserId(session);
        connectionManager.addConnection(userId, session);
        
        // Notify friends about online status
        userService.updateOnlineStatus(userId, true);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            MessagePayload payload = objectMapper.readValue(message.getPayload(), MessagePayload.class);
            
            // Save message to database
            Message savedMessage = messageService.saveMessage(payload);
            
            // Send to all participants in the conversation
            List<String> participants = conversationService.getParticipants(payload.getConversationId());
            
            for (String participantId : participants) {
                WebSocketSession participantSession = connectionManager.getConnection(participantId);
                if (participantSession != null && participantSession.isOpen()) {
                    participantSession.sendMessage(new TextMessage(
                        objectMapper.writeValueAsString(MessageResponse.from(savedMessage))
                    ));
                }
            }
            
        } catch (Exception e) {
            log.error("Error handling message", e);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUserId(session);
        connectionManager.removeConnection(userId);
        userService.updateOnlineStatus(userId, false);
    }
}

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private PushNotificationService pushNotificationService;
    
    @Transactional
    public Message sendMessage(SendMessageRequest request) {
        // Validate user is participant in conversation
        if (!conversationService.isParticipant(request.getConversationId(), request.getSenderId())) {
            throw new UnauthorizedException("User not authorized to send message");
        }
        
        Message message = Message.builder()
            .conversationId(request.getConversationId())
            .senderId(request.getSenderId())
            .content(request.getContent())
            .messageType(request.getMessageType())
            .timestamp(LocalDateTime.now())
            .build();
        
        Message savedMessage = messageRepository.save(message);
        
        // Send push notifications to offline users
        List<String> offlineParticipants = conversationService.getOfflineParticipants(
            request.getConversationId(), request.getSenderId()
        );
        
        for (String participantId : offlineParticipants) {
            pushNotificationService.sendMessageNotification(participantId, savedMessage);
        }
        
        return savedMessage;
    }
}
```

### **Q3: Design a Caching System (Redis-like)**
**Requirements:**
- Key-value storage
- TTL support
- LRU eviction
- High availability
- 10K requests/second

**Solution:**
```java
@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {
    
    @Autowired
    private CacheService cacheService;
    
    @PostMapping("/{key}")
    public ResponseEntity<Void> set(@PathVariable String key, 
                                   @RequestBody SetRequest request) {
        cacheService.set(key, request.getValue(), request.getTtlSeconds());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        Optional<String> value = cacheService.get(key);
        return value.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        cacheService.delete(key);
        return ResponseEntity.ok().build();
    }
}

@Service
public class CacheService {
    
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final LinkedHashMap<String, Long> accessOrder = new LinkedHashMap<String, Long>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > MAX_SIZE;
        }
    };
    
    private static final int MAX_SIZE = 10000;
    private final Object lock = new Object();
    
    public void set(String key, String value, Integer ttlSeconds) {
        long expirationTime = ttlSeconds != null ? 
            System.currentTimeMillis() + (ttlSeconds * 1000) : Long.MAX_VALUE;
        
        CacheEntry entry = new CacheEntry(value, expirationTime);
        
        synchronized (lock) {
            cache.put(key, entry);
            accessOrder.put(key, System.currentTimeMillis());
            
            // Remove expired entries
            cleanupExpiredEntries();
        }
    }
    
    public Optional<String> get(String key) {
        synchronized (lock) {
            CacheEntry entry = cache.get(key);
            
            if (entry == null) {
                return Optional.empty();
            }
            
            // Check expiration
            if (entry.isExpired()) {
                cache.remove(key);
                accessOrder.remove(key);
                return Optional.empty();
            }
            
            // Update access order for LRU
            accessOrder.put(key, System.currentTimeMillis());
            return Optional.of(entry.getValue());
        }
    }
    
    public void delete(String key) {
        synchronized (lock) {
            cache.remove(key);
            accessOrder.remove(key);
        }
    }
    
    private void cleanupExpiredEntries() {
        Iterator<Map.Entry<String, CacheEntry>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheEntry> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                accessOrder.remove(entry.getKey());
            }
        }
    }
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void scheduledCleanup() {
        synchronized (lock) {
            cleanupExpiredEntries();
        }
    }
}

@Data
@AllArgsConstructor
public class CacheEntry {
    private String value;
    private long expirationTime;
    
    public boolean isExpired() {
        return expirationTime < System.currentTimeMillis();
    }
}
```

### **Q4: Design a Rate Limiter**
**Requirements:**
- Limit API requests per user
- Different limits for different endpoints
- Distributed system support
- 1M requests/second

**Solution:**
```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    @Autowired
    private RateLimiterService rateLimiterService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = extractUserId(request);
        String endpoint = request.getRequestURI();
        
        if (!rateLimiterService.isAllowed(userId, endpoint)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-RateLimit-Retry-After", "60");
            return false;
        }
        
        return true;
    }
}

@Service
public class RateLimiterService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    // Token bucket algorithm
    public boolean isAllowed(String userId, String endpoint) {
        RateLimitConfig config = getRateLimitConfig(endpoint);
        String key = "rate_limit:" + userId + ":" + endpoint;
        
        return executeTokenBucketAlgorithm(key, config);
    }
    
    private boolean executeTokenBucketAlgorithm(String key, RateLimitConfig config) {
        String script = 
            "local key = KEYS[1] " +
            "local capacity = tonumber(ARGV[1]) " +
            "local tokens = tonumber(ARGV[2]) " +
            "local interval = tonumber(ARGV[3]) " +
            "local now = tonumber(ARGV[4]) " +
            
            "local bucket = redis.call('HMGET', key, 'tokens', 'last_refill') " +
            "local current_tokens = tonumber(bucket[1]) or capacity " +
            "local last_refill = tonumber(bucket[2]) or now " +
            
            "-- Calculate tokens to add " +
            "local time_passed = now - last_refill " +
            "local tokens_to_add = math.floor(time_passed / interval * tokens) " +
            "current_tokens = math.min(capacity, current_tokens + tokens_to_add) " +
            
            "if current_tokens >= 1 then " +
            "  current_tokens = current_tokens - 1 " +
            "  redis.call('HMSET', key, 'tokens', current_tokens, 'last_refill', now) " +
            "  redis.call('EXPIRE', key, interval * 2) " +
            "  return 1 " +
            "else " +
            "  redis.call('HMSET', key, 'tokens', current_tokens, 'last_refill', now) " +
            "  redis.call('EXPIRE', key, interval * 2) " +
            "  return 0 " +
            "end";
        
        Long result = redisTemplate.execute(
            RedisScript.of(script, Long.class),
            Collections.singletonList(key),
            String.valueOf(config.getCapacity()),
            String.valueOf(config.getRefillRate()),
            String.valueOf(config.getRefillInterval()),
            String.valueOf(System.currentTimeMillis())
        );
        
        return result != null && result == 1;
    }
    
    private RateLimitConfig getRateLimitConfig(String endpoint) {
        // Different limits for different endpoints
        switch (endpoint) {
            case "/api/v1/login":
                return new RateLimitConfig(5, 1, 60000); // 5 requests per minute
            case "/api/v1/search":
                return new RateLimitConfig(100, 10, 60000); // 100 requests per minute
            default:
                return new RateLimitConfig(1000, 100, 60000); // 1000 requests per minute
        }
    }
}

@Data
@AllArgsConstructor
public class RateLimitConfig {
    private int capacity;      // Bucket capacity
    private int refillRate;    // Tokens added per interval
    private long refillInterval; // Interval in milliseconds
}
```

## **Follow-up Questions to Expect:**

1. **"How would you handle database failures?"**
   - Master-slave replication
   - Circuit breaker pattern
   - Graceful degradation

2. **"How do you ensure data consistency?"**
   - ACID properties
   - Eventual consistency
   - Distributed transactions (2PC, Saga pattern)

3. **"How would you monitor the system?"**
   - Application metrics (Micrometer)
   - Infrastructure monitoring (Prometheus)
   - Distributed tracing (Zipkin)
   - Log aggregation (ELK stack)

4. **"How do you handle security?"**
   - Authentication (JWT)
   - Authorization (RBAC)
   - Input validation
   - Rate limiting
   - HTTPS/TLS
