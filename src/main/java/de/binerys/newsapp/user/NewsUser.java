package de.binerys.newsapp.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "NEWS_USER")
@AllArgsConstructor
@NoArgsConstructor
public class NewsUser {
    @Id
    @Size(min = 4, message = "muss mind. {min} Zeichen lang sein")
    @NotBlank
    private String username;
    @Size(min = 4, message = "muss mind. {min} Zeichen lang sein")
    @NotBlank
    private String password;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Past
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
