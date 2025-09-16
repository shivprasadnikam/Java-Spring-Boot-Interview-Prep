# Apache ActiveMQ with Spring Boot - Complete Guide

## Table of Contents
- [Introduction](#introduction)
- [Core Concepts](#core-concepts)
- [Setup with Docker](#setup-with-docker)
- [Spring Boot Integration](#spring-boot-integration)
- [Message Types](#message-types)
- [Message Persistence](#message-persistence)
- [Transactions](#transactions)
- [Clustering & High Availability](#clustering--high-availability)
- [Security](#security)
- [Monitoring & Management](#monitoring--management)
- [Performance Tuning](#performance-tuning)
- [Common Patterns](#common-patterns)
- [Interview Questions](#interview-questions)
- [Best Practices](#best-practices)

## Introduction
Apache ActiveMQ is an open-source message broker that implements the Java Message Service (JMS) API and supports multiple protocols including AMQP, STOMP, and MQTT.

### Key Features
- Full JMS 1.1 and J2EE 1.4 support
- Multiple protocol support (OpenWire, STOMP, AMQP, MQTT, etc.)
- High availability through master/slave configurations
- Message persistence with various storage options
- Clustering for scalability
- Web console for monitoring and management

## Core Concepts

### 1. Message Broker
A server that implements the JMS specification to handle message routing, delivery, and persistence.

### 2. Destination Types
- **Queue**: Point-to-point messaging (one-to-one)
- **Topic**: Publish/subscribe messaging (one-to-many)

### 3. Message Components
- **Header**: Contains metadata (JMSMessageID, JMSTimestamp, etc.)
- **Properties**: Custom key-value pairs
- **Body**: The actual message content

### 4. Message Delivery Models
- **Persistent**: Messages survive broker restarts (default)
- **Non-persistent**: Higher performance but messages are lost if broker restarts

## Setup with Docker

### Basic ActiveMQ Container
```yaml
# docker-compose.yml
version: '3'
services:
  activemq:
    image: apache/activemq:5.17.3
    ports:
      - "8161:8161"  # Web Console
      - "61616:61616"  # OpenWire
      - "5672:5672"   # AMQP
      - "61613:61613"  # STOMP
      - "1883:1883"   # MQTT
      - "61614:61614"  # WS
    volumes:
      - activemq_data:/opt/activemq/data
    environment:
      - ACTIVEMQ_ADMIN_LOGIN=admin
      - ACTIVEMQ_ADMIN_PASSWORD=admin
      - ACTIVEMQ_MIN_MEMORY=512M
      - ACTIVEMQ_MAX_MEMORY=1024M

volumes:
  activemq_data:
```

## Spring Boot Integration

### Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-activemq</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### Configuration
```yaml
# application.yml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    packages:
      trust-all: true  # Trust all packages for deserialization (not recommended for production)
    pool:
      enabled: true
      max-connections: 10
      idle-timeout: 30000

# Custom configuration
app:
  activemq:
    queue: sample.queue
    topic: sample.topic
```

### Producer Configuration
```java
@Configuration
@EnableJms
public class ActiveMQConfig {
    
    @Value("${app.activemq.queue}")
    private String queueName;
    
    @Value("${app.activemq.topic}")
    private String topicName;
    
    @Bean
    public Queue queue() {
        return new ActiveMQQueue(queueName);
    }
    
    @Bean
    public Topic topic() {
        return new ActiveMQTopic(topicName);
    }
    
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDeliveryPersistent(true);
        template.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return template;
    }
}
```

### Message Producer
```java
@Service
public class MessageProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageProducerService.class);
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private Queue queue;
    
    @Autowired
    private Topic topic;
    
    public void sendToQueue(String message) {
        logger.info("Sending message to queue: {}", message);
        jmsTemplate.convertAndSend(queue, message, this::addMessageProperties);
    }
    
    public void sendToTopic(String message) {
        logger.info("Sending message to topic: {}", message);
        jmsTemplate.convertAndSend(topic, message, this::addMessageProperties);
    }
    
    public void sendObjectAsJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(object);
            jmsTemplate.convertAndSend(queue, json, message -> {
                message.setStringProperty("Content-Type", "application/json");
                message.setStringProperty("ObjectType", object.getClass().getName());
                return message;
            });
        } catch (JsonProcessingException e) {
            logger.error("Error converting object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
    
    private Message addMessageProperties(Message message) throws JMSException {
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        message.setJMSTimestamp(System.currentTimeMillis());
        message.setStringProperty("Source", "SpringBootApp");
        return message;
    }
}
```

### Message Consumer
```java
@Service
public class MessageConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerService.class);
    
    @JmsListener(destination = "${app.activemq.queue}")
    public void receiveFromQueue(TextMessage message) throws JMSException {
        logger.info("Received message from queue: {}", message.getText());
        // Process the message
        message.acknowledge();
    }
    
    @JmsListener(destination = "${app.activemq.topic}", containerFactory = "topicListenerFactory")
    public void receiveFromTopic(TextMessage message) throws JMSException {
        logger.info("Received message from topic: {}", message.getText());
        // Process the message
        message.acknowledge();
    }
    
    @JmsListener(destination = "${app.activemq.queue}", containerFactory = "jsonListenerFactory")
    public void receiveJsonMessage(Message message) throws JMSException, IOException {
        String json = ((TextMessage) message).getText();
        String objectType = message.getStringProperty("ObjectType");
        
        logger.info("Received JSON message of type {}: {}", objectType, json);
        
        // Deserialize JSON to object
        ObjectMapper mapper = new ObjectMapper();
        Object obj = mapper.readValue(json, Class.forName(objectType));
        
        // Process the object
        processObject(obj);
        
        message.acknowledge();
    }
    
    private void processObject(Object obj) {
        // Process the deserialized object
    }
}
```

### Container Configuration
```java
@Configuration
public class JmsConfig {
    
    @Bean
    public DefaultJmsListenerContainerFactory queueListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("5-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setErrorHandler(t -> {
            logger.error("Error in listener: {}", t.getMessage(), t);
        });
        return factory;
    }
    
    @Bean
    public DefaultJmsListenerContainerFactory topicListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setSubscriptionDurable(true);
        factory.setClientId("client-1");
        factory.setConcurrency("1");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
    
    @Bean
    public DefaultJmsListenerContainerFactory jsonListenerFactory(ConnectionFactory connectionFactory, 
            MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
    
    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("ObjectType");
        return converter;
    }
}
```

## Message Types

### 1. Text Message
```java
TextMessage message = session.createTextMessage("Hello, ActiveMQ!");
producer.send(message);
```

### 2. Object Message
```java
ObjectMessage message = session.createObjectMessage(new MySerializableObject());
producer.send(message);
```

### 3. Map Message
```java
MapMessage message = session.createMapMessage();
message.setString("name", "John");
message.setInt("age", 30);
producer.send(message);
```

### 4. Bytes Message
```java
BytesMessage message = session.createBytesMessage();
message.writeBytes(fileContent);
producer.send(message);
```

### 5. Stream Message
```java
StreamMessage message = session.createStreamMessage();
message.writeString("Hello");
message.writeInt(123);
producer.send(message);
```

## Message Persistence

### 1. KahaDB (Default)
```xml
<broker>
  <persistenceAdapter>
    <kahaDB directory="${activemq.data}/kahadb"/>
  </persistenceAdapter>
</broker>
```

### 2. JDBC Persistence
```xml
<bean id="mysql-ds" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
  <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
  <property name="url" value="jdbc:mysql://localhost/activemq?relaxAutoCommit=true"/>
  <property name="username" value="activemq"/>
  <property name="password" value="activemq"/>
</bean>

<broker>
  <persistenceAdapter>
    <jdbcPersistenceAdapter dataSource="#mysql-ds" createTablesOnStartup="false"/>
  </persistenceAdapter>
</broker>
```

### 3. LevelDB (Deprecated in newer versions)
```xml
<broker>
  <persistenceAdapter>
    <levelDB directory="${activemq.data}/leveldb"/>
  </persistenceAdapter>
</broker>
```

## Transactions

### 1. Local Transactions
```java
@Transactional
public void processOrder(Order order) {
    // Process order in database
    orderRepository.save(order);
    
    // Send message to ActiveMQ
    jmsTemplate.convertAndSend("orders.queue", order);
    
    // If an exception occurs here, both DB and JMS operations will be rolled back
    processPayment(order);
}
```

### 2. XA Transactions (Distributed)
```java
@Bean
public JmsTransactionManager jmsTransactionManager(ConnectionFactory connectionFactory) {
    return new JmsTransactionManager(connectionFactory);
}

@Bean
public PlatformTransactionManager transactionManager(
        DataSource dataSource, 
        JmsTransactionManager jmsTransactionManager) {
    
    JtaTransactionManager transactionManager = new JtaTransactionManager();
    transactionManager.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ALWAYS);
    
    return new ChainedTransactionManager(
        new DataSourceTransactionManager(dataSource),
        jmsTransactionManager
    );
}
```

## Clustering & High Availability

### 1. Master-Slave with Shared Storage
```xml
<broker brokerName="brokerA" dataDirectory="${activemq.data}">
  <persistenceAdapter>
    <kahaDB directory="${activemq.data}/kahadb"/>
  </persistenceAdapter>
  <transportConnectors>
    <transportConnector name="openwire" uri="tcp://0.0.0.0:61616" discoveryUri="multicast://default"/>
  </transportConnectors>
  <networkConnectors>
    <networkConnector name="default-nc" uri="multicast://default"/>
  </networkConnectors>
</broker>
```

### 2. Network of Brokers
```xml
<networkConnectors>
  <networkConnector name="broker1-to-broker2" 
                   uri="static:(tcp://broker2:61616)"
                   duplex="true"
                   conduitSubscriptions="true"
                   networkTTL="3"/>
</networkConnectors>
```

## Security

### 1. Authentication
```xml
<plugins>
  <simpleAuthenticationPlugin>
    <users>
      <authenticationUser username="admin" password="admin" groups="admins,users"/>
      <authenticationUser username="user" password="password" groups="users"/>
    </users>
  </simpleAuthenticationPlugin>
</plugins>
```

### 2. Authorization
```xml
<authorizationPlugin>
  <map>
    <authorizationMap>
      <authorizationEntries>
        <authorizationEntry queue=">" read="admins" write="admins" admin="admins" />
        <authorizationEntry queue="USERS.>" read="users" write="users" admin="users" />
        <authorizationEntry topic=">" read="admins" write="admins" admin="admins" />
        <authorizationEntry topic="ACTIVEMQ.ADMIN.>" read="admins" write="admins" admin="admins" />
      </authorizationEntries>
    </authorizationMap>
  </map>
</authorizationPlugin>
```

### 3. SSL/TLS
```xml
<sslContext>
  <sslContext keyStore="file:${activemq.conf}/broker.ks"
              keyStorePassword="password"
              trustStore="file:${activemq.conf}/broker.ts"
              trustStorePassword="password"/>
</sslContext>

<transportConnectors>
  <transportConnector name="ssl" uri="ssl://0.0.0.0:61617?transport.needClientAuth=true"/>
</transportConnectors>
```

## Monitoring & Management

### 1. Web Console
Access the ActiveMQ Web Console at: `http://localhost:8161/admin/`

### 2. JMX Monitoring
```xml
<broker useJmx="true" xmlns="http://activemq.apache.org/schema/core">
  <managementContext>
    <managementContext createConnector="true" connectorPort="1099" jmxDomainName="org.apache.activemq"/>
  </managementContext>
</broker>
```

### 3. Advisory Messages
```java
@JmsListener(destination = "ActiveMQ.Advisory.>")
public void handleAdvisory(Message message) {
    // Process advisory messages
    logger.info("Advisory message: {}", message);
}
```

## Performance Tuning

### 1. Producer Settings
```java
@Bean
public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    JmsTemplate template = new JmsTemplate(connectionFactory);
    template.setDeliveryMode(DeliveryMode.NON_PERSISTENT); // For better performance
    template.setExplicitQosEnabled(true);
    template.setTimeToLive(86400000); // 24 hours
    template.setSessionTransacted(false); // Disable transactions for better throughput
    return template;
}
```

### 2. Consumer Settings
```java
@Bean
public DefaultMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setDestinationName("myQueue");
    container.setMessageListener(message -> {
        // Process message
    });
    container.setConcurrentConsumers(5);
    container.setMaxConcurrentConsumers(10);
    container.setIdleTaskExecutionLimit(60);
    container.setIdleConsumerLimit(5);
    container.setReceiveTimeout(5000);
    return container;
}
```

### 3. Broker Tuning
```xml
<broker schedulerSupport="true" schedulerDirectory="${activemq.data}/scheduler" 
       useJmx="true" persistent="true" useShutdownHook="false">
  
  <systemUsage>
    <systemUsage>
      <memoryUsage>
        <memoryUsage limit="1024 mb"/>
      </memoryUsage>
      <storeUsage>
        <storeUsage limit="50 gb"/>
      </storeUsage>
      <tempUsage>
        <tempUsage limit="10 gb"/>
      </tempUsage>
    </systemUsage>
  </systemUsage>
  
  <policyEntry queue=">" producerFlowControl="false" memoryLimit="1mb" />
  
</broker>
```

## Common Patterns

### 1. Request-Reply Pattern
```java
// Sender
public String sendWithReply(String message) {
    return (String) jmsTemplate.execute(session -> {
        Queue tempQueue = session.createTemporaryQueue();
        MessageProducer producer = session.createProducer(destination);
        TextMessage request = session.createTextMessage(message);
        request.setJMSReplyTo(tempQueue);
        producer.send(request);
        
        MessageConsumer consumer = session.createConsumer(tempQueue);
        Message response = consumer.receive(5000); // 5 seconds timeout
        return response instanceof TextMessage ? ((TextMessage) response).getText() : null;
    }, true);
}

// Receiver
@JmsListener(destination = "request.queue")
public void handleRequest(Message message, Session session, MessageProducer replyProducer) throws JMSException {
    String text = ((TextMessage) message).getText();
    String response = "Processed: " + text;
    
    TextMessage reply = session.createTextMessage(response);
    reply.setJMSCorrelationID(message.getJMSCorrelationID());
    replyProducer.send(message.getJMSReplyTo(), reply);
}
```

### 2. Competing Consumers
```java
@JmsListener(destination = "orders.queue", concurrency = "5-10")
public void processOrder(Order order) {
    // Process order with multiple consumers
}
```

### 3. Publish-Subscribe with Durable Subscriptions
```java
@JmsListener(destination = "orders.topic", containerFactory = "topicListenerFactory")
public void subscribeToOrders(Order order) {
    // Process order from topic
}

@Bean
public DefaultJmsListenerContainerFactory topicListenerFactory(ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setSubscriptionDurable(true);
    factory.setClientId("order-processor-1");
    return factory;
}
```

## Interview Questions

### Basic Level
1. **What is ActiveMQ and what are its key features?**
   - Open-source message broker
   - Implements JMS 1.1 and J2EE 1.4
   - Supports multiple protocols (OpenWire, STOMP, AMQP, MQTT)
   - Provides high availability and scalability

2. **What are the different types of message models in ActiveMQ?**
   - Point-to-Point (Queue)
   - Publish/Subscribe (Topic)

3. **What is the difference between a queue and a topic?**
   - Queue: Point-to-point, each message consumed by one consumer
   - Topic: Publish-subscribe, each message consumed by multiple subscribers

### Intermediate Level
4. **How does ActiveMQ handle message persistence?**
   - KahaDB (default)
   - JDBC persistence
   - LevelDB (deprecated)
   - Memory-based (non-persistent)

5. **What is the role of a dead letter queue in ActiveMQ?**
   - Stores messages that couldn't be delivered
   - Handles poison messages
   - Configured using `deadLetterStrategy`

6. **How can you implement message filtering in ActiveMQ?**
   - Using message selectors with SQL-92 syntax
   - Example: `consumer = session.createConsumer(destination, "color = 'red' AND price > 100")`

### Advanced Level
7. **Explain ActiveMQ's clustering capabilities**
   - Network of brokers
   - Master-slave configurations
   - Message load balancing
   - Store and forward

8. **How does ActiveMQ ensure high availability?**
   - Master-slave with shared storage
   - JDBC master-slave
   - Replicated LevelDB store (deprecated)
   - KahaDB with shared file system

9. **What is the difference between persistent and non-persistent messages?**
   - Persistent: Survive broker restarts, stored on disk
   - Non-persistent: Higher performance, lost on broker restart

10. **How can you monitor ActiveMQ?**
    - Web Console (http://localhost:8161/admin)
    - JMX monitoring
    - Advisory messages
    - Command-line tools (activemq-admin)

## Best Practices

1. **Connection Management**
   - Use connection pooling
   - Close connections properly
   - Set appropriate timeouts

2. **Message Handling**
   - Use appropriate message types
   - Keep message size small
   - Use message properties for filtering

3. **Error Handling**
   - Implement dead letter queues
   - Handle poison messages
   - Use transactions when needed

4. **Performance**
   - Tune memory settings
   - Use appropriate persistence options
   - Scale consumers horizontally

5. **Security**
   - Enable authentication
   - Use SSL/TLS for network communication
   - Implement proper authorization

## Troubleshooting

### Common Issues
1. **Connection Issues**
   - Check broker URL and credentials
   - Verify network connectivity
   - Check firewall settings

2. **Performance Problems**
   - Monitor memory usage
   - Check disk I/O
   - Review consumer performance

3. **Message Loss**
   - Check persistence settings
   - Review acknowledgment modes
   - Verify error handling

### Useful Commands
```bash
# Start ActiveMQ
./bin/activemq start

# Stop ActiveMQ
./bin/activemq stop

# Check broker status
./bin/activemq status

# List queues
./bin/activemq query -QQueue=\"*\"

# Browse queue messages
./bin/activemq browse --amqurl tcp://localhost:61616 queue.name

# Purge a queue
./bin/activemq purge queue.name
```

## Resources

### Documentation
- [Apache ActiveMQ Documentation](https://activemq.apache.org/components/classic/documentation/)
- [Spring JMS Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#jms)

### Books
- "ActiveMQ in Action" by Bruce Snyder, Dejan Bosanac, and Rob Davies
- "Enterprise Integration Patterns" by Gregor Hohpe and Bobby Woolf

### Online Courses
- Apache ActiveMQ with Spring Boot (Udemy)
- JMS with ActiveMQ (Pluralsight)

---
*This guide provides a comprehensive overview of Apache ActiveMQ with Spring Boot integration, covering everything from basic setup to advanced configurations and best practices.*
