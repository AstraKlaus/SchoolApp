package ak.spring.mappers;

import ak.spring.dto.AnswerDTO;
import ak.spring.models.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AnswerDTOMapper implements Function<Answer, AnswerDTO> {

    @Override
    public AnswerDTO apply(Answer answer) {
        return new AnswerDTO(answer.getId(),
                answer.getComment(),
                answer.getAttachment(),
                answer.getDescription(),
                answer.getFile(),
                answer.getCreatedAt(),
                answer.getUpdatedAt());
    }
}