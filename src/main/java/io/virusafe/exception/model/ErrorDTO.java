package io.virusafe.exception.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public final class ErrorDTO {

    @ApiModelProperty(example = "Invalid request!")
    private String message;

    /**
     * All-args constructor for ErrorDTO.
     * Can be used as a Lombok builder.
     *
     * @param message the error message
     */
    @Builder(builderMethodName = "builder")
    public ErrorDTO(final @NonNull String message) {
        this.message = message;
    }

    /**
     * Construct a new ErrorDTO from a given exception, using its message.
     * Can be used as a Lombok builder.
     *
     * @param exception the exception to use
     */
    @Builder(builderMethodName = "fromExceptionBuilder", builderClassName = "FromExceptionErrorDTOBuilder")
    public ErrorDTO(final @NonNull Exception exception) {
        this.message = exception.getMessage();
    }
}