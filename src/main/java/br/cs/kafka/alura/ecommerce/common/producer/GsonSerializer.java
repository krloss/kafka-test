package br.cs.kafka.alura.ecommerce.common.producer;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.cs.kafka.alura.ecommerce.common.Message;

public class GsonSerializer<T> implements Serializer<Message<T>> {
	private static final Gson parse = new GsonBuilder().create();
	
	@Override
	public byte[] serialize(String topic, Message<T> data) {
		var result = parse.toJson(data);
		return result.getBytes();
	}
}
