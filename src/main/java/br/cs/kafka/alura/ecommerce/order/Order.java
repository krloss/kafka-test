package br.cs.kafka.alura.ecommerce.order;

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
	public String getOrderId() {
		return orderId;
	}
}
