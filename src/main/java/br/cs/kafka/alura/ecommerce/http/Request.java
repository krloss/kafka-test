package br.cs.kafka.alura.ecommerce.http;

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
}
