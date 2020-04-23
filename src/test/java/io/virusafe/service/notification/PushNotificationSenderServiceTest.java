package io.virusafe.service.notification;

import io.virusafe.domain.query.QuestionnaireQuery;
import io.virusafe.exception.PushNotificationException;
import io.virusafe.service.pushnotification.PushNotificationService;
import io.virusafe.service.query.QuestionnaireQueryService;
import io.virusafe.service.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PushNotificationSenderServiceTest {

    private static final String MESSAGE = "Please send your questionnaire today.";
    private static final String TITLE = "title";
    private static final String TEST_USER_GUID = "testUserGuid";

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PushNotificationService pushNotificationService;

    @Mock
    private QuestionnaireQueryService questionnaireQueryService;

    private PushNotificationSenderService pushNotificationSenderService;

    @BeforeEach
    public void setUpTests() {
        pushNotificationSenderService = new PushNotificationSenderServiceImpl(userDetailsService,
                pushNotificationService, questionnaireQueryService);
    }

    @Test
    public void sendCustomPushNotificationsTest() {
        QuestionnaireQuery mockQuestionnaireQuery = mock(QuestionnaireQuery.class);

        when(questionnaireQueryService.searchInQuestionnaire(mockQuestionnaireQuery))
                .thenReturn(Set.of(TEST_USER_GUID));
        when(userDetailsService.findPushTokensForUserGuids(any())).thenReturn(Set.of(TEST_USER_GUID));
        pushNotificationSenderService.sendCustomPushNotifications(mockQuestionnaireQuery, TITLE, MESSAGE);
        verify(questionnaireQueryService, times(1)).searchInQuestionnaire(mockQuestionnaireQuery);
        verify(userDetailsService, times(1)).findPushTokensForUserGuids(any());
    }

    @Test
    public void sendCustomPushNotificationsTest_searchInQuestionnaireEmptySet() {
        QuestionnaireQuery mockQuestionnaireQuery = mock(QuestionnaireQuery.class);

        when(questionnaireQueryService.searchInQuestionnaire(mockQuestionnaireQuery))
                .thenReturn(emptySet());
        assertThrows(PushNotificationException.class, () ->
                pushNotificationSenderService.sendCustomPushNotifications(mockQuestionnaireQuery, TITLE, MESSAGE));
    }

    @Test
    public void sendCustomPushNotificationsTest_findPushTokensForUserGuidsEmptySet() {
        QuestionnaireQuery mockQuestionnaireQuery = mock(QuestionnaireQuery.class);

        when(questionnaireQueryService.searchInQuestionnaire(mockQuestionnaireQuery))
                .thenReturn(Set.of(TEST_USER_GUID));
        when(userDetailsService.findPushTokensForUserGuids(any())).thenReturn(emptySet());
        assertThrows(PushNotificationException.class, () -> pushNotificationSenderService
                .sendCustomPushNotifications(mockQuestionnaireQuery, TITLE, MESSAGE));
    }
}
