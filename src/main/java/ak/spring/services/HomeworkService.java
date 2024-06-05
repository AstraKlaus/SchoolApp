package ak.spring.services;
import ak.spring.dto.HomeworkDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.models.Homework;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final LessonRepository lessonRepository;
    private final HomeworkDTOMapper homeworkDTOMapper;

    @Autowired
    public HomeworkService(HomeworkRepository homeworkRepository,
                           LessonRepository lessonRepository,
                           HomeworkDTOMapper homeworkDTOMapper) {
        this.homeworkRepository = homeworkRepository;
        this.lessonRepository = lessonRepository;
        this.homeworkDTOMapper = homeworkDTOMapper;
    }

    public List<Homework> findByName(String name) {
        return homeworkRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "name", name));
    }

    public Page<Homework> findWithPagination(int offset, int pageSize) {
        return homeworkRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Homework findById(int id) {
        return homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public void deleteHomework(int id) {
        Homework existingHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
        homeworkRepository.delete(existingHomework);
    }

    public Homework saveHomework(Homework homework) {
        Homework newHomework = Homework.builder()
                .name(homework.getName())
                .description(homework.getDescription())
                .attachment(homework.getAttachment())
                .lesson(homework.getLesson())
                .build();
        return homeworkRepository.save(newHomework);
    }

    public Homework updateHomework(int id, Homework updatedHomework) {
        Homework existingHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
        existingHomework.setName(updatedHomework.getName());
        existingHomework.setDescription(updatedHomework.getDescription());
        existingHomework.setAttachment(updatedHomework.getAttachment());
        existingHomework.setLesson(updatedHomework.getLesson());
        return homeworkRepository.save(existingHomework);
    }

    public List<Homework> findAll() {
        return homeworkRepository.findAll();
    }
}

