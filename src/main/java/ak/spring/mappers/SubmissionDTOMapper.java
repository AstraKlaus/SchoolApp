package ak.spring.mappers;

import ak.spring.dto.SubmissionDTO;
import ak.spring.models.Person;
import ak.spring.models.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Lazy
public class SubmissionDTOMapper implements Function<Submission, SubmissionDTO> {

    private final PersonDTOMapper personDTOMapper;

    @Autowired
    public SubmissionDTOMapper(PersonDTOMapper personDTOMapper) {
        this.personDTOMapper = personDTOMapper;
    }

    @Override
    public SubmissionDTO apply(Submission submission) {
        return new SubmissionDTO(personDTOMapper.apply(submission.getStudent()),
                submission.getFeedback(),
                submission.getFile(),
                submission.getCreatedAt(),
                submission.getUpdatedAt());
    }
}