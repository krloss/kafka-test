package br.cs.kafka.alura.ecommerce.common;

public class CorrelationId {
	private final String id;
	
	public CorrelationId(String title) {
		id = String.format("{%s:%s}",title,Helper.getUUID());
	}
	public CorrelationId(CorrelationId parent, String title) {
		id = String.format("%s,{%s:%s}",parent.id,title,Helper.getUUID());
	}
	public CorrelationId(CorrelationId parent, Class<?> type) {
		this(parent,type.getSimpleName());
	}
	
	@Override
	public String toString() { return id; }
}
