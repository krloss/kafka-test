# kafka-alura-ecommerce

### Requirements :
* Java 11+
* Maven 3

### Configurations:
* config/zookeeper.properties

```properties
dataDir=/opt/kafka-2.7.0/data-alura/zookeeper
```
<br>

* config/server.properties

```properties
broker.id=10
default.replication.factor=2

listeners=PLAINTEXT://:9010

log.dirs=/opt/kafka-2.7.0/data-alura/kafka

offsets.topic.replication.factor=2
transaction.state.log.replication.factor=2
```

### Commands :
```bash
cd /opt/kafka-2.7.0/

# Start / Stop
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties 
bin/kafka-server-start.sh -daemon config/server00.properties
...
bin/kafka-server-stop.sh
bin/zookeeper-server-stop.sh

# Tests:
bin/kafka-console-consumer.sh --bootstrap-server "localhost:9092" --topic "KafkaAlura-TesteConsole" --from-beginning
bin/kafka-console-producer.sh --bootstrap-server "localhost:9092" --topic "KafkaAlura-TesteConsole"

# Checks:
bin/kafka-topics.sh --zookeeper localhost:2181 --describe
bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe

# Configs:
bin/kafka-configs.sh --bootstrap-server "localhost:9092" --list
bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --partitions 3 --topic KafkaAluraEcommerce_NewOrder
```
