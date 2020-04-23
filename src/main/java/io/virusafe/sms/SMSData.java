package io.virusafe.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * SMS data representation
 */
@Data
@RequiredArgsConstructor
@ToString
@Builder
public class SMSData {

    @JsonProperty("service_id")
    private final String serviceId;

    @JsonProperty("sc")
    private final String title;

    @JsonProperty("msisdn")
    private final String phoneNumber;

    @JsonProperty("text")
    private final String message;

}