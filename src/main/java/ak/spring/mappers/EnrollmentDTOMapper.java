package ak.spring.mappers;

import ak.spring.dto.EnrollmentDTO;
import ak.spring.models.Enrollment;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EnrollmentDTOMapper implements Function<Enrollment, EnrollmentDTO> {
    private PersonDTOMapper personMapper;
    private CourseDTOMapper courseMapper;

    @Override
    public EnrollmentDTO apply(Enrollment enrollment) {
        return new EnrollmentDTO(personMapper.apply(enrollment.getPerson()),
                courseMapper.apply(enrollment.getCourse()),
                enrollment.getCreatedAt());
    }
}
