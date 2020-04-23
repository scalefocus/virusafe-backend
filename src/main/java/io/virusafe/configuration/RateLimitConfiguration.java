package io.virusafe.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "update.rate")
@Getter
@Setter
public class RateLimitConfiguration {
    private long questionnaireSubmitLimit;
    private long questionnaireSubmitBucketSize;
    private long locationUpdateLimit;
    private long locationUpdateBucketSize;
    private long pinRequestLimit;
    private long pinRequestBucketSize;
    private long personalInfoUpdateLimit;
    private long personalInfoUpdateBucketSize;
    private long pushTokenUpdateLimit;
    private long pushTokenUpdateBucketSize;
    private long proximityUpdateLimit;
    private long proximityUpdateBucketSize;
}
