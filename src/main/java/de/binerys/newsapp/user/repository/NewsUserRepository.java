package de.binerys.newsapp.user.repository;

import de.binerys.newsapp.user.NewsUser;

public interface NewsUserRepository {
    NewsUser findByUsername(String username);
    NewsUser save(NewsUser user);
    long count();
}
