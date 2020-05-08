package io.virusafe.repository;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class UserDetailsRepositoryTest {
    private final static String PUSH_TOKEN_1 = "token1";
    private final static String PUSH_TOKEN_2 = "token2";
    private static final String USER_GUID = "userGUID";

    @Test
    void findAllPushTokensByUserGuids_AllTokens() {
        UserDetailsRepository repository = spy(UserDetailsRepository.class);
        when(repository.findAllPushTokens()).thenReturn(Set.of(PUSH_TOKEN_1, PUSH_TOKEN_2));
        Set<String> allTokens = repository.findAllPushTokensByUserGuids(Collections.emptySet(), true);
        assertNotNull(allTokens);
        assertEquals(2, allTokens.size());
    }

    @Test
    void findAllPushTokensByUserGuids_In() {
        UserDetailsRepository repository = spy(UserDetailsRepository.class);
        when(repository.findAllPushTokensByUserGuidIn(anySet())).thenReturn(Set.of(PUSH_TOKEN_1));
        Set<String> allTokens = repository.findAllPushTokensByUserGuids(Set.of(USER_GUID), false);
        assertNotNull(allTokens);
        assertEquals(1, allTokens.size());
    }

    @Test
    void findAllPushTokensByUserGuids_NotIn() {
        UserDetailsRepository repository = spy(UserDetailsRepository.class);
        when(repository.findAllPushTokensByUserGuidNotIn(anySet())).thenReturn(Set.of(PUSH_TOKEN_2));
        Set<String> allTokens = repository.findAllPushTokensByUserGuids(Set.of(USER_GUID), true);
        assertNotNull(allTokens);
        assertEquals(1, allTokens.size());
    }

}
