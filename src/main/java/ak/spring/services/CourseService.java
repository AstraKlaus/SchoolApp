package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Course;
import ak.spring.repositories.CourseRepository;
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
    private final CourseDTOMapper courseDTOMapper;
    private final LessonDTOMapper lessonDTOMapper;
    private final CurriculumDTOMapper curriculumDTOMapper;

    @Autowired
    public CourseService(CourseRepository courseRepository,
                         CourseDTOMapper courseDTOMapper, LessonDTOMapper lessonDTOMapper, CurriculumDTOMapper curriculumDTOMapper) {
        this.courseRepository = courseRepository;
        this.courseDTOMapper = courseDTOMapper;
        this.lessonDTOMapper = lessonDTOMapper;
        this.curriculumDTOMapper = curriculumDTOMapper;
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

    public CourseDTO uploadCourse(Course course){
        Course newCourse = Course.builder()
                .description(course.getDescription())
                .name(course.getName())
                .access(course.isAccess())
                .curriculum(course.getCurriculum())
                .homeworks(course.getHomeworks())
                .lessons(course.getLessons())
                .build();

        courseRepository.save(newCourse);
        return courseDTOMapper.apply(newCourse);
    }

    public CourseDTO findById(int id){
        return courseRepository.findById(id).map(courseDTOMapper).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public void deleteCourse(int id){
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseRepository.delete(existingCourse);
    }

    public CourseDTO updateCourse(int id, Course updatedCourse) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setAccess(updatedCourse.isAccess());
        existingCourse.setCurriculum(updatedCourse.getCurriculum());
        existingCourse.setLessons(updatedCourse.getLessons());

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
}
