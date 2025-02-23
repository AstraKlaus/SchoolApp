package ak.spring.mappers;

import ak.spring.dto.SettingsDTO;
import ak.spring.models.Settings;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SettingsDTOMapper implements Function<Settings, SettingsDTO> {
    @Override
    public SettingsDTO apply(Settings settings) {
        return SettingsDTO.builder()
                .id(settings.getId())
                .theme(settings.getTheme())
                .isSerif(settings.getIsSerif())
                .fontSize(settings.getFontSize())
                .lineHeight(settings.getLineHeight())
                .letterSpacing(settings.getLetterSpacing())
                .imgHiding(settings.getImgHiding())
                .build();
    }
}
