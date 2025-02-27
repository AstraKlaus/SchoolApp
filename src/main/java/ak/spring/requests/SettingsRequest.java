package ak.spring.requests;

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
public class SettingsRequest {

    @NotNull(message = "Тема обязательна для выбора")
    private String theme;

    @NotNull(message = "Размер шрифта обязателен для выбора")
    private String fontSize;

    @NotNull(message = "Поле 'Использовать шрифт с засечками' не может быть пустым")
    private Boolean isSerif;

    @NotNull(message = "Межстрочный интервал обязателен для выбора")
    private String lineHeight;

    @NotNull(message = "Межбуквенный интервал обязателен для выбора")
    private String letterSpacing;

    @NotNull(message = "Поле 'скрытие изображений' не может быть пустым")
    private Boolean imgHiding;
}


