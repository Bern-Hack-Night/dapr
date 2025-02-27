package ch.ti8m.musicemitter.servicediscovery;

import ch.ti8m.musicemitter.servicediscovery.music.MelodyProvider;
import ch.ti8m.musicemitter.servicediscovery.music.Music;
import io.dapr.client.DaprClient;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceDiscoveryService {

    private static final String STATE_KEY_NAME = "knownServices";

    @Value("${dapr.statestore.name}")
    private String stateStoreName;

    private final DaprClient daprClient;
    private final MelodyProvider melodyProvider;

    @Getter
    private final Set<String> registeredMusicServices = new CopyOnWriteArraySet<>();

    @PostConstruct
    public void init() {
        registeredMusicServices.clear();
        daprClient.saveState(stateStoreName, STATE_KEY_NAME, registeredMusicServices).block();
    }

    public void registerMusicService(final String serviceName) {
        registeredMusicServices.add(serviceName);
        daprClient.saveState(stateStoreName, STATE_KEY_NAME, registeredMusicServices).block();
        daprClient.publishEvent("pubsub", serviceName, "Welcome " + serviceName + "!").block();
        log.info("Registered service '{}'", serviceName);
    }

    public Map<Music, Set<String>> getServiceDistribution() {
        final Map<Music, Set<String>> musicToServiceMappings = new HashMap<>();
        melodyProvider.getMusics().forEach(music -> musicToServiceMappings.put(music, new HashSet<>()));
        final List<Set<String>> sets = new ArrayList<>(musicToServiceMappings.values());
        int index = 0;
        for (final String service : getRegisteredMusicServices()) {
            sets.get(index % sets.size()).add(service);
            index++;
        }
        return musicToServiceMappings;
    }

}
