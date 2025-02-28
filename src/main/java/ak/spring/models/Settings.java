package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import jakarta.validation.constraints.*;

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

    @NotNull(message = "Межстрочный интервал обязателен")
    @ManyToOne
    @JoinColumn(name = "line_height_id", nullable = false)
    private LineHeight lineHeight;

    @NotNull(message = "Межбуквенный интервал обязателен")
    @ManyToOne
    @JoinColumn(name = "letter_spacing_id", nullable = false)
    private LetterSpacing letterSpacing;

    @NotNull(message = "Поле 'шрифт с засечками' не может быть пустым")
    @Column(name = "is_serif", nullable = false)
    private Boolean isSerif;

    @NotNull(message = "Поле 'скрытие изображений' не может быть пустым")
    @Column(name = "img_hiding", nullable = false)
    private Boolean imgHiding;

    @ToString.Exclude
    @OneToOne(mappedBy = "settings", fetch = FetchType.LAZY)
    private Person person;
}


