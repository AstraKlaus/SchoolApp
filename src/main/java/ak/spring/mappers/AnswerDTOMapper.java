package ak.spring.mappers;

import ak.spring.dto.AnswerDTO;
import ak.spring.models.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Lazy
public class AnswerDTOMapper implements Function<Answer, AnswerDTO> {

    private final PersonDTOMapper personDTOMapper;

    @Autowired
    public AnswerDTOMapper(PersonDTOMapper personDTOMapper) {
        this.personDTOMapper = personDTOMapper;
    }

    @Override
    public AnswerDTO apply(Answer answer) {
        return new AnswerDTO(answer.getId(),
                answer.getFeedback(),
                answer.getFile(),
                answer.getCreatedAt(),
                answer.getUpdatedAt());
    }
}