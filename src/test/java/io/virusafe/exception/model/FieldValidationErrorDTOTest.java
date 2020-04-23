package io.virusafe.exception.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldValidationErrorDTOTest {

    @Test
    public void testBuildValidationErrorDTOFailsWithoutObjectError() {
        assertThrows(NullPointerException.class,
                () -> FieldValidationErrorDTO.builder().build());
    }
}