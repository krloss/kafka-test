package br.cs.kafka.alura.ecommerce.common.producer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import br.cs.kafka.alura.ecommerce.common.Message;

public class ProducerService<T> implements Callback,Closeable {
	private final KafkaProducer<String,Message<T>> producer;
	
	public ProducerService() {
		producer = new KafkaProducer<>(getProperties());
	}
	
	public Future<RecordMetadata> sendAsync(String topic, String key, Message<T> value) {
		return producer.send(new ProducerRecord<>(topic,key,value),this);
	}
	public void send(String topic, String key, Message<T> value) throws Exception {
		sendAsync(topic,key,value).get();
	}
	
	@Override
	public void onCompletion(RecordMetadata metadata, Exception exception) {
		if(exception != null) {
			System.out.printf("ProducerService.exception(%s): %s\n",exception,exception.getMessage());
			return;
		}
		
		System.out.printf("ProducerService.Metadata[offset:%d, partition:%d, topic:%s]\n",metadata.offset(),metadata.partition(),metadata.topic());
	}
	
	@Override
	public void close() {
		producer.close();
	}
	
	private Properties getProperties() {
		var properties = new Properties();
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9010,localhost:9020,localhost:9030");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,GsonSerializer.class.getName());
		properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");
		return properties;
	}
}
