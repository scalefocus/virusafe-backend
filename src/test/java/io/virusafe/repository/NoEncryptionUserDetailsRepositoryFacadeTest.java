package io.virusafe.repository;

import io.virusafe.domain.entity.RegistrationPin;
import io.virusafe.domain.entity.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoEncryptionUserDetailsRepositoryFacadeTest {

    private static final String USER_GUID = "USER_GUID";
    private static final String IDENTIFICATION_NUMBER = "IDENTIFICATION_NUMBER";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String PIN = "PIN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    @Mock
    private UserDetailsRepository userDetailsRepository;

    private NoEncryptionUserDetailsRepositoryFacade repositoryFacade;

    @BeforeEach
    public void setUp() {
        repositoryFacade = new NoEncryptionUserDetailsRepositoryFacade(userDetailsRepository);
    }

    @Test
    public void testFindByUserGuid() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        Optional<UserDetails> userResult = repositoryFacade.findByUserGuid(USER_GUID);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(USER_GUID, userResult.get().getUserGuid())
        );
    }

    @Test
    public void testFindWithMissingUser() {
        when(userDetailsRepository.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());
        Optional<UserDetails> userResult = repositoryFacade.findByUserGuid(USER_GUID);
        assertFalse(userResult.isPresent());
    }

    @Test
    public void testFindByPhoneNumber() {
        UserDetails userDetails = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(userDetails));
        Optional<UserDetails> userResult = repositoryFacade.findByPhoneNumber(PHONE_NUMBER);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(PHONE_NUMBER, userResult.get().getPhoneNumber())
        );
    }

    @Test
    public void testFindByPhoneNumberAndValidPin() {
        LocalDateTime timeNow = LocalDateTime.now();
        UserDetails userDetails = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .registrationPins(Collections.singletonList(
                        RegistrationPin.builder()
                                .pin(PIN)
                                .validUntil(timeNow.plusMinutes(2))
                                .build()
                ))
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByPhoneNumberAndValidPin(PHONE_NUMBER, PIN, timeNow))
                .thenReturn(Optional.of(userDetails));
        Optional<UserDetails> userResult = repositoryFacade.findByPhoneNumberAndValidPin(PHONE_NUMBER, PIN, timeNow);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(PHONE_NUMBER, userResult.get().getPhoneNumber()),
                () -> assertEquals(PIN, userResult.get().getRegistrationPins().get(0).getPin()),
                () -> assertTrue(timeNow.isBefore(userResult.get().getRegistrationPins().get(0).getValidUntil()))
        );
    }

    @Test
    public void testFindByRefreshToken() {
        UserDetails userDetails = UserDetails.builder()
                .refreshToken(REFRESH_TOKEN)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByRefreshToken(REFRESH_TOKEN)).thenReturn(Optional.of(userDetails));
        Optional<UserDetails> userResult = repositoryFacade.findByRefreshToken(REFRESH_TOKEN);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(REFRESH_TOKEN, userResult.get().getRefreshToken())
        );
    }

    @Test
    public void testSave() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .identificationNumberPlain(IDENTIFICATION_NUMBER)
                .build();
        repositoryFacade.save(userDetails);
        verify(userDetailsRepository, times(1)).save(expectedUserDetails);
    }
}