package io.virusafe.exception.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Getter
@ToString
public class FieldValidationErrorDTO {

    @ApiModelProperty(name = "fieldName")
    private String fieldName;

    /**
     * Construct a new FieldValidationErrorDTO from a given ObjectError.
     * If the ObjectError is of type FieldError, use its fieldName.
     * Can be used as a Lombok builder.
     *
     * @param objectError the ObjectError to use
     */
    @Builder
    public FieldValidationErrorDTO(final @NonNull ObjectError objectError) {
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            this.fieldName = fieldError.getField();
        }
    }
}
