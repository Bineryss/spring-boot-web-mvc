package de.binerys.newsapp.news.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "news.api")
@ConstructorBinding
public record NewsServiceProperties(String baseUrl, String key) {
}
