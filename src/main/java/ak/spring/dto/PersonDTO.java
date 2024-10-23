package ak.spring.dto;

import ak.spring.models.Role;
import ak.spring.models.Settings;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Role role;
    private SettingsDTO settings;
    private Integer classroomId;
}
