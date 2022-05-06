package de.binerys.newsapp.user.repository;

import de.binerys.newsapp.user.NewsUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsUserRepository extends JpaRepository<NewsUser, String> {
    NewsUser findByUsername(String username);
}
