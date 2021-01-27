package br.cs.kafka.alura.ecommerce.fraud;

import java.math.BigDecimal;
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

public class FraudDetectorService extends ConsumerService<Order> {
	private static final Collection<String> CONSUMER_TOPICS = Collections.singleton("KafkaAluraEcommerce_NewOrder");
	
	private final ProducerService<Order> producer = new ProducerService<>();
	
	public FraudDetectorService() { super(true); }
		
	private static boolean isFraud(Order order) {
		return new BigDecimal("700").compareTo(order.getAmount()) < 0;
	}
	
	@Override
	protected void parse(ConsumerRecord<String, Message<Order>> record) throws Exception {
		var type = getClass().getSimpleName();
		var value = record.value();
		var order = value.getPayload();
		
		System.out.printf("FraudDetectorService.ConsumerRecord[offset:%d, partition:%d, timestamp:%d, topic:%s,\n\tkey:'%s', value:%s]\n",
			record.offset(),record.partition(),record.timestamp(),record.topic(),record.key(),order);

		if(isFraud(order)) {
			System.out.println("FraudDetectorService: REJECT Order!");
			producer.send("KafkaAluraEcommerce_RejectOrder",order.getClientId(),
				new Message<>(new CorrelationId(value.getCorrelationId(),type),order));
		}
		else {
			System.out.println("FraudDetectorService: APPROVE Order!!!");
			producer.send("KafkaAluraEcommerce_ApproveOrder",order.getClientId(),
				new Message<>(new CorrelationId(value.getCorrelationId(),type),order));
		}
	}
	
	@Override
	protected Properties getProperties() {
		var properties = super.getProperties();
		
		properties.putAll(Map.of(
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,GsonDeserializer.class.getName(),
			GsonDeserializer.CLASS_CONFIG,Order.class.getName(),
			ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"1"
		));
		
		return properties;
	}
	
	@Override
	public void close() {
		super.close();
		producer.close();
	}
	
	public static void main(String[] args) throws InterruptedException {
		new ConsumerRunner(FraudDetectorService::new,CONSUMER_TOPICS).start(3);
	}
}
