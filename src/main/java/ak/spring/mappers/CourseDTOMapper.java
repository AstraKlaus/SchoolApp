package ak.spring.mappers;

import ak.spring.dto.CourseDTO;
import ak.spring.models.Course;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Lazy
public class CourseDTOMapper implements Function<Course, CourseDTO> {

    private final PersonDTOMapper personMapper;

    public CourseDTOMapper(@Lazy PersonDTOMapper personMapper) {
        this.personMapper = personMapper;
    }

    @Override
    public CourseDTO apply(Course course) {
        return new CourseDTO(course.getId(),
                course.getName(),
                course.getDescription(),
                course.isAccess(),
                course.getStudents()
                        .stream()
                        .map(personMapper)
                        .toList());
    }
}