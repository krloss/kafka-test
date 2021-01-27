package br.cs.kafka.alura.ecommerce.common.consumer;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.cs.kafka.alura.ecommerce.common.Message;

public class GsonDeserializer<T> implements Deserializer<Message<T>> {
	public static final String CLASS_CONFIG = String.format("%s.type",GsonDeserializer.class.getName());
	private Gson parse;
	
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		try {
			this.parse =  new GsonBuilder().registerTypeAdapter(Message.class,
				new MessageAdapter<T>(String.valueOf(configs.get(CLASS_CONFIG)))).create();
		}
		catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Message<T> deserialize(String topic, byte[] data) {
		return parse.fromJson(new String(data),Message.class);
	}
}
