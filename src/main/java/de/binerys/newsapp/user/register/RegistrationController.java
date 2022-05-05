package de.binerys.newsapp.user.register;

import de.binerys.newsapp.user.NewsUser;
import de.binerys.newsapp.user.repository.NewsUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/user/register")
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;
    private final NewsUserRepository newsUserRepository;

    public RegistrationController(PasswordEncoder passwordEncoder, NewsUserRepository newsUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.newsUserRepository = newsUserRepository;
    }

    @GetMapping
    public String showRegistration(Model model) {
        model.addAttribute("newsUser", new NewsUser());
        return "Register";
    }

    @PostMapping
    public String processRegistration(@Valid NewsUser user, BindingResult bindingResult) {
        if(newsUserRepository.findByUsername(user.getUsername()) != null) {
            bindingResult.addError(new FieldError("newsUser", "username", "Benutzer existiert bereits!"));
        }
        if (bindingResult.hasErrors()) {
                    return "Register";
        }

        var passwordHash = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordHash);

        newsUserRepository.save(user);
        return "redirect:/user/profile/" +
                URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
    }

}
