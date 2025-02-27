package ch.ti8m.musicemitter.servicediscovery.music;

import ch.ti8m.musicemitter.servicediscovery.ServiceDiscoveryService;
import io.dapr.client.DaprClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessagingService {
    private final DaprClient daprClient;
    private final ServiceDiscoveryService serviceDiscoveryService;

    private static final int tempo = 130;
    private static final float timeMultiplier = (float) (60 * 4) / tempo;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (true) {
                final Map<Music, Set<String>> musicToServiceMappings = serviceDiscoveryService.getServiceDistribution();
                if (musicToServiceMappings.values().stream().allMatch(Set::isEmpty)) {
                    try {
                        log.info("Waiting for services to connect");
                        Thread.sleep(3000);
                        continue;
                    } catch (final InterruptedException ignore) {
                    }
                }
                final List<Thread> threads = new ArrayList<>();
                for (final Music music : musicToServiceMappings.keySet()) {
                    threads.add(new Thread(() -> {
                        for (final String note : music.notes()) {
                            for (final String service : musicToServiceMappings.get(music)) {
                                new Thread(() -> daprClient.publishEvent("pubsub", "music", service + "," + note).block()).start();
                                log.trace("Sending note {} to {}", note, service);
                            }
                            try {
                                Thread.sleep((long) (Float.parseFloat(note.split(",")[1]) * 1000 * timeMultiplier));
                            } catch (final InterruptedException ignore) {
                            }
                        }
                    }));
                }
                threads.forEach(Thread::start);
                threads.forEach(thread -> {
                    try {
                        thread.join();
                    } catch (final InterruptedException ignore) {
                    }
                });
                log.info("Music complete.");
            }
        }).start();
    }
}
