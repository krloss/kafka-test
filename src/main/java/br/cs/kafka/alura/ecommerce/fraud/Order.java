package br.cs.kafka.alura.ecommerce.fraud;

import java.math.BigDecimal;

public class Order {
	private final String clientId, orderId;
	private final BigDecimal amount;
	
	public Order(String clientId, String orderId, BigDecimal amount) {
		this.clientId = clientId;
		this.orderId = orderId;
		this.amount = amount;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return String.format("{client:'%s', order:'%s', amount:%.2f}",clientId,orderId,amount);
	}
}
