package ak.spring.dto;

import ak.spring.models.FontSize;
import ak.spring.models.Theme;
import lombok.*;

import jakarta.validation.constraints.*;
import lombok.*;

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
}

