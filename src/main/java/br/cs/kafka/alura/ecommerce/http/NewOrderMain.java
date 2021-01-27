package br.cs.kafka.alura.ecommerce.http;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import br.cs.kafka.alura.ecommerce.common.Message;
import br.cs.kafka.alura.ecommerce.common.producer.ProducerService;

public class NewOrderMain {
	private static final String PRODUCER_TOPIC = "KafkaAluraEcommerce_NewOrderBatch";
	private static final ConcurrentHashMap<String,Request> DB = new ConcurrentHashMap<>();
	private static final List<Request> VALUES = List.of(
		new Request("QUA1300",3),
		new Request("QUA1500",5),
		new Request("QUA1500",5),
		new Request("QUA1700",7)
	);
	
	private static void send(ProducerService<Request> producer, Request value) throws Exception {
		if(DB.containsKey(value.getRequestId())) {
			System.out.printf("NewOrderMain.send: Request Duplicated(%s)\n",value.getRequestId());
			return;
		}
		
		producer.send(PRODUCER_TOPIC,value.getRequestId(),new Message<>(NewOrderMain.class,value));
		DB.put(value.getRequestId(),value);
		System.out.printf("NewOrderMain.send: Request(%s)\n",value.getRequestId());
	}
	
	public static void main(String[] args) throws Exception {
		try(var producer = new ProducerService<Request>()) {
			for(Request it : VALUES) send(producer,it);
		}
	}
}
