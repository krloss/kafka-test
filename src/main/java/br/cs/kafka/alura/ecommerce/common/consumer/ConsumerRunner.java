package br.cs.kafka.alura.ecommerce.common.consumer;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ConsumerRunner implements Callable<Boolean> {
	private final ConsumerFactory factory;
	private final Collection<String> topics;
	private final Pattern pattern;
	
	public ConsumerRunner(ConsumerFactory factory, Collection<String> topics) {
		this.factory = factory;
		this.topics = topics;
		this.pattern = null;
	}
	public ConsumerRunner(ConsumerFactory factory, Pattern pattern) {
		this.factory = factory;
		this.pattern = pattern;
		this.topics = null;
	}
	
	@Override
	public Boolean call() throws Exception {
		try(var consumer = factory.create()) {
			if(topics != null) consumer.subscribe(topics);
			else if(pattern !=null) consumer.subscribe(pattern);
			else throw new RuntimeException("Topics or Pattern is Required in ConsumerRunner.call");
		}
		
		return true;
	}
	
	public void start(int nThreads) throws InterruptedException {
		var executor = Executors.newFixedThreadPool(nThreads);
		
		try { executor.invokeAll(Collections.nCopies(nThreads,this)); }
		catch(InterruptedException e) {
			System.out.printf(">>> ConsumerRunner.start: %s\n",e.getMessage());
			throw e;
		}
		finally { executor.shutdown(); }
	}
}
