package ak.spring.models;

import ak.spring.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    @Pattern(regexp = "^[А-Яа-яA-Za-z\\s-]+$", message = "Имя может содержать только буквы, пробел и дефис")
    private String firstName;

    @NotBlank(message = "Фамилия не должна быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов длиной")
    @Column(name = "last_name", nullable = false, length = 50)
    @Pattern(regexp = "^[А-Яа-яA-Za-z\\s-]+$", message = "Фамилия может содержать только буквы, пробел и дефис")
    private String lastName;

    @Size(max = 50, message = "Отчество должно быть до 50 символов длиной")
    @Column(name = "patronymic", length = 50)
    @Pattern(regexp = "^[А-Яа-яA-Za-z\\s-]+$", message = "Отчество может содержать только буквы, пробел и дефис")
    private String patronymic;

    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "Роль пользователя обязательна")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Size(max = 100, message = "Максимальное количество ответов — 100")
    private List<Answer> answers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "settings_id", referencedColumnName = "id")
    private Settings settings;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "classroom_id", referencedColumnName = "id")
    private Classroom classroom;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

