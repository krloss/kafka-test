# kafka-alura-ecommerce

### Requirements :
* Java 11+
* Maven 3

### Configurations:
- config/zookeeper.properties

```properties
clientPort=2120

dataDir=/opt/kafka-2.7.0/data-alura/zookeeper

server.10=localhost:2115:2119
server.20=localhost:2125:2129
server.30=localhost:2135:2139
```
<br>

- data-alura/zookeeper/myid

```
20
```
<br>

- config/server.properties

```properties
broker.id=20
default.replication.factor=2

listeners=PLAINTEXT://:9020

log.dirs=/opt/kafka-2.7.0/data-alura/kafka

num.partitions=2

offsets.topic.replication.factor=2
transaction.state.log.replication.factor=2

zookeeper.connect=localhost:2120,localhost:2110,localhost:2130
```

### Commands :
```bash
cd /opt/kafka-2.7.0/

# Start / Stop
bin/zookeeper-server-start.sh -daemon config/zookeeper20.properties 
bin/kafka-server-start.sh -daemon config/server20.properties
...
bin/kafka-server-stop.sh
bin/zookeeper-server-stop.sh

# Tests:
bin/kafka-console-consumer.sh --bootstrap-server "localhost:9020,localhost:9010,localhost:9030" --topic "KafkaAlura-TesteConsole" --from-beginning
bin/kafka-console-producer.sh --bootstrap-server "localhost:9020,localhost:9010,localhost:9030" --topic "KafkaAlura-TesteConsole"

# Checks:
bin/kafka-topics.sh --zookeeper "localhost:2120,localhost:2110,localhost:2130" --describe
bin/kafka-consumer-groups.sh --all-groups --bootstrap-server "localhost:9020,localhost:9010,localhost:9030" --describe

# Configs:
bin/kafka-configs.sh --bootstrap-server "localhost:9020,localhost:9010,localhost:9030" --list
bin/kafka-topics.sh --alter --bootstrap-server "localhost:9020,localhost:9010,localhost:9030" --partitions 3 --topic KafkaAluraEcommerce_NewOrder
```
