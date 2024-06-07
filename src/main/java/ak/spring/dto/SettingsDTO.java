package ak.spring.dto;

import ak.spring.models.FontSize;
import ak.spring.models.Theme;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {
    private int id;
    private Theme theme;
    private FontSize fontSize;
    private Boolean isSerif;
}
