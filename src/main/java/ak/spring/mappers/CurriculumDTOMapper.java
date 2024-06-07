package ak.spring.mappers;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.models.Course;
import ak.spring.models.Curriculum;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CurriculumDTOMapper implements Function<Curriculum, CurriculumDTO> {

    @Override
    public CurriculumDTO apply(Curriculum curriculum) {
        return new CurriculumDTO(curriculum.getId(),
                curriculum.getName(),
                curriculum.getDescription(),
                curriculum.getAccess());
    }
}
