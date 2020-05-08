package io.virusafe.repository;

import io.virusafe.domain.entity.RegistrationPin;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.security.encryption.SymmetricEncryptionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptionUserDetailsRepositoryFacadeTest {

    private static final String USER_GUID = "USER_GUID";
    private static final String ENCRYPTED_IDENTIFICATION_NUMBER = "ENCRYPTED_IDENTIFICATION_NUMBER";
    private static final String DECRYPTED_IDENTIFICATION_NUMBER = "DECRYPTED_IDENTIFICATION_NUMBER";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String PIN = "PIN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String IV_VECTOR = "IV_VECTOR";
    private static final String PUSH_TOKEN = "PUSH_TOKEN";
    @Mock
    private UserDetailsRepository userDetailsRepository;
    @Mock
    private SymmetricEncryptionProvider encryptionProvider;
    private EncryptionUserDetailsRepositoryFacade repositoryFacade;

    @BeforeEach
    public void setUp() {
        repositoryFacade = new EncryptionUserDetailsRepositoryFacade(encryptionProvider,
                userDetailsRepository, IV_VECTOR);
    }

    @Test
    public void testFindByUserGuid() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(ENCRYPTED_IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        when(encryptionProvider.decrypt(ENCRYPTED_IDENTIFICATION_NUMBER, IV_VECTOR))
                .thenReturn(DECRYPTED_IDENTIFICATION_NUMBER);
        Optional<UserDetails> userResult = repositoryFacade.findByUserGuid(USER_GUID);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(ENCRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumber()),
                () -> assertEquals(DECRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
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
    public void testFindByUserGuidDecryptFails() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(ENCRYPTED_IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        when(encryptionProvider.decrypt(ENCRYPTED_IDENTIFICATION_NUMBER, IV_VECTOR))
                .thenThrow(RuntimeException.class);
        Optional<UserDetails> userResult = repositoryFacade.findByUserGuid(USER_GUID);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(ENCRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumber()),
                () -> assertNull(userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(USER_GUID, userResult.get().getUserGuid())
        );
    }

    @Test
    public void testFindByUserGuidWithNoId() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(null)
                .build();
        when(userDetailsRepository.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        Optional<UserDetails> userResult = repositoryFacade.findByUserGuid(USER_GUID);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertNull(userResult.get().getIdentificationNumber()),
                () -> assertNull(userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(USER_GUID, userResult.get().getUserGuid())
        );
    }

    @Test
    public void testFindByPhoneNumber() {
        UserDetails userDetails = UserDetails.builder()
                .phoneNumber(PHONE_NUMBER)
                .identificationNumber(ENCRYPTED_IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(userDetails));
        when(encryptionProvider.decrypt(ENCRYPTED_IDENTIFICATION_NUMBER, IV_VECTOR))
                .thenReturn(DECRYPTED_IDENTIFICATION_NUMBER);
        Optional<UserDetails> userResult = repositoryFacade.findByPhoneNumber(PHONE_NUMBER);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(ENCRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumber()),
                () -> assertEquals(DECRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
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
                .identificationNumber(ENCRYPTED_IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByPhoneNumberAndValidPin(PHONE_NUMBER, PIN, timeNow))
                .thenReturn(Optional.of(userDetails));
        when(encryptionProvider.decrypt(ENCRYPTED_IDENTIFICATION_NUMBER, IV_VECTOR))
                .thenReturn(DECRYPTED_IDENTIFICATION_NUMBER);
        Optional<UserDetails> userResult = repositoryFacade.findByPhoneNumberAndValidPin(PHONE_NUMBER, PIN, timeNow);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(ENCRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumber()),
                () -> assertEquals(DECRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(PHONE_NUMBER, userResult.get().getPhoneNumber()),
                () -> assertEquals(PIN, userResult.get().getRegistrationPins().get(0).getPin()),
                () -> assertTrue(timeNow.isBefore(userResult.get().getRegistrationPins().get(0).getValidUntil()))
        );
    }

    @Test
    public void testFindByRefreshToken() {
        UserDetails userDetails = UserDetails.builder()
                .refreshToken(REFRESH_TOKEN)
                .identificationNumber(ENCRYPTED_IDENTIFICATION_NUMBER)
                .build();
        when(userDetailsRepository.findByRefreshToken(REFRESH_TOKEN)).thenReturn(Optional.of(userDetails));
        when(encryptionProvider.decrypt(ENCRYPTED_IDENTIFICATION_NUMBER, IV_VECTOR))
                .thenReturn(DECRYPTED_IDENTIFICATION_NUMBER);
        Optional<UserDetails> userResult = repositoryFacade.findByRefreshToken(REFRESH_TOKEN);
        assertAll(
                () -> assertTrue(userResult.isPresent()),
                () -> assertEquals(ENCRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumber()),
                () -> assertEquals(DECRYPTED_IDENTIFICATION_NUMBER, userResult.get().getIdentificationNumberPlain()),
                () -> assertEquals(REFRESH_TOKEN, userResult.get().getRefreshToken())
        );
    }

    @Test
    public void testSave() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumberPlain(DECRYPTED_IDENTIFICATION_NUMBER)
                .build();
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(ENCRYPTED_IDENTIFICATION_NUMBER)
                .identificationNumberPlain(DECRYPTED_IDENTIFICATION_NUMBER)
                .build();
        when(encryptionProvider.encrypt(DECRYPTED_IDENTIFICATION_NUMBER, IV_VECTOR))
                .thenReturn(ENCRYPTED_IDENTIFICATION_NUMBER);
        repositoryFacade.save(userDetails);
        verify(userDetailsRepository, times(1)).save(expectedUserDetails);
    }

    @Test
    public void testSaveWithNoId() {
        UserDetails userDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumberPlain(null)
                .build();
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(null)
                .identificationNumberPlain(null)
                .build();
        repositoryFacade.save(userDetails);
        verify(userDetailsRepository, times(1)).save(expectedUserDetails);
    }

    @Test
    public void testFindAllPushTokensByUserGuid() {
        when(userDetailsRepository.findAllPushTokensByUserGuids(Set.of(USER_GUID), true)).thenReturn(Set.of(PUSH_TOKEN));
        repositoryFacade.findAllPushTokensByUserGuid(Set.of(USER_GUID), true);
        verify(userDetailsRepository, times(1)).findAllPushTokensByUserGuids(Set.of(USER_GUID), true);
    }
}