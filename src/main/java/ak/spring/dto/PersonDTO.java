package ak.spring.dto;

import ak.spring.models.Classroom;
import ak.spring.models.Enrollment;
import ak.spring.models.Role;
import ak.spring.models.Submission;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private List<String> courseName;
    private String classroomName;
}
