package de.binerys.newsapp.user.register;

import de.binerys.newsapp.user.NewsUser;
import de.binerys.newsapp.user.repository.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @MockBean
    NewsUserRepository newsUserRepository;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc
                .perform(get("/user/register"))
                .andExpect(model().attributeExists("newsUser"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("Register")));
    }

    @Test
    void testProcessRegistrationForm() throws Exception {
        var checkUser = new NewsUser("user", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        mockMvc
                .perform(post("/user/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstname", checkUser.getFirstname())
                        .param("lastname", checkUser.getLastname())
                        .param("birthday",
                                checkUser.getBirthday().format(DateTimeFormatter.ISO_DATE))
                        .param("username", checkUser.getUsername())
                        .param("password", checkUser.getPassword()))
                .andExpect(redirectedUrl("/user/profile/"+checkUser.getUsername()));
        //geht nicht, da das passwort gehashed ist
//        verify(newsUserRepository).save(checkUser);

        var captor = ArgumentCaptor.forClass(NewsUser.class);
        verify(newsUserRepository).save(captor.capture());

        var savedUser = captor.getValue();

        assertThat(checkUser).usingRecursiveComparison()
                .ignoringFields("password").isEqualTo(savedUser);
        assertThat(passwordEncoder.matches(
                checkUser.getPassword(), savedUser.getPassword())).isTrue();
    }

    @Test
    void testInvalidRegistrationFormFails() throws Exception {
        mockMvc
                .perform(post("/user/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstname", "first")
                        .param("lastname", "last")
                        .param("birthday", "1970-01-01")
                        .param("username", "use")
                        .param("password", "pass"))
                .andExpect(view().name("Register"))
                .andExpect(content().string(
                        containsString("muss mind. 4 Zeichen lang sein")));
        verify(newsUserRepository, never()).save(any());
    }

}
