package io.virusafe.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class PushNotificationDTO {
    private String title;
    private String body;
    private String imageUrl;
}
