package io.virusafe.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPushNotificationDTO {

    @NotNull
    private Set<String> userGuids;

    @NotNull
    @Size(max = 15)
    private String title;

    @NotNull
    @Size(max = 200)
    private String message;
}
