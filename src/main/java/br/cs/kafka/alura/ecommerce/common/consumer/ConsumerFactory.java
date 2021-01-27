package br.cs.kafka.alura.ecommerce.common.consumer;

public interface ConsumerFactory {
	public ConsumerService<?> create();
}
