package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Lesson;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.LessonRepository;
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
    private final CourseDTOMapper courseDTOMapper;
    private final LessonDTOMapper lessonDTOMapper;
    private final CourseRepository courseRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository,
                         CourseDTOMapper courseDTOMapper,
                         LessonDTOMapper lessonDTOMapper,
                         CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseDTOMapper = courseDTOMapper;
        this.lessonDTOMapper = lessonDTOMapper;
        this.courseRepository = courseRepository;
    }

    public List<LessonDTO> findByName(String name) {
        return lessonRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "name", name))
                .stream()
                .map(lessonDTOMapper)
                .toList();
    }

    public Page<LessonDTO> findWithPagination(int offset, int pageSize) {
        Page<Lesson> lessons = lessonRepository.findAll(PageRequest.of(offset, pageSize));
        return lessons.map(lessonDTOMapper);
    }

    public LessonDTO findById(int id) {
        return lessonRepository.findById(id).map(lessonDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
    }

    public void deleteLesson(int id) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        lessonRepository.delete(existingLesson);
    }

    public LessonDTO saveLesson(Lesson lesson) {
        Lesson newLesson = Lesson.builder()
                .name(lesson.getName())
                .course(lesson.getCourse())
                .content(lesson.getContent())
                .attachments(lesson.getAttachments())
                .access(lesson.getAccess())
                .description(lesson.getDescription())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        lessonRepository.save(newLesson);

        return lessonDTOMapper.apply(newLesson);
    }

    public LessonDTO updateLesson(int id, LessonDTO updatedLesson) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        existingLesson.setName(updatedLesson.getName());
        existingLesson.setContent(updatedLesson.getContent());
        existingLesson.setAttachments(updatedLesson.getAttachments());
        existingLesson.setAccess(updatedLesson.getAccess());
        existingLesson.setDescription(updatedLesson.getDescription());
        existingLesson.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        lessonRepository.save(existingLesson);
        return lessonDTOMapper.apply(existingLesson);
    }

    public List<LessonDTO> findAll() {
        return lessonRepository.findAll()
                .stream()
                .map(lessonDTOMapper)
                .toList();
    }

    public CourseDTO getCourse(int id) {
        return lessonRepository.findById(id)
                .map(Lesson::getCourse)
                .map(courseDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }


    public LessonDTO addCourseToLesson(int id, int courseId) {
        return lessonRepository.findById(id)
                .map(lesson -> {
                    lesson.setCourse(courseRepository.findById(courseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId)));
                    lessonRepository.save(lesson);
                    return lessonDTOMapper.apply(lesson);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
    }
}
