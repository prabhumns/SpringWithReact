package io.madipalli.prabhu.SpringWithReact.config;

import io.madipalli.prabhu.SpringWithReact.service.SubscriptionService;
import org.springframework.stereotype.Component;

@Component
public class KinesisListener {

	private final SubscriptionService service;

	public KinesisListener(final SubscriptionService subscriptionManager) {
		this.service = subscriptionManager;
		new Thread(this::consumeLoop).start();
	}

	private void consumeLoop() {
		while (true) {
			try {
				service.sendRandomEvent();
				Thread.sleep(5000); // simulate delay
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
