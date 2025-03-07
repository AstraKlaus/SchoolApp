package ak.spring.models;

import ak.spring.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person")
public class Person implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя пользователя должно быть от 2 до 50 символов длиной")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$", message = "Имя пользователя может содержать только латинские буквы, цифры и символ подчеркивания")
    private String username;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов длиной")
    @Column(name = "first_name", nullable = false, length = 50)
    @Pattern(regexp = "^[а-яА-ЯёЁ\\s]+$", message = "Имя может содержать только русские буквы и пробел")
    private String firstName;

    @NotBlank(message = "Фамилия не должна быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов длиной")
    @Column(name = "last_name", nullable = false, length = 50)
    @Pattern(regexp = "^[а-яА-ЯёЁ\\s]+$", message = "Фамилия может содержать только русские буквы и пробел")
    private String lastName;

    @Nullable
    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Pattern(regexp = "^$|^[а-яА-ЯёЁ\\s]+$", message = "Отчество может содержать только русские буквы и пробел")
    private String patronymic;

    @ToString.Exclude
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Роль пользователя обязательна")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @ToString.Exclude
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Size(max = 100, message = "Максимальное количество ответов — 100")
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_id", referencedColumnName = "id")
    private Settings settings;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "classroom_id", referencedColumnName = "id")
    private Classroom classroom;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Token> tokens = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

