package io.virusafe.exception.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorDTOTest {

    @Test
    public void testBuildErrorDTOFailsWithoutMessage() {
        assertThrows(NullPointerException.class,
                () -> ErrorDTO.builder().build());
    }

    @Test
    public void testBuildErrorDTOFailsWithoutException() {
        assertThrows(NullPointerException.class,
                () -> ErrorDTO.fromExceptionBuilder().build());
    }
}