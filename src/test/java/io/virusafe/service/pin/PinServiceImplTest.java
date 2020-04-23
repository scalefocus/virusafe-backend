package io.virusafe.service.pin;

import io.virusafe.domain.entity.RegistrationPin;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.service.pin.generator.PinGenerator;
import io.virusafe.service.sms.SMSService;
import io.virusafe.service.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PinServiceImplTest {

    private static final long PIN_VALID_TIME = 5L;
    private static final String VALIDATION_PHONE_NUMBER = "VALIDATION_PHONE_NUMBER";
    private static final long PIN_VALID_AMOUNT = 1L;
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String PIN = "PIN";
    private final Clock systemClock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PinGenerator pinGenerator;
    @Mock
    private SMSService smsService;

    private PinServiceImpl pinService;

    @BeforeEach
    public void setUp() {
        pinService = new PinServiceImpl(userDetailsService, pinGenerator, smsService, PIN_VALID_TIME,
                VALIDATION_PHONE_NUMBER, PIN_VALID_AMOUNT, systemClock);
    }

    @Test
    public void testGeneratePin() {
        RegistrationPin validRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(LocalDateTime.now(systemClock).plusMinutes(1L))
                .build();
        RegistrationPin expiredRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(LocalDateTime.now(systemClock).minusMinutes(1L))
                .build();
        RegistrationPin invalidRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .build();
        UserDetails existingUser = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .registrationPins(Arrays.asList(validRegistrationPin, expiredRegistrationPin, invalidRegistrationPin))
                .build();
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(existingUser));
        when(pinGenerator.generatePin()).thenReturn(PIN);
        pinService.generatePin(PHONE_NUMBER);
        verify(smsService, times(1)).sendPinCreationMessage(PHONE_NUMBER, PIN);
    }

    @Test
    public void testGeneratePinForMissingUser() {
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.empty());
        when(pinGenerator.generatePin()).thenReturn(PIN);
        pinService.generatePin(PHONE_NUMBER);
        verify(smsService, times(1)).sendPinCreationMessage(PHONE_NUMBER, PIN);
    }

    @Test
    public void testGeneratePinForStoreValidationUser() {
        when(userDetailsService.findByPhoneNumber(VALIDATION_PHONE_NUMBER)).thenReturn(Optional.empty());
        pinService.generatePin(VALIDATION_PHONE_NUMBER);
        // Verify we won't send an SMS for the validation number
        verify(smsService, times(0)).sendPinCreationMessage(
                eq(VALIDATION_PHONE_NUMBER), any());
    }

    @Test
    public void testVerifyPin() {
        RegistrationPin registrationPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(LocalDateTime.now(systemClock).plusMinutes(PIN_VALID_TIME))
                .build();
        UserDetails userDetails = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .registrationPins(Collections.singletonList(registrationPin))
                .build();
        when(userDetailsService.findByPhoneNumberAndValidPin(PHONE_NUMBER, PIN, LocalDateTime.now(systemClock)))
                .thenReturn(Optional.of(userDetails));
        assertTrue(pinService.verifyPin(PHONE_NUMBER, PIN));
    }

    @Test
    public void testInvalidatePins() {
        RegistrationPin validRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(LocalDateTime.now(systemClock).plusMinutes(1L))
                .build();
        RegistrationPin expiredRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(LocalDateTime.now(systemClock).minusMinutes(1L))
                .build();
        RegistrationPin invalidRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .build();
        UserDetails existingUser = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .registrationPins(Arrays.asList(validRegistrationPin, expiredRegistrationPin, invalidRegistrationPin))
                .build();
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(existingUser));

        pinService.invalidatePins(PHONE_NUMBER);

        RegistrationPin invalidatedRegistrationPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(LocalDateTime.now(systemClock))
                .build();
        UserDetails expectedUser = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .registrationPins(Arrays.asList(invalidatedRegistrationPin,
                        expiredRegistrationPin, invalidRegistrationPin))
                .build();
        verify(userDetailsService, times(1)).save(expectedUser);
    }


    @Test
    public void testInvalidatePinsThrowsExceptionForMissingUser() {
        when(userDetailsService.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> pinService.invalidatePins(PHONE_NUMBER));
    }
}