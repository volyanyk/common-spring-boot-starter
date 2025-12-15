package com.common.starter.messaging;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Configuration
public class MessagingConfig {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessagingConfig.class);

    @Bean
    public Consumer<String> consumer() {
        return message -> {
            log.info("Received message: {}", message);
            // Process message
        };
    }
}

@Component
class EventProducer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EventProducer.class);
    private final StreamBridge streamBridge;

    public EventProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendEvent(String bindingName, Object event) {
        log.info("Sending event to binding: {}", bindingName);
        streamBridge.send(bindingName, event);
    }
}
