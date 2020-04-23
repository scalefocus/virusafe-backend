package io.virusafe.service.pin;

import io.virusafe.domain.entity.RegistrationPin;
import io.virusafe.domain.entity.UserDetails;
import io.virusafe.service.pin.generator.PinGenerator;
import io.virusafe.service.sms.SMSService;
import io.virusafe.service.userdetails.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PinServiceImpl implements PinService {

    private static final String DEFAULT_PIN = "000000";
    private static final String USING_IOS_VALIDATION_NUMBER = "Using iOS validation number {0}";
    private static final String MISSING_USER_TEMPLATE = "User with phone number {0} is not registered!";

    private final UserDetailsService userDetailsService;
    private final PinGenerator pinGenerator;
    private final SMSService smsService;
    private final long pinValidTime;
    private final String iosValidationPhoneNumber;
    private final long pinValidAmount;
    private final Clock systemClock;

    /**
     * Construct a new PinService, using the autowired beans.
     *
     * @param userDetailsService
     * @param pinGenerator
     * @param smsService
     * @param pinValidTime
     * @param iosValidationPhoneNumber
     * @param pinValidAmount
     * @param systemClock
     */
    @Autowired
    public PinServiceImpl(final UserDetailsService userDetailsService, final PinGenerator pinGenerator,
                          final SMSService smsService,
                          @Value("${pin.generation.valid.minutes}") final long pinValidTime,
                          @Value("${ios.validation.process.phone.number:'iOSValidationPhone'}") final String iosValidationPhoneNumber,
                          @Value("${pin.generation.valid.limit}") final long pinValidAmount,
                          final Clock systemClock) {
        this.userDetailsService = userDetailsService;
        this.pinGenerator = pinGenerator;
        this.smsService = smsService;
        this.pinValidTime = pinValidTime;
        this.iosValidationPhoneNumber = iosValidationPhoneNumber;
        this.pinValidAmount = pinValidAmount;
        this.systemClock = systemClock;
    }

    @Override
    public void generatePin(final String phoneNumber) {

        Optional<UserDetails> userDetails = userDetailsService.findByPhoneNumber(phoneNumber);

        String pin = pinGenerator.generatePin();
        if (iosValidationPhoneNumber.equals(phoneNumber)) {
            log.info(MessageFormat.format(USING_IOS_VALIDATION_NUMBER, iosValidationPhoneNumber));
            pin = DEFAULT_PIN;
        }

        // Set token validity at given number of minutes.
        LocalDateTime timeNow = LocalDateTime.now(systemClock);
        LocalDateTime validUntil = timeNow.plusMinutes(pinValidTime);

        RegistrationPin registrationPin = RegistrationPin.builder()
                .pin(pin)
                .validUntil(validUntil)
                .build();

        // If not present in the database, build new user details with the phone number.
        UserDetails existingUserDetails = userDetails.orElseGet(() -> buildUserDetails(phoneNumber));
        // Invalidate pins that are going to be active at the same time as the new pin.
        existingUserDetails.getRegistrationPins()
                .stream()
                .filter(p -> Objects.nonNull(p.getValidUntil()))
                .filter(p -> p.getValidUntil().isAfter(timeNow)) // Get all currently valid PINs.
                .sorted(Comparator.comparing(RegistrationPin::getValidUntil).reversed())
                .skip(pinValidAmount - 1) // Ensure we have no more than pinValidAmount active PINs.
                .forEach(p -> p.setValidUntil(timeNow));

        existingUserDetails.addRegistrationPin(registrationPin);
        registrationPin.setUserDetails(existingUserDetails);
        userDetailsService.save(existingUserDetails);

        if (iosValidationPhoneNumber.equals(phoneNumber)) {
            // do not send SMS for iOSValidation
            return;
        }
        smsService.sendPinCreationMessage(phoneNumber, pin);
    }

    @Override
    public boolean verifyPin(final String phoneNumber, final String pin) {

        Optional<UserDetails> userDetails = userDetailsService
                .findByPhoneNumberAndValidPin(phoneNumber, pin, LocalDateTime.now(systemClock));
        return userDetails.isPresent();
    }

    @Override
    public void invalidatePins(final String phoneNumber) {
        UserDetails userDetails = userDetailsService.findByPhoneNumber(phoneNumber)
                .orElseThrow(
                        () -> new NoSuchElementException(MessageFormat.format(MISSING_USER_TEMPLATE, phoneNumber))
                );
        LocalDateTime timeNow = LocalDateTime.now(systemClock);
        userDetails.getRegistrationPins()
                .forEach(pin -> invalidateIfActive(pin, timeNow));
        userDetailsService.save(userDetails);
    }

    private void invalidateIfActive(final RegistrationPin registrationPin, final LocalDateTime timeNow) {
        if (Objects.nonNull(registrationPin.getValidUntil()) &&
                registrationPin.getValidUntil().isAfter(timeNow)) {
            registrationPin.setValidUntil(timeNow);
        }
    }

    private UserDetails buildUserDetails(final String phoneNumber) {
        return UserDetails.builder()
                .phoneNumber(phoneNumber)
                .userGuid(UUID.randomUUID().toString())
                .build();
    }
}
