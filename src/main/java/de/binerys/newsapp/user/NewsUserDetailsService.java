package de.binerys.newsapp.user;

import de.binerys.newsapp.user.repository.NewsUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class NewsUserDetailsService implements UserDetailsService {

    private final NewsUserRepository newsUserRepository;

    public NewsUserDetailsService(NewsUserRepository newsUserRepository) {
        this.newsUserRepository = newsUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            var newsUser = newsUserRepository.findByUsername(username);
            if (newsUser == null) {
                throw new UsernameNotFoundException("Unknown User");
            }

            return new User(
                    newsUser.getUsername(),
                    newsUser.getPassword(),
                    Collections.emptyList()
            );
        }
}
