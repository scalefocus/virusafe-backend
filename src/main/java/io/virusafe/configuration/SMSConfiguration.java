package io.virusafe.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sms.service")
@Getter
@Setter
public class SMSConfiguration {

    private String serviceId;

    private String title;

    private String messagePattern;
}
