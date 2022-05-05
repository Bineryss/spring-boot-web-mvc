package de.binerys.newsapp.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.binerys.newsapp.news.service.NewsService;
import de.binerys.newsapp.news.service.NewsServiceImpl;
import de.binerys.newsapp.news.service.NewsServiceProperties;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({
        NewsServiceImpl.class,
        JacksonAutoConfiguration.class,
        RestTemplate.class
})
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"news.api.key=testkey"})
@EnableConfigurationProperties(NewsServiceProperties.class)
public class NewsServiceMockWebServerTest {
    static MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() {
        mockWebServer = new MockWebServer();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("news.api.baseurl", () -> mockWebServer.url("/").toString());
    }

    @Test
    void testNewsService(@Autowired ObjectMapper mapper, @Autowired NewsService newsService) throws JsonProcessingException {

        NewsItemContainer mockNewsItemContainer = new NewsItemContainer(List.of(
                new NewsItem("Title", "Description", "Url", "Image")));

        var json = mapper.writeValueAsString(mockNewsItemContainer);
        prepareResponse(response -> response
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(json));

        var newsItems = newsService.findNews(1);
        assertEquals(mockNewsItemContainer.articles(), newsItems);
        expectRequest(request -> assertThat(request.getPath())
                .isEqualTo("/v2/top-headlines?country=de&apiKey=testkey&pageSize=1&page=0"));
    }


    private void prepareResponse(Consumer<MockResponse> consumer) {
        MockResponse response = new MockResponse();
        consumer.accept(response);
        mockWebServer.enqueue(response);
    }
    private void expectRequest(Consumer<RecordedRequest> consumer) {
        try {
            consumer.accept(mockWebServer.takeRequest());
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
