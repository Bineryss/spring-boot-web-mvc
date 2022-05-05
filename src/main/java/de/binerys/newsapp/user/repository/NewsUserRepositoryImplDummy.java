package de.binerys.newsapp.user.repository;

import de.binerys.newsapp.user.NewsUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NewsUserRepositoryImplDummy implements NewsUserRepository {
    private final Map<String, NewsUser> map = new ConcurrentHashMap<>();

    public NewsUserRepositoryImplDummy(PasswordEncoder passwordEncoder) {
        map.put("buck", new NewsUser("buck", passwordEncoder.encode("buck"), "Buck", "Rogers", LocalDate.now()));
    }
    @Override
    public NewsUser findByUsername(String username) {
        return map.get(username);
    }

    @Override
    public NewsUser save(NewsUser user) {
        map.put(user.getUsername(), user);
        return user;
    }

    @Override
    public long count() {
        return map.size();
    }
}
