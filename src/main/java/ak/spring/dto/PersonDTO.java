package ak.spring.dto;

import ak.spring.models.Role;
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
    private Role role;
    private String classroomName;
}
