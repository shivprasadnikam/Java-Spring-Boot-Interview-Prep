# Apache Kafka with Spring Boot - Complete Guide

## Table of Contents
- [Core Concepts](#core-concepts)
- [Kafka Setup](#kafka-setup)
- [Spring Boot Integration](#spring-boot-integration)
- [Producers](#producers)
- [Consumers](#consumers)
- [Kafka Streams](#kafka-streams)
- [Error Handling](#error-handling)
- [Transactions](#transactions)
- [Schema Registry](#schema-registry)
- [Performance Tuning](#performance-tuning)
- [Monitoring & Metrics](#monitoring--metrics)
- [Security](#security)
- [Best Practices](#best-practices)
- [Interview Questions](#interview-questions)

## Core Concepts

### Key Components
- **Broker**: Kafka server that stores messages
- **Topic**: Category/feed name to which messages are published
- **Partition**: Ordered, immutable sequence of messages within a topic
- **Offset**: Unique identifier for messages within a partition
- **Producer**: Publishes messages to topics
- **Consumer**: Subscribes to topics and processes messages
- **Consumer Group**: Group of consumers that share the same group ID
- **ZooKeeper**: Manages Kafka brokers and maintains cluster metadata

### Key Features
- High throughput
- Fault tolerance
- Horizontal scalability
- At-least-once delivery semantics
- Message retention based on time/size

## Kafka Setup

### Local Development with Docker
```yaml
# docker-compose.yml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.3.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
```

## Spring Boot Integration

### Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    <!-- For Kafka Streams -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka-streams</artifactId>
    </dependency>
</dependencies>
```

### Configuration
```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: my-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    listener:
      ack-mode: RECORD
```

## Producers

### Basic Producer
```java
@Service
public class KafkaProducerService {
    
    private static final String TOPIC = "users";
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
    
    public void sendUser(User user) {
        kafkaTemplate.send(TOPIC, user.getId().toString(), user);
    }
    
    public void sendWithCallback(String message) {
        ListenableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(TOPIC, message);
            
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                System.out.println("Sent message=[" + message + "]");
            }
            
            @Override
            public void onFailure(Throwable ex) {
                System.err.println("Unable to send message=[" + message + "]: " + ex.getMessage());
            }
        });
    }
}
```

### Producer Configuration
```java
@Configuration
public class KafkaProducerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

## Consumers

### Basic Consumer
```java
@Service
public class KafkaConsumerService {
    
    @KafkaListener(topics = "users", groupId = "my-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }
    
    @KafkaListener(
        topics = "users",
        groupId = "user-group",
        containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUser(User user, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received user: " + user + " from partition: " + partition);
    }
    
    @KafkaListener(
        topics = "users",
        groupId = "batch-group",
        containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void consumeBatch(List<ConsumerRecord<String, User>> records) {
        System.out.println("Received batch of " + records.size() + " messages");
        records.forEach(record -> {
            System.out.println("Processing user: " + record.value());
        });
    }
}
```

### Consumer Configuration
```java
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    
    @Bean
    public ConsumerFactory<String, User> userConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-group");
        
        JsonDeserializer<User> deserializer = new JsonDeserializer<>(User.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            deserializer
        );
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }
}
```

## Kafka Streams

### Stream Processing Example
```java
@Configuration
public class KafkaStreamsConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream("input-topic");
        
        // Process the stream
        stream.filter((key, value) -> value != null && value.length() > 5)
              .mapValues(value -> value.toUpperCase())
              .to("output-topic");
              
        return stream;
    }
    
    @Bean
    public KafkaStreams kafkaStreams(StreamsBuilder builder) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
        
        // Add shutdown hook to close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        
        return streams;
    }
}
```

## Error Handling

### Error Handlers
```java
@Configuration
@EnableKafka
public class KafkaErrorHandlingConfig {
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Error handler for the container
        factory.setErrorHandler(new SeekToCurrentErrorHandler(
            new FixedBackOff(1000L, 2) // max 3 attempts (1 + 2 retries)
        ));
        
        // Error handler for the message listener
        factory.setRecoveryCallback(context -> {
            ConsumerRecord<?, ?> record = (ConsumerRecord<?, ?>) context.getAttribute("record");
            System.err.println("Failed to process: " + record);
            return null;
        });
        
        return factory;
    }
    
    @Bean
    public KafkaListenerErrorHandler kafkaListenerErrorHandler() {
        return (message, exception) -> {
            System.err.println("Error processing message: " + message);
            return "Error processing message: " + exception.getMessage();
        };
    }
}

// Usage
@Service
public class KafkaErrorHandlingService {
    
    @KafkaListener(
        topics = "error-topic",
        errorHandler = "kafkaListenerErrorHandler"
    )
    public void handleMessage(String message) {
        if (message.contains("error")) {
            throw new RuntimeException("Simulated error processing: " + message);
        }
        System.out.println("Processed: " + message);
    }
}
```

## Transactions

### Transactional Producer
```java
@Configuration
public class KafkaTransactionConfig {
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // ... other configs ...
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx-1");
        
        DefaultKafkaProducerFactory<String, String> factory = 
            new DefaultKafkaProducerFactory<>(configProps);
        factory.setTransactionIdPrefix("tx-");
        return factory;
    }
    
    @Bean
    public KafkaTransactionManager<String, String> kafkaTransactionManager() {
        return new KafkaTransactionManager<>(producerFactory());
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

// Usage
@Service
public class TransactionalService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Transactional("kafkaTransactionManager")
    public void processInTransaction(String message) {
        // Write to database
        jdbcTemplate.update("INSERT INTO messages (content) VALUES (?)", message);
        
        // Send to Kafka
        kafkaTemplate.send("output-topic", message);
        
        // If any exception occurs, both DB write and Kafka send will be rolled back
    }
}
```

## Schema Registry

### Avro Schema Example
```java
// User.avsc
{
  "type": "record",
  "name": "User",
  "namespace": "com.example.kafka.model",
  "fields": [
    {"name": "id", "type": "int"},
    {"name": "name", "type": "string"},
    {"name": "email", "type": ["null", "string"], "default": null}
  ]
}

// Configuration
@Configuration
public class AvroConfig {
    
    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;
    
    @Bean
    public SchemaRegistryClient schemaRegistryClient() {
        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(
            schemaRegistryUrl, 1000
        );
        return client;
    }
    
    @Bean
    public KafkaAvroSerializer kafkaAvroSerializer() {
        Map<String, Object> config = new HashMap<>();
        config.put("schema.registry.url", schemaRegistryUrl);
        
        KafkaAvroSerializer serializer = new KafkaAvroSerializer();
        serializer.configure(config, false);
        return serializer;
    }
    
    @Bean
    public ProducerFactory<String, GenericRecord> avroProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // ... other configs ...
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(
            configProps,
            new StringSerializer(),
            kafkaAvroSerializer()
        );
    }
}
```

## Performance Tuning

### Producer Tuning
```yaml
spring:
  kafka:
    producer:
      batch-size: 16384  # 16KB batch size
      buffer-memory: 33554432  # 32MB buffer memory
      compression-type: snappy  # or gzip, lz4, zstd
      retries: 3
      linger-ms: 20  # Wait up to 20ms to batch messages
      max-in-flight-requests-per-connection: 5
      acks: all  # Wait for all in-sync replicas to acknowledge
```

### Consumer Tuning
```yaml
spring:
  kafka:
    consumer:
      max-poll-records: 500  # Max records per poll
      fetch-min-size: 1  # Minimum bytes to fetch
      fetch-max-wait-ms: 500  # Maximum time to wait for fetch
      max-partition-fetch-bytes: 1048576  # 1MB per partition
      heartbeat-interval-ms: 3000
      session-timeout-ms: 10000
      max-poll-interval-ms: 300000  # 5 minutes
```

## Monitoring & Metrics

### Micrometer Integration
```yaml
management:
  metrics:
    enable:
      kafka: true
      kafka-consumer: true
      kafka-producer: true
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
```

### Custom Metrics
```java
@Service
public class KafkaMetricsService {
    
    private final Counter messageCounter;
    private final Timer processingTimer;
    
    public KafkaMetricsService(MeterRegistry registry) {
        this.messageCounter = Counter.builder("kafka.messages.processed")
            .description("Number of messages processed")
            .tag("topic", "users")
            .register(registry);
            
        this.processingTimer = Timer.builder("kafka.processing.time")
            .description("Time taken to process messages")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
    }
    
    @KafkaListener(topics = "users")
    public void processMessage(String message) {
        processingTimer.record(() -> {
            // Process the message
            System.out.println("Processing: " + message);
            messageCounter.increment();
        });
    }
}
```

## Security

### SSL/SASL Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: kafka-broker:9093
    security:
      protocol: SASL_SSL
    ssl:
      trust-store-location: classpath:kafka.truststore.jks
      trust-store-password: changeit
      key-store-location: classpath:kafka.keystore.jks
      key-store-password: changeit
      key-password: changeit
    properties:
      sasl:
        mechanism: SCRAM-SHA-512
        jaas:
          config: org.apache.kafka.common.security.scram.ScramLoginModule required username="admin" password="admin-secret";
```

### ACLs (Access Control Lists)
```bash
# Create ACL for producer
kafka-acls --bootstrap-server localhost:9092 --add \
  --allow-principal User:producer \
  --operation WRITE --topic test-topic

# Create ACL for consumer group
kafka-acls --bootstrap-server localhost:9092 --add \
  --allow-principal User:consumer \
  --operation READ --group my-group
```

## Best Practices

### Producer Best Practices
1. Use appropriate `acks` setting (1 for throughput, `all` for durability)
2. Enable compression when network is the bottleneck
3. Use `linger.ms` and `batch.size` for better batching
4. Implement proper error handling and retries
5. Monitor producer metrics (record-send-rate, record-queue-time, etc.)

### Consumer Best Practices
1. Process messages in batches when possible
2. Commit offsets manually for better control
3. Handle rebalance listeners properly
4. Monitor consumer lag
5. Use appropriate `max.poll.records` to avoid long pauses

### General Best Practices
1. Use schemas (Avro, Protobuf) for message serialization
2. Implement proper error handling and dead-letter queues
3. Monitor key metrics (throughput, latency, error rates)
4. Plan for capacity and scale
5. Secure your Kafka cluster

## Interview Questions

### Basic
1. What is Kafka and what are its main components?
2. Explain the difference between a Kafka topic and a partition.
3. What is a consumer group in Kafka?
4. How does Kafka ensure fault tolerance and high availability?

### Intermediate
1. Explain the different types of delivery semantics in Kafka.
2. How does Kafka handle message ordering?
3. What is the role of ZooKeeper in a Kafka cluster?
4. How would you handle duplicate messages in a consumer?

### Advanced
1. Explain the Kafka replication protocol.
2. How would you design a system to handle exactly-once processing?
3. What are some strategies for handling schema evolution?
4. How would you monitor and troubleshoot a Kafka cluster?

## Resources
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Documentation](https://docs.confluent.io/)
- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)
- [Kafka: The Definitive Guide](https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/)

---
*This guide is designed to help senior Java developers master Apache Kafka with Spring Boot for building scalable, distributed systems and preparing for technical interviews at top product-based companies.*
