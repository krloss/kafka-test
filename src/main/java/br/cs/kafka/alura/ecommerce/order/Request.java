package br.cs.kafka.alura.ecommerce.order;

public class Request {
	private final String requestId;
	private final Integer amount;
	
	public Request(String requestId, Integer amount) {
		this.requestId = requestId;
		this.amount = amount;
	}
	
	public String getRequestId() {
		return requestId;
	}
	public Integer getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return String.format("{request:'%s', amount:%d}",requestId,amount);
	}
}
