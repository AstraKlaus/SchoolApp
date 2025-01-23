package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotNull(message = "Тема обязательна")
    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @NotNull(message = "Размер шрифта обязателен")
    @ManyToOne
    @JoinColumn(name = "font_size_id", nullable = false)
    private FontSize fontSize;

    @NotNull(message = "Поле 'шрифт с засечками' не может быть пустым")
    @Column(name = "is_serif", nullable = false)
    private Boolean isSerif;

    @OneToMany(mappedBy = "settings", orphanRemoval = true, fetch = FetchType.LAZY)
    @Size(max = 100, message = "Максимальное количество пользователей — 100")
    private List<Person> person;
}


