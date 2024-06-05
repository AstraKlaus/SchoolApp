package ak.spring.services;

import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Lesson;
import ak.spring.repositories.LessonRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.requests.LessonRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class LessonService {
    private final LessonRepository lessonRepository;
    private final PersonRepository personRepository;
    private final LessonDTOMapper lessonDTOMapper;

    @Autowired
    public LessonService(LessonRepository lessonRepository,
                         PersonRepository personRepository,
                         LessonDTOMapper lessonDTOMapper) {
        this.lessonRepository = lessonRepository;
        this.personRepository = personRepository;
        this.lessonDTOMapper = lessonDTOMapper;
    }

    public List<Lesson> findByName(String name) {
        return lessonRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "name", name));
    }

    public Page<Lesson> findWithPagination(int offset, int pageSize) {
        return lessonRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Lesson findById(int id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
    }

    public void deleteLesson(int id) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        lessonRepository.delete(existingLesson);
    }

    public Lesson saveLesson(Lesson lesson) {
        Lesson newLesson = Lesson.builder()
                .name(lesson.getName())
                .course(lesson.getCourse())
                .content(lesson.getContent())
                .build();
        return lessonRepository.save(newLesson);
    }

    public Lesson updateLesson(int id, Lesson updatedLesson) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        existingLesson.setName(updatedLesson.getName());
        existingLesson.setContent(updatedLesson.getContent());
        existingLesson.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        existingLesson.setCourse(updatedLesson.getCourse());
        return lessonRepository.save(existingLesson);
    }

    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }
}
