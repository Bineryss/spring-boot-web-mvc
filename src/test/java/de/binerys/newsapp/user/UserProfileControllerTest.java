package de.binerys.newsapp.user;

import de.binerys.newsapp.user.repository.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @MockBean
    NewsUserRepository newsUserRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testProfile() throws Exception {
        NewsUser testuser = new NewsUser("user", "pass",
                "first", "last", LocalDate.now());
        when(newsUserRepository.findByUsername(testuser.getUsername()))
                .thenReturn(testuser);
        mockMvc.perform(get("/user/profile/{username}", testuser.getUsername()))
                .andExpect(model().attributeExists("newsUser"))
                .andExpect(content().string(
                        containsString("Hello " + testuser.getFirstname())));
        verify(newsUserRepository).findByUsername(testuser.getUsername());
    }

    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(get("/user/profile/{username}", "unknown"))
                .andExpect(view().name("UserNotFound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        containsString("We can not find this user.")));
    }
}