package ak.spring.mappers;

import ak.spring.dto.LessonDTO;
import ak.spring.models.Lesson;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.function.Function;

@Service
public class LessonDTOMapper implements Function<Lesson, LessonDTO> {
    @Override
    public LessonDTO apply(Lesson lesson) {
        return new LessonDTO(lesson.getId(),
                lesson.getName(),
                lesson.getContent(),
                lesson.getAttachments(),
                lesson.getDescription(),
                lesson.getCourse().getId(),
                lesson.getAccess(),
                lesson.getCreatedAt(),
                new Timestamp(System.currentTimeMillis()));
    }
}
