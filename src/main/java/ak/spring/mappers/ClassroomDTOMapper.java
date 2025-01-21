package ak.spring.mappers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.models.Classroom;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Lazy
public class ClassroomDTOMapper implements Function<Classroom, ClassroomDTO> {
    private final PersonDTOMapper personMapper;

    public ClassroomDTOMapper(@Lazy PersonDTOMapper personMapper) {
        this.personMapper = personMapper;
    }

    @Override
    public ClassroomDTO apply(Classroom classroom) {
        return new ClassroomDTO(classroom.getId(),
                classroom.getName(),
                classroom.getPersons()
                        .stream()
                        .map(personMapper)
                        .toList());
    }
}
