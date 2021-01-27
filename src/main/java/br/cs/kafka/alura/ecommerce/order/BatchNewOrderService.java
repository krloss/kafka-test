package br.cs.kafka.alura.ecommerce.order;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.cs.kafka.alura.ecommerce.common.CorrelationId;
import br.cs.kafka.alura.ecommerce.common.Helper;
import br.cs.kafka.alura.ecommerce.common.Message;
import br.cs.kafka.alura.ecommerce.common.consumer.ConsumerRunner;
import br.cs.kafka.alura.ecommerce.common.consumer.ConsumerService;
import br.cs.kafka.alura.ecommerce.common.consumer.GsonDeserializer;
import br.cs.kafka.alura.ecommerce.common.producer.ProducerService;

public class BatchNewOrderService extends ConsumerService<Request> {
	private static final String PRODUCER_TOPIC = "KafkaAluraEcommerce_NewOrder";
	private static final Collection<String> CONSUMER_TOPICS = Collections.singleton("KafkaAluraEcommerce_NewOrderBatch");
	private static final ConcurrentHashMap<String,Order> DB = new ConcurrentHashMap<>();
	
	private final ProducerService<Order> producer = new ProducerService<>();
	
	protected BatchNewOrderService() { super(true); }
	
	@Override
	protected Properties getProperties() {
		var properties = super.getProperties();
		
		properties.putAll(Map.of(
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,GsonDeserializer.class.getName(),
			GsonDeserializer.CLASS_CONFIG,Request.class.getName()
		));
		
		return properties;
	}
	
	private static ArrayList<Order> findOrders(Integer limit) throws NoSuchAlgorithmException {
		ArrayList<Order> result = new ArrayList<>();
		
		for(int i = 0; i < limit; i++) {
			var key = String.format("Client_%02d",i);
			
			result.add(new Order(key,Helper.getUUID(),new BigDecimal(Math.random() * 1000 + 100)));
		}
		
		return result;
	}
	
	@Override
	protected void parse(ConsumerRecord<String, Message<Request>> record) throws Exception {
		var value = record.value();
		
		System.out.printf("BatchNewOrder.ConsumerRecord[offset:%d, partition:%d, timestamp:%d, topic:%s,\n\tkey:'%s', value:'%s']\n",
			record.offset(),record.partition(),record.timestamp(),record.topic(),record.key(),value);
		
		for(var it : findOrders(value.getPayload().getAmount())) {
			if(DB.containsKey(it.getOrderId())) {
				System.out.printf("BatchNewOrderService.parse: Order Duplicated(%s)\n",it.getOrderId());
				continue;
			}
			
			producer.sendAsync(PRODUCER_TOPIC,it.getClientId(),new Message<>(
				new CorrelationId(value.getCorrelationId(),getClass().getSimpleName()),it));
			
			DB.put(it.getOrderId(),it);
		}
	}
	
	@Override
	public void close() {
		super.close();
		producer.close();
	}
	
	public static void main(String[] args) throws InterruptedException {
		new ConsumerRunner(BatchNewOrderService::new,CONSUMER_TOPICS).start(3);
	}
}
