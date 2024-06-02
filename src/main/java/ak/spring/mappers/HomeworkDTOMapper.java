package ak.spring.mappers;

import ak.spring.dto.HomeworkDTO;
import ak.spring.models.Homework;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.function.Function;

@Service
public class HomeworkDTOMapper implements Function<Homework, HomeworkDTO> {
    @Override
    public HomeworkDTO apply(Homework homework) {
        return new HomeworkDTO(homework.getName(),
                homework.getDescription(),
                homework.getAttachment(),
                homework.getLesson(),
                homework.getCreatedAt(),
                new Timestamp(System.currentTimeMillis()));
    }
}