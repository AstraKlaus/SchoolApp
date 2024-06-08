package ak.spring.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private int id;
    private String name;
    private List<PersonDTO> persons;
}
