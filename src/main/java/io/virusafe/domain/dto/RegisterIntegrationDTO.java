package io.virusafe.domain.dto;

import io.virusafe.domain.IdentificationType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RegisterIntegrationDTO {
    private String identificationNumber;
    private IdentificationType identificationType;
    private RegisterLocation location;
    private String phone;
    private Integer age;
    private String gender;
    private String preExistingConditions;
    private Long timestamp;
    private Long serverTime;

    private List<RegisterAnswer> questions = new ArrayList<>();

    @Data
    @Builder
    public static class RegisterAnswer {
        private Integer mobileId;
        private String question;
        private Boolean answer;
    }

    @Data
    @Builder
    public static class RegisterLocation {
        private Double lat;
        private Double lon;
    }
}
