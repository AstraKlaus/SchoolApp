package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.models.Course;
import ak.spring.models.Person;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final CourseDTOMapper courseDTOMapper;

    @Autowired
    public CourseService(CourseRepository courseRepository,
                         PersonRepository personRepository,
                         CourseDTOMapper courseDTOMapper) {
        this.courseRepository = courseRepository;
        this.personRepository = personRepository;
        this.courseDTOMapper = courseDTOMapper;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Page<Course> findWithPagination(int offset, int pageSize) {
        return courseRepository.findAll(PageRequest.of(offset, pageSize));

    }

    public List<Course> findByName(String name) {
        return courseRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "name", name));
    }

    public Course uploadCourse(Course course){
        Course newCourse = Course.builder()
                .description(course.getDescription())
                .name(course.getName())
                .students(course.getStudents())
                .build();

        personRepository.saveAll(course.getStudents());
        return courseRepository.save(newCourse);
    }

    public Course findById(int id){
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    public void deleteCourse(int id){
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseRepository.delete(existingCourse);
    }

    public Course updateCourse(int id, Course updatedCourse) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        return courseRepository.save(existingCourse);

    }

    public List<Person> getStudents(int id) {
        return findById(id).getStudents();
    }

    public void addPersonInCourse(int personId, int courseId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        if (person.getCourses() == null){
            person.setCourses(new ArrayList<>(Collections.singletonList(course)));
        }else {
            person.getCourses().add(course);
        }

        personRepository.save(person);
    }

    public void removePersonFromCourse(int personId, int courseId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        person.getCourses().remove(course);

        personRepository.save(person);
    }
}
