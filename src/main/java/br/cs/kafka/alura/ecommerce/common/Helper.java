package br.cs.kafka.alura.ecommerce.common;

import java.util.UUID;

public class Helper {
	public static String getUUID() {
		return UUID.randomUUID().toString().substring(0,8);
	}
}
