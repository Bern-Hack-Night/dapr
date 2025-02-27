package ch.ti8m.musicemitter.servicediscovery;

import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ServiceDiscoveryController {

    private final ServiceDiscoveryService serviceDiscoveryService;

    @Topic(name = "services:register", pubsubName = "pubsub")
    @PostMapping(path = "/services/registration")
    private Mono<Void> registerService(@RequestBody(required = false) final CloudEvent<String> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                serviceDiscoveryService.registerMusicService(cloudEvent.getData());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Topic(name = "services:unregister", pubsubName = "pubsub")
    @PostMapping(path = "/services/unregistration")
    private Mono<Void> unRegisterService(@RequestBody(required = false) final CloudEvent<String> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                serviceDiscoveryService.unRegisterMusicService(cloudEvent.getData());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
