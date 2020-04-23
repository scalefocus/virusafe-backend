package io.virusafe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SystemTimeConfiguration {

    /**
     * Provide a default Clock bean, configured with the system default time zone.
     *
     * @return the system default time zone Clock bean
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }
}
