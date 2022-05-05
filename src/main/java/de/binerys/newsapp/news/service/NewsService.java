package de.binerys.newsapp.news.service;

import de.binerys.newsapp.news.NewsItem;

import java.util.List;

public interface NewsService {
    List<NewsItem> findNews(int count);
}
