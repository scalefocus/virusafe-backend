package io.virusafe.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Profile("test")
public class UserDetailsRepositoryTest {

/*    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Test
    public void findAllPushTokensByUserGuidTest() {
        Set<String> userGuids = new HashSet<>();
        userGuids.add("a72c7397-733c-11ea-bbcc-f60659fdf23c");
        Set<String> allPushTokensByUserGuid = userDetailsRepository.findAllPushTokensByUserGuid(userGuids);
        System.out.println(allPushTokensByUserGuid);
    }*/
}
