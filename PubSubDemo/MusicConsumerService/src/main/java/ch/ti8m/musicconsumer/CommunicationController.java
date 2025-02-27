package ch.ti8m.musicconsumer;

import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@Slf4j
public class CommunicationController {

    @Value("${spring.application.name}")
    private String appName;

    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Topic(name = "music", pubsubName = "pubsub")
    @PostMapping(path = "/services")
    private Mono<Void> receiveNote(@RequestBody(required = false) final CloudEvent<String> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                if (cloudEvent.getData().startsWith(appName)) {
                    log.trace("Received: {}", cloudEvent.getData());
                    sink.tryEmitNext(cloudEvent.getData());
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping("/api/stream")
    public Flux<String> sendNoteToFrontend() {
        return sink.asFlux();
    }
}
