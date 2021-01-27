package br.cs.kafka.alura.ecommerce.email;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.cs.kafka.alura.ecommerce.common.CorrelationId;
import br.cs.kafka.alura.ecommerce.common.Message;
import br.cs.kafka.alura.ecommerce.common.consumer.ConsumerRunner;
import br.cs.kafka.alura.ecommerce.common.consumer.ConsumerService;
import br.cs.kafka.alura.ecommerce.common.consumer.GsonDeserializer;
import br.cs.kafka.alura.ecommerce.common.producer.ProducerService;

public class EmailService extends ConsumerService<Order> {
	private static final String EMAIL = "Thank you for your order!";
	private static final String PRODUCER_TOPIC = "KafkaAluraEcommerce_SendMail";
	private static final Collection<String> CONSUMER_TOPICS = Collections.singleton("KafkaAluraEcommerce_NewOrder");
	
	private final ProducerService<String> producer = new ProducerService<>();
	
	private EmailService() { super(true); }
	
	@Override
	protected void parse(ConsumerRecord<String,Message<Order>> record) {
		var value = record.value();
		
		System.out.printf("EmailService.ConsumerRecord[offset:%d, partition:%d, timestamp:%d, topic:%s,\n\tkey:'%s', value:'%s']\n",
			record.offset(),record.partition(),record.timestamp(),record.topic(),record.key(),value);
		
		producer.sendAsync(PRODUCER_TOPIC,value.getPayload().getClientId(),
			new Message<>(new CorrelationId(value.getCorrelationId(),getClass()),EMAIL));
	}
	
	@Override
	protected Properties getProperties() {
		var properties = super.getProperties();
		
		properties.putAll(Map.of(
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,GsonDeserializer.class.getName(),
			GsonDeserializer.CLASS_CONFIG,Order.class.getName(),
			ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"10",
			ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest"
		));
		
		return properties;
	}
	
	@Override
	public void close() {
		super.close();
		producer.close();
	}
	
	public static void main(String[] args) throws InterruptedException {
		new ConsumerRunner(EmailService::new,CONSUMER_TOPICS).start(3);
	}
}
