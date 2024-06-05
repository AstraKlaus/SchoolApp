package ak.spring.dto;

import ak.spring.models.Person;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private int id;
    private String name;
    private String description;
    private boolean access;
    private List<PersonDTO> students;
}
