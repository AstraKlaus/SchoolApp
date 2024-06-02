package ak.spring.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private String name;
    private PersonDTO teacher;
    private List<PersonDTO> persons;
}
