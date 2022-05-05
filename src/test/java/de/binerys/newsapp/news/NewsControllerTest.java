package de.binerys.newsapp.news;

import de.binerys.newsapp.news.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(NewsController.class)
public class NewsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    NewsService newsService;

    List<NewsItem> newsMock;

    @BeforeEach
    void setUp() {
        newsMock = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            newsMock.add(new NewsItem("Title " + i, "Desc", "", ""));
        }
        when(newsService.findNews(5)).thenReturn(newsMock);
    }

    @Test
    void testNewsController() throws Exception {

        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("news", equalTo(newsMock)))
                .andExpect(content().string(containsString("Title 0")));
    }
}