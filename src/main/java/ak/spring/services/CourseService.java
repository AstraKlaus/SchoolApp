package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Course;
import ak.spring.models.Curriculum;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.LessonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CourseDTOMapper courseDTOMapper;
    private final LessonDTOMapper lessonDTOMapper;
    private final HomeworkDTOMapper homeworkDTOMapper;
    private final CurriculumDTOMapper curriculumDTOMapper;
    private final HomeworkRepository homeworkRepository;
    private final CurriculumRepository curriculumRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository,
                         LessonRepository lessonRepository, CourseDTOMapper courseDTOMapper, LessonDTOMapper lessonDTOMapper, HomeworkDTOMapper homeworkDTOMapper, CurriculumDTOMapper curriculumDTOMapper, HomeworkRepository homeworkRepository, CurriculumRepository curriculumRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.courseDTOMapper = courseDTOMapper;
        this.lessonDTOMapper = lessonDTOMapper;
        this.homeworkDTOMapper = homeworkDTOMapper;
        this.curriculumDTOMapper = curriculumDTOMapper;
        this.homeworkRepository = homeworkRepository;
        this.curriculumRepository = curriculumRepository;
    }

    public List<CourseDTO> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(courseDTOMapper)
                .toList();
    }

    public Page<CourseDTO> findWithPagination(int offset, int pageSize) {
        Page<Course> courses = courseRepository.findAll(PageRequest.of(offset, pageSize));
        return courses.map(courseDTOMapper);
    }

    public List<CourseDTO> findByName(String name) {
        return courseRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "name", name))
                .stream()
                .map(courseDTOMapper)
                .toList();
    }

    public CourseDTO uploadCourse(CourseDTO courseDTO) {
        Course course = Course.builder()
                .name(courseDTO.getName())
                .description(courseDTO.getDescription())
                .access(courseDTO.getAccess())
                .curriculum(curriculumRepository.findById(courseDTO.getCurriculumId())
                        .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", courseDTO.getCurriculumId())))
                .build();

        return courseDTOMapper.apply(courseRepository.save(course));
    }

    public CourseDTO findById(int id){
        return courseRepository.findById(id).map(courseDTOMapper).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public void deleteCourse(int id){
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseRepository.delete(existingCourse);
    }

    public CourseDTO updateCourse(int id, CourseDTO updatedCourse) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setAccess(updatedCourse.getAccess());

        courseRepository.save(existingCourse);
        return courseDTOMapper.apply(existingCourse);
    }

    public CurriculumDTO getCurriculum(int id) {
        return courseRepository.findById(id)
                .map(Course::getCurriculum)
                .map(curriculumDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public List<LessonDTO> getLessons(int id) {
        return courseRepository.findById(id)
                .map(lesson -> lesson.getLessons()
                        .stream()
                        .map(lessonDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public List<HomeworkDTO> getHomeworks(int id) {
        return courseRepository.findById(id)
                .map(homework -> homework.getHomeworks()
                        .stream()
                        .map(homeworkDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public CourseDTO addLessonToCourse(int id, int lessonId) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.getLessons().add(lessonRepository.findById(lessonId)
                            .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId)));
                    courseRepository.save(course);
                    return courseDTOMapper.apply(course);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public CourseDTO addHomeworkToCourse(int id, int homeworkId) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.getHomeworks().add(homeworkRepository.findById(homeworkId)
                            .orElseThrow(() -> new ResourceNotFoundException("Homework", "id", homeworkId)));
                    courseRepository.save(course);
                    return courseDTOMapper.apply(course);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public CourseDTO addCurriculumToCourse(int courseId, int curriculumId) {
        return courseRepository.findById(courseId)
                .map(course -> {
                    course.setCurriculum(curriculumRepository.findById(curriculumId)
                            .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId)));
                    courseRepository.save(course);
                    return courseDTOMapper.apply(course);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    public void deleteCurriculumFromCourse(int courseId, int curriculumId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (course.getCurriculum().getId() != curriculumId) {
            throw new ResourceNotFoundException("Curriculum", "id", curriculumId);
        }

        course.setCurriculum(null);
        courseRepository.save(course);
    }
}
