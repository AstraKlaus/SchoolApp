package ak.spring.services;
import ak.spring.dto.AnswerDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Homework;
import ak.spring.repositories.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final CourseRepository courseRepository;
    private final AnswerRepository answerRepository;
    private final LessonDTOMapper lessonDTOMapper;
    private final LessonRepository lessonRepository;
    private final CurriculumDTOMapper curriculumDTOMapper;
    private final AnswerDTOMapper answerDTOMapper;
    private final HomeworkDTOMapper homeworkDTOMapper;
    private final MinioService minioService;

    @Autowired
    public HomeworkService(HomeworkRepository homeworkRepository,
                           CourseRepository courseRepository,
                           AnswerRepository answerRepository, LessonDTOMapper lessonDTOMapper,
                           LessonRepository lessonRepository,
                           CurriculumDTOMapper curriculumDTOMapper,
                           AnswerDTOMapper answerDTOMapper,
                           HomeworkDTOMapper homeworkDTOMapper,
                           MinioService minioService) {
        this.homeworkRepository = homeworkRepository;
        this.courseRepository = courseRepository;
        this.answerRepository = answerRepository;
        this.lessonDTOMapper = lessonDTOMapper;
        this.lessonRepository = lessonRepository;
        this.curriculumDTOMapper = curriculumDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
        this.homeworkDTOMapper = homeworkDTOMapper;
        this.minioService = minioService;
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

    public HomeworkDTO updateHomework(int id, HomeworkDTO updatedHomework) {
        Homework existingHomework = homeworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
        existingHomework.setName(updatedHomework.getName());
        existingHomework.setDescription(updatedHomework.getDescription());
        existingHomework.setAttachment(updatedHomework.getAttachment());
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

    public List<AnswerDTO> getAnswers(int id) {
        return homeworkRepository.findById(id)
                .map(homework -> homework.getAnswers()
                        .stream()
                        .map(answerDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public void uploadImage(int id, MultipartFile image) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = minioService.uploadFile(image);

        homeworkRepository.findById(id)
                .map(homework -> {
                    homework.setAttachment(url);
                    return homeworkRepository.save(homework);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public CurriculumDTO getCurriculum(int id) {
        return homeworkRepository.findById(id)
                .map(homework -> homework.getCourse().getCurriculum())
                .map(curriculumDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public void deleteAnswer(int id, int answerId) {
        homeworkRepository.findById(id)
                .map(homework -> {
                    homework.getAnswers()
                            .removeIf(answer -> answer.getId() == answerId);
                    return homeworkRepository.save(homework);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public HomeworkDTO addCourseToHomework(int id, int courseId) {
        return homeworkRepository.findById(id)
                .map(homework -> {
                    homework.setCourse(courseRepository.findById(courseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId)));
                    homeworkRepository.save(homework);
                    return homeworkDTOMapper.apply(homework);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public HomeworkDTO addAnswerToHomework(int id, int answerId) {
        return homeworkRepository.findById(id)
                .map(homework -> {
                    homework.getAnswers()
                            .add(answerRepository.findById(answerId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId)));
                    homeworkRepository.save(homework);
                    return homeworkDTOMapper.apply(homework);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", id));
    }

    public InputStream getObject(String filename) {
        return minioService.getObject(filename);
    }
}

