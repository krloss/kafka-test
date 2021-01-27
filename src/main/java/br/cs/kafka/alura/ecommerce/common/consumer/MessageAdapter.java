package br.cs.kafka.alura.ecommerce.common.consumer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import br.cs.kafka.alura.ecommerce.common.CorrelationId;
import br.cs.kafka.alura.ecommerce.common.Message;

public class MessageAdapter<T> implements JsonDeserializer<Message<T>> {
	private Class<T> type;
	
	@SuppressWarnings("unchecked")
	public MessageAdapter(String type) throws ClassNotFoundException {
		this.type = (Class<T>) Class.forName(type);
	}
	
	@Override
	public Message<T> deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) {
		var json = element.getAsJsonObject();
		CorrelationId correlationId = context.deserialize(json.get("correlationId"),CorrelationId.class);
		T payload = context.deserialize(json.get("payload"),type);
		
		return new Message<>(correlationId,payload);
	}
}
