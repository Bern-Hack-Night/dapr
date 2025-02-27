package ch.ti8m.musicconsumer;

import io.dapr.client.DaprClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceDiscoveryAgent {

    private final DaprClient daprClient;

    @Value("${spring.application.name}")
    private String applicationName;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        log.info("Initializing ServiceDiscoveryAgent...");
        daprClient.publishEvent(
                "pubsub",
                "services:register",
                applicationName
        ).block();
    }

}
