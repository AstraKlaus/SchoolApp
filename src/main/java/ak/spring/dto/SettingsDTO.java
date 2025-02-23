package ak.spring.dto;

import ak.spring.models.FontSize;
import ak.spring.models.LetterSpacing;
import ak.spring.models.LineHeight;
import ak.spring.models.Theme;
import lombok.*;

import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {

    private int id;

    @NotNull(message = "Тема обязательна для выбора")
    private Theme theme;

    @NotNull(message = "Размер шрифта обязателен для выбора")
    private FontSize fontSize;

    @NotNull(message = "Поле 'Использовать шрифт с засечками' не может быть пустым")
    private Boolean isSerif;

    @NotNull(message = "Межстрочный интервал обязателен для выбора")
    private LineHeight lineHeight;

    @NotNull(message = "Межбуквенный интервал обязателен для выбора")
    private LetterSpacing letterSpacing;

    @NotNull(message = "Поле 'скрытие изображений' не может быть пустым")
    private Boolean imgHiding;
}

