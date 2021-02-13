package br.cs.kafka.alura.ecommerce.common.consumer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import br.cs.kafka.alura.ecommerce.common.CorrelationId;
import br.cs.kafka.alura.ecommerce.common.Helper;
import br.cs.kafka.alura.ecommerce.common.Message;
import br.cs.kafka.alura.ecommerce.common.producer.ProducerService;

public abstract class ConsumerService<T> implements Closeable {
	private static final long POLL_MILLIS = 300;
	private static final long WAIT_MILLIS = 3000;
	private static final String DLQ_TOPIC = "KafkaAluraEcommerce_DLQ";
	private final boolean emptyCheck;
	private final KafkaConsumer<String,Message<T>> consumer;
	private final ProducerService<T> dlqProducer = new ProducerService<>();
	private boolean notClosed = true;
	
	protected ConsumerService(boolean emptyCheck) {
		this.emptyCheck = emptyCheck;
		consumer = new KafkaConsumer<>(getProperties());
	}
	
	protected abstract void parse(ConsumerRecord<String,Message<T>> record) throws Exception;
	
	private void exceptionHandle(ConsumerRecord<String, Message<T>> record, Exception exception) {
		System.out.printf("ConsumerService.exceptionHandle: %s\n",exception.getMessage());
		
		try {
			var value = record.value();
			dlqProducer.send(DLQ_TOPIC,record.key(),new Message<>(new CorrelationId(value.getCorrelationId(),"DLQ"),value.getPayload()));
		}
		catch(Exception e) { throw new RuntimeException(e.getMessage(),exception); }
	}
	private void subscribe() {
		var records = consumer.poll(Duration.ofMillis(POLL_MILLIS));
		
		if(emptyCheck && records.isEmpty()) System.out.println(">>> ConsumerService.subscribe: Registros não encontrados.");
		else for(var it : records) {
			try { parse(it); }
			catch(Exception e) { exceptionHandle(it,e); }
		}
		
		try { Thread.sleep(WAIT_MILLIS); }
		catch(InterruptedException e) { Thread.currentThread().interrupt(); }
	}
	
	public void subscribe(Collection<String> topics) {
		consumer.subscribe(topics);
		
		while(notClosed) { subscribe(); }
	}
	public void subscribe(Pattern pattern) {
		consumer.subscribe(pattern);
		
		while(notClosed) { subscribe(); }
	}
	
	@Override
	public void close() {
		notClosed = false;
		consumer.close();
		dlqProducer.close();
	}
	
	protected Properties getProperties() {
		var clazz = getClass().getSimpleName();
		var id = Helper.getUUID();
		var properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9010,localhost:9020,localhost:9030");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,clazz);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG,String.format("%s_%s",id,clazz));
		return properties;
	}
}
