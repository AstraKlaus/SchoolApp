package ak.spring.mappers;

import ak.spring.dto.CourseDTO;
import ak.spring.models.Course;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CourseDTOMapper implements Function<Course, CourseDTO> {

    @Override
    public CourseDTO apply(Course course) {
        return new CourseDTO(course.getId(),
                course.getName(),
                course.getDescription(),
                course.isAccess());
    }
}