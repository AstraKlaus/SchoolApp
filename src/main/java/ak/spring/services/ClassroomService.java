package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.models.Person;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomDTOMapper classroomDTOMapper;
    private final CurriculumDTOMapper curriculumDTOMapper;
    private final PersonRepository personRepository;
    private final CurriculumRepository curriculumRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository,
                            ClassroomDTOMapper classroomDTOMapper, CurriculumDTOMapper curriculumDTOMapper, PersonRepository personRepository, CurriculumRepository curriculumRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomDTOMapper = classroomDTOMapper;
        this.curriculumDTOMapper = curriculumDTOMapper;
        this.personRepository = personRepository;
        this.curriculumRepository = curriculumRepository;
    }

    public List<ClassroomDTO> findByName(String name) {
        return classroomRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "name", name))
                .stream()
                .map(classroomDTOMapper)
                .toList();
    }

    public Page<ClassroomDTO> findWithPagination(int offset, int pageSize) {
        Page<Classroom> classrooms = classroomRepository.findAll(PageRequest.of(offset, pageSize));
        return classrooms.map(classroomDTOMapper);
    }

    public ClassroomDTO findById(int id) {
        return classroomRepository.findById(id)
                .map(classroomDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));
    }

    public ClassroomDTO updateGroup(int id, ClassroomDTO updatedClassroom) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));
        existingClassroom.setName(updatedClassroom.getName());

        classroomRepository.save(existingClassroom);
        return classroomDTOMapper.apply(existingClassroom);
    }


    public void deleteGroup(int id) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));
        classroomRepository.delete(existingClassroom);
    }
    public ClassroomDTO uploadGroup(Classroom classroom){
        Classroom newClassroom = Classroom.builder()
                .name(classroom.getName())
                .curriculum(classroom.getCurriculum())
                .persons(classroom.getPersons())
                .build();
        classroomRepository.save(newClassroom);

        return classroomDTOMapper.apply(newClassroom);
    }

    public List<ClassroomDTO> findAll() {
        return classroomRepository.findAll().stream()
                .map(classroomDTOMapper)
                .toList();
    }


    public List<PersonDTO> getStudents(int id) {
        return findById(id).getPersons();
    }

    public CurriculumDTO getCurriculum(int id) {
        return classroomRepository.findById(id)
                .map(Classroom::getCurriculum)
                .map(curriculumDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

//
//    @Transactional
//    public ClassroomDTO addStudentToClassroom(int classroomId, int studentId) {
//        return classroomRepository.findById(classroomId)
//                .map(classroom -> {
//                    Person person = personRepository.findById(studentId)
//                            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
//                    classroom.getPersons().add(person);
//
//                    classroomRepository.save(classroom);
//                    personRepository.save(person);
//                    return classroomDTOMapper.apply(classroom);
//                })
//                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));
//    }

    @Transactional
    public ClassroomDTO addStudentToClassroom(int classroomId, int studentId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));

        Person student = personRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        student.setClassroom(classroom);

        List<Person> persons = classroom.getPersons();
        persons.add(student);
        classroom.setPersons(persons);

        // Логирование для проверки содержимого списка persons
        System.out.println("Persons in classroom before save: " + classroom.getPersons());

        classroomRepository.save(classroom);
        personRepository.save(student);

        // Логирование для проверки успешного сохранения
        System.out.println("Classroom saved successfully");

        ClassroomDTO classroomDTO = classroomDTOMapper.apply(classroom);

        // Логирование для проверки содержимого classroomDTO
        System.out.println("ClassroomDTO: " + classroomDTO);

        return classroomDTO;
    }


    public void deleteStudentFromClassroom(int classroomId, int studentId) {
        Person student = personRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        student.setClassroom(null);
        personRepository.save(student);

        classroomRepository.findById(classroomId)
                .map(classroom -> {
                    classroom.getPersons().removeIf(person -> person.getId() == studentId);
                    classroomRepository.save(classroom);
                    return classroom;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));
    }

    public ClassroomDTO addClassroomToCurriculum(int classroomId, int curriculumId) {
        return classroomRepository.findById(classroomId)
                .map(classroom -> {
                    classroom.setCurriculum(curriculumRepository.findById(curriculumId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId)));
                    classroomRepository.save(classroom);
                    return classroomDTOMapper.apply(classroom);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));
    }

    public void deleteClassroomFromCurriculum(int classroomId, int curriculumId) {
        classroomRepository.findById(classroomId)
                .map(classroom -> {
                    classroom.setCurriculum(null);
                    classroomRepository.save(classroom);
                    return classroom;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));
    }
}
