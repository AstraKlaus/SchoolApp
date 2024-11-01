package ak.spring.mappers;

import ak.spring.dto.AnswerDTO;
import ak.spring.models.Answer;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AnswerDTOMapper implements Function<Answer, AnswerDTO> {

    @Override
    public AnswerDTO apply(Answer answer) {
        return new AnswerDTO(answer.getId(),
                answer.getText(),
                answer.getComment(),
                answer.getAttachment(),
                answer.getStudent().getId(),
                answer.getHomework().getId(),
                answer.getStatus().getId(),
                answer.getCreatedAt(),
                answer.getUpdatedAt());
    }
}