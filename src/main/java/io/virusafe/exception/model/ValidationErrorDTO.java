package io.virusafe.exception.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
public final class ValidationErrorDTO {

    @ApiModelProperty(example = "Request arguments validation failed!")
    private String message;

    private List<FieldValidationErrorDTO> validationErrors = new ArrayList<>();

    /**
     * All-args constructor for ValidationErrorDTO.
     * Can be used as a Lombok builder.
     *
     * @param message          the error message
     * @param validationErrors the list of validation errors
     */
    @Builder
    public ValidationErrorDTO(final @NonNull String message,
                              final List<FieldValidationErrorDTO> validationErrors) {
        this.message = message;
        if (Objects.nonNull(validationErrors)) {
            this.validationErrors.addAll(validationErrors);
        }
    }
}