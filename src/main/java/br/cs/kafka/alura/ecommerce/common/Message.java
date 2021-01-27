package br.cs.kafka.alura.ecommerce.common;

public class Message<T> {
	private final CorrelationId correlationId;
	private final T payload;
	
	public Message(String title, T payload) {
		this.correlationId = new CorrelationId(title);
		this.payload = payload;
	}
	public Message(Class<?> type, T payload) {
		this(type.getSimpleName(),payload);
	}
	public Message(CorrelationId correlationId, T payload) {
		this.correlationId = correlationId;
		this.payload = payload;
	}
	
	public CorrelationId getCorrelationId() {
		return correlationId;
	}
	public T getPayload() {
		return payload;
	}
	
	@Override
	public String toString() {
		return String.format("{correlationId:'%s', payload:'%s'}",correlationId,payload);
	}
}
