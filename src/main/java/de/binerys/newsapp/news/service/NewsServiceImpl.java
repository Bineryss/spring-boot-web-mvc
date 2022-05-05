package de.binerys.newsapp.news.service;

import de.binerys.newsapp.news.NewsItem;
import de.binerys.newsapp.news.NewsItemContainer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class NewsServiceImpl implements NewsService{
    private final NewsServiceProperties properties;
    private final RestTemplate restTemplate;

    private static final Logger log = Logger.getLogger(NewsServiceImpl.class.toString());

    private static final String API_CALL = "v2/top-headlines?" + "country=de&" +
            "apiKey={apikey}&" +
            "pageSize={count}&" +
            "page=0";

    public NewsServiceImpl(NewsServiceProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(cacheNames = "news.cache")
    public List<NewsItem> findNews(int count) {
        log.info("Calling API...");

        var params = Map.of(
                "count", String.valueOf(count),
                "apikey", properties.key()
        );

        var container =
                restTemplate.getForObject(
                        properties.baseUrl() + API_CALL,
                        NewsItemContainer.class,
                        params);

        if(container == null) {
            throw new IllegalCallerException("Der Api Aufruf ist fehlgeschlagen!");
        }
        return container.articles();
    }
}
