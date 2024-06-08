package ak.spring.services;
import ak.spring.dto.AnswerDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Homework;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final LessonDTOMapper lessonDTOMapper;
    private final LessonRepository lessonRepository;
    private final AnswerDTOMapper answerDTOMapper;
    private final HomeworkDTOMapper homeworkDTOMapper;

    @Autowired
    public HomeworkService(HomeworkRepository homeworkRepository,
                           LessonDTOMapper lessonDTOMapper, LessonRepository lessonRepository,
                           AnswerDTOMapper answerDTOMapper, HomeworkDTOMapper homeworkDTOMapper) {
        this.homeworkRepository = homeworkRepository;
        this.lessonDTOMapper = lessonDTOMapper;
        this.lessonRepository = lessonRepository;
        this.answerDTOMapper = answerDTOMapper;
        this.homeworkDTOMapper = homeworkDTOMapper;
    }

    public List<HomeworkDTO> findByName(String name) {
        return homeworkRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "name", name))
                .stream()
                .map(homeworkDTOMapper)
                .toList();
    }

    public Page<HomeworkDTO> findWithPagination(int offset, int pageSize) {
        Page<Homework> homeworks = homeworkRepository.findAll(PageRequest.of(offset, pageSize));
        return homeworks.map(homeworkDTOMapper);
    }

    public HomeworkDTO findById(int id) {
        return homeworkRepository.findById(id).map(homeworkDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public void deleteHomework(int id) {
        Homework existingHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
        homeworkRepository.delete(existingHomework);
    }

    public HomeworkDTO saveHomework(Homework homework) {
        Homework newHomework = Homework.builder()
                .name(homework.getName())
                .description(homework.getDescription())
                .attachment(homework.getAttachment())
                .access(homework.isAccess())
                .course(homework.getCourse())
                .answers(homework.getAnswers())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        homeworkRepository.save(newHomework);
        return homeworkDTOMapper.apply(newHomework);
    }

    public HomeworkDTO updateHomework(int id, Homework updatedHomework) {
        Homework existingHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
        existingHomework.setName(updatedHomework.getName());
        existingHomework.setDescription(updatedHomework.getDescription());
        existingHomework.setAttachment(updatedHomework.getAttachment());
        existingHomework.setAnswers(updatedHomework.getAnswers());
        existingHomework.setCourse(updatedHomework.getCourse());
        existingHomework.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        homeworkRepository.save(existingHomework);
        return homeworkDTOMapper.apply(existingHomework);
    }

    public List<HomeworkDTO> findAll() {
        return homeworkRepository.findAll()
                .stream()
                .map(homeworkDTOMapper)
                .toList();
    }

    public LessonDTO getLesson(int id) {
        return lessonRepository.findById(id)
                .map(lessonDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
    }

    public List<AnswerDTO> getAnswers(int id) {
        return homeworkRepository.findById(id)
                .map(homework -> homework.getAnswers()
                        .stream()
                        .map(answerDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }
}

