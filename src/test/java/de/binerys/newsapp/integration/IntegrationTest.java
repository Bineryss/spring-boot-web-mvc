package de.binerys.newsapp.integration;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import de.binerys.newsapp.user.NewsUser;
import de.binerys.newsapp.user.repository.NewsUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTest {
    @LocalServerPort
    private int port;

    @SpyBean
    NewsUserRepository newsUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    static final boolean HEADLESS = "true".equals(System.getenv("CI"));

    @Test
    void testRegistration() {
        var checkUser = new NewsUser("user", "pass", "first", "last", LocalDate.of(1970, 1, 1));

        try (var playwright = Playwright.create()) {
            var browserType = playwright.chromium();
            var launchOptions = new BrowserType.LaunchOptions();
            launchOptions.headless = HEADLESS;

            try (var browser = browserType.launch(launchOptions)) {
                var contextOptions = new Browser.NewContextOptions();
                contextOptions.locale = "de-DE";
                var context = browser.newContext(contextOptions);

                var page = context.newPage();
                page.navigate("http://localhost:" + port + "/user/register");
                page.type("#firstname", checkUser.getFirstname());
                page.type("#lastname", checkUser.getLastname());
                page.type("#username", checkUser.getUsername());
                page.type("#password", checkUser.getPassword());
                page.type("#birthday", checkUser.getBirthday()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                page.click("button");

                var captor = ArgumentCaptor.forClass(NewsUser.class);
                verify(newsUserRepository).save(captor.capture());

                var savedUser = captor.getValue();

                assertThat(checkUser).usingRecursiveComparison()
                        .ignoringFields("password").isEqualTo(savedUser);
                assertThat(passwordEncoder.matches(
                        checkUser.getPassword(), savedUser.getPassword())).isTrue();

                assertEquals("http://localhost:" + port + "/user/profile/" +
                        URLEncoder.encode(checkUser.getUsername(), StandardCharsets.UTF_8), page.url());
                page.waitForSelector("text=Hello " + checkUser.getFirstname());

                assertThat(checkUser).usingRecursiveComparison()
                        .ignoringFields("password", "version").isEqualTo(savedUser);
            }
        }
    }

    @AfterEach
    void cleanup(){
        if(newsUserRepository.existsById("user")){
            newsUserRepository.deleteById("user");
        }
    }
}

