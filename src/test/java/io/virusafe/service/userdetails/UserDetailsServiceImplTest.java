package io.virusafe.service.userdetails;

import io.virusafe.domain.Gender;
import io.virusafe.domain.command.PersonalInformationUpdateCommand;
import io.virusafe.domain.entity.RegistrationPin;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.repository.UserDetailsRepositoryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    private static final String USER_GUID = "USER_GUID";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String PIN = "PIN";
    private static final long BUCKET_COUNT = 1L;
    private static final int AGE = 34;
    private static final String IDENTIFICATION_NUMBER = "IDENTIFICATION_NUMBER";
    private static final String PRE_EXISTING_CONDITIONS = "PRE_EXISTING_CONDITIONS";
    private static final String PUSH_TOKEN = "PUSH_TOKEN";
    private static final String REFRESH_TOKEN_HASH = "REFRESH_TOKEN_HASH";
    private static final String TOKEN_SECRET = "TOKEN_SECRET";
    private final Clock systemClock = Clock.fixed(
            Instant.parse("2020-09-05T00:00:00.00Z"),
            ZoneId.of("UTC")
    );
    private final LocalDateTime timeNow = LocalDateTime.now(systemClock);
    @Mock
    private UserDetailsRepositoryFacade userDetailsRepositoryFacade;

    private UserDetailsServiceImpl userDetailsService;

    private final UserDetails userDetails = UserDetails.builder().userGuid(USER_GUID).build();
    private final LocalDateTime submitTime = timeNow;

    @BeforeEach
    public void setUp() {
        this.userDetailsService = new UserDetailsServiceImpl(userDetailsRepositoryFacade);
    }

    @Test
    public void testFindByUserGuidProxiesToFacade() {
        UserDetails expectedUser = UserDetails.builder()
                .userGuid(USER_GUID)
                .build();
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.of(expectedUser));
        Optional<UserDetails> foundUser = userDetailsService.findByUserGuid(USER_GUID);
        assertAll(
                () -> assertEquals(expectedUser.getUserGuid(), foundUser.get().getUserGuid())
        );
    }

    @Test
    public void testFindByPhoneNumberProxiesToFacade() {
        UserDetails expectedUser = UserDetails.builder()
                .userGuid(USER_GUID)
                .phoneNumber(PHONE_NUMBER)
                .build();
        when(userDetailsRepositoryFacade.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(expectedUser));
        Optional<UserDetails> foundUser = userDetailsService.findByPhoneNumber(PHONE_NUMBER);
        assertAll(
                () -> assertEquals(expectedUser.getUserGuid(), foundUser.get().getUserGuid()),
                () -> assertEquals(expectedUser.getPhoneNumber(), foundUser.get().getPhoneNumber())
        );
    }

    @Test
    public void testFindByPhoneNumberAndPinProxiesToFacade() {
        RegistrationPin expectedPin = RegistrationPin.builder()
                .pin(PIN)
                .validUntil(timeNow.plusMinutes(5))
                .build();
        UserDetails expectedUser = UserDetails.builder()
                .userGuid(USER_GUID)
                .phoneNumber(PHONE_NUMBER)
                .registrationPins(Collections.singletonList(expectedPin))
                .build();
        when(userDetailsRepositoryFacade.findByPhoneNumberAndValidPin(PHONE_NUMBER,
                PIN, timeNow)).thenReturn(Optional.of(expectedUser));
        Optional<UserDetails> foundUser = userDetailsService.findByPhoneNumberAndValidPin(PHONE_NUMBER,
                PIN, timeNow);
        assertAll(
                () -> assertEquals(expectedUser.getUserGuid(), foundUser.get().getUserGuid()),
                () -> assertEquals(expectedUser.getPhoneNumber(), foundUser.get().getPhoneNumber()),
                () -> assertEquals(expectedPin.getPin(), foundUser.get().getRegistrationPins().get(0).getPin()),
                () -> assertTrue(timeNow.isBefore(foundUser.get().getRegistrationPins().get(0).getValidUntil()))
        );
    }

    @Test
    public void testSaveProxiesToFacade() {
        userDetailsService.save(userDetails);
        verify(userDetailsRepositoryFacade, times(1))
                .save(userDetails);
    }

    @Test
    public void testUpdatePersonalInformation() {
        PersonalInformationUpdateCommand personalInformationUpdateCommand = new PersonalInformationUpdateCommand();
        personalInformationUpdateCommand.setAge(AGE);
        personalInformationUpdateCommand.setGender(Gender.FEMALE);
        personalInformationUpdateCommand.setIdentificationNumber(IDENTIFICATION_NUMBER);
        personalInformationUpdateCommand.setPreExistingConditions(PRE_EXISTING_CONDITIONS);
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .age(AGE)
                .gender(Gender.FEMALE)
                .identificationNumberPlain(IDENTIFICATION_NUMBER)
                .preExistingConditions(PRE_EXISTING_CONDITIONS)
                .build();
        userDetailsService.updatePersonalInformation(USER_GUID, personalInformationUpdateCommand);
        verify(userDetailsRepositoryFacade, times(1))
                .save(expectedUserDetails);
    }

    @Test
    public void testUpdatePersonalInformationFailsForMissingUser() {
        PersonalInformationUpdateCommand personalInformationUpdateCommand = new PersonalInformationUpdateCommand();
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.updatePersonalInformation(USER_GUID, personalInformationUpdateCommand));
    }

    @Test
    public void testUpdatePushToken() {
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .pushToken(PUSH_TOKEN)
                .build();
        userDetailsService.updatePushToken(USER_GUID, PUSH_TOKEN);
        verify(userDetailsRepositoryFacade, times(1))
                .save(expectedUserDetails);
    }

    @Test
    public void testUpdatePushTokenFailsForMissingUser() {
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.updatePushToken(USER_GUID, PUSH_TOKEN));
    }

    @Test
    public void testUpdateTokenDetails() {
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .tokenSecret(TOKEN_SECRET)
                .refreshToken(REFRESH_TOKEN_HASH)
                .build();
        userDetailsService.updateTokenDetails(USER_GUID, TOKEN_SECRET, REFRESH_TOKEN_HASH);
        verify(userDetailsRepositoryFacade, times(1))
                .save(expectedUserDetails);
    }

    @Test
    public void testUpdateTokenDetailsFailsForMissingUser() {
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.updateTokenDetails(USER_GUID, TOKEN_SECRET, REFRESH_TOKEN_HASH));
    }

    @Test
    public void testFindByRefreshTokenProxiesToFacade() {
        UserDetails expectedUser = UserDetails.builder()
                .userGuid(USER_GUID)
                .refreshToken(REFRESH_TOKEN_HASH)
                .build();
        when(userDetailsRepositoryFacade.findByRefreshToken(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(expectedUser));
        Optional<UserDetails> foundUser = userDetailsService.findByRefreshToken(REFRESH_TOKEN_HASH);
        assertAll(
                () -> assertEquals(expectedUser.getUserGuid(), foundUser.get().getUserGuid()),
                () -> assertEquals(expectedUser.getRefreshToken(), foundUser.get().getRefreshToken())
        );
    }

    @Test
    public void testDeleteByUserGuid() {
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.of(userDetails));
        UserDetails expectedUserDetails = UserDetails.builder()
                .userGuid(USER_GUID)
                .identificationNumber(null)
                .age(null)
                .gender(null)
                .preExistingConditions(null)
                .build();
        userDetailsService.deleteByUserGuid(USER_GUID);
        verify(userDetailsRepositoryFacade, times(1))
                .save(expectedUserDetails);
    }

    @Test
    public void testDeleteByUserGuidFailsForMissingUser() {
        when(userDetailsRepositoryFacade.findByUserGuid(USER_GUID)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.deleteByUserGuid(USER_GUID));
    }
}