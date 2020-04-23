package io.virusafe.exception.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationErrorDTOTest {

    private static final String MESSAGE = "MESSAGE";

    @Test
    public void testBuildValidationErrorDTOFailsWithoutMessage() {
        assertThrows(NullPointerException.class,
                () -> ValidationErrorDTO.builder().build());
    }

    @Test
    public void testBuildValidationErrorDTOSucceedsWithoutErrors() {
        ValidationErrorDTO validationErrorDTO = ValidationErrorDTO.builder().message(MESSAGE).build();
        assertEquals(MESSAGE, validationErrorDTO.getMessage());
    }
}