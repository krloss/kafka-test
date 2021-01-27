package br.cs.kafka.alura.ecommerce.log;

import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.cs.kafka.alura.ecommerce.common.Message;
import br.cs.kafka.alura.ecommerce.common.consumer.ConsumerRunner;
import br.cs.kafka.alura.ecommerce.common.consumer.ConsumerService;

public class LogService extends ConsumerService<String> {
	private static final Pattern CONSUMER_TOPICS = Pattern.compile("^KafkaAluraEcommerce_.*");
	
	private LogService() { super(true); }
	
	@Override
	protected void parse(ConsumerRecord<String, Message<String>> record) throws Exception {
		System.out.printf("LogService.ConsumerRecord[offset:%d, partition:%d, timestamp:%d, topic:%s,\n\tkey:'%s', value:'%s']\n",
			record.offset(),record.partition(),record.timestamp(),record.topic(),record.key(),record.value());
	}
	
	public static void main(String[] args) throws InterruptedException {
		new ConsumerRunner(LogService::new,CONSUMER_TOPICS).start(3);
	}
}
