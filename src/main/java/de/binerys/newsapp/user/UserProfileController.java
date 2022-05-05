package de.binerys.newsapp.user;

import de.binerys.newsapp.user.repository.NewsUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UserProfileController {
    private final NewsUserRepository newsUserRepository;

    public UserProfileController(NewsUserRepository newsUserRepository) {
        this.newsUserRepository = newsUserRepository;
    }

    @GetMapping("/user/profile/{username}")
    public String showProfile(Model model,
                              @PathVariable("username") String username) {
        var user = newsUserRepository.findByUsername(username);

        if (user == null) {
            throw new UnknownNewsUserException("Der angegbene User existiert nicht!");
        }
        model.addAttribute("newsUser", user);
        return "Profile";
    }

    @ExceptionHandler(UnknownNewsUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFound() {
        return "UserNotFound";
    }


}
