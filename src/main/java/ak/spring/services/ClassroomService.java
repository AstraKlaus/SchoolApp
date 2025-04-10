package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.models.Curriculum;
import ak.spring.models.Person;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomDTOMapper classroomDTOMapper;
    private final CurriculumDTOMapper curriculumDTOMapper;
    private final PersonRepository personRepository;
    private final CurriculumRepository curriculumRepository;
    private final PersonDTOMapper personDTOMapper;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository,
                            ClassroomDTOMapper classroomDTOMapper,
                            CurriculumDTOMapper curriculumDTOMapper,
                            PersonRepository personRepository,
                            CurriculumRepository curriculumRepository,
                            PersonDTOMapper personDTOMapper) {
        this.classroomRepository = classroomRepository;
        this.classroomDTOMapper = classroomDTOMapper;
        this.curriculumDTOMapper = curriculumDTOMapper;
        this.personRepository = personRepository;
        this.curriculumRepository = curriculumRepository;
        this.personDTOMapper = personDTOMapper;
    }

    public List<ClassroomDTO> findByName(String name) {
        return classroomRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "name", name))
                .stream()
                .map(classroomDTOMapper)
                .toList();
    }

    public Page<ClassroomDTO> findWithPagination(int offset, int pageSize) {
        Page<Classroom> classrooms = classroomRepository.findAll(PageRequest.of(offset, pageSize, Sort.by("name").ascending()));
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
                .curricula(classroom.getCurricula())
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

    public List<CurriculumDTO> getCurricula(int classroomId) {
        return classroomRepository.findById(classroomId)
                .map(Classroom::getCurricula)
                .map(curricula -> curricula.stream()
                        .map(curriculumDTOMapper)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));
    }



    @Transactional
    public ClassroomDTO addStudentToClassroom(int classroomId, int studentId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));

        Person student = personRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        // Проверка на максимальное количество студентов
        if (classroom.getPersons().size() >= 50) {
            throw new DataIntegrityViolationException("Максимальное количество студентов в классе — 50");
        }

        student.setClassroom(classroom);
        classroom.getPersons().add(student);

        classroomRepository.save(classroom);
        personRepository.save(student);

        return classroomDTOMapper.apply(classroom);
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
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));

        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId));

        if (!classroom.getCurricula().contains(curriculum)) {
            classroom.getCurricula().add(curriculum);
        }
        if (!curriculum.getClassrooms().contains(classroom)) {
            curriculum.getClassrooms().add(classroom);
        }
        classroomRepository.save(classroom);

        return classroomDTOMapper.apply(classroom);
    }

    public void deleteClassroomFromCurriculum(int classroomId, int curriculumId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));

        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId));

        boolean wasRemovedFromClassroom = classroom.getCurricula().removeIf(c -> c.getId() == curriculumId);

        if (!wasRemovedFromClassroom) {
            throw new ResourceNotFoundException("Связь между Classroom " + classroomId + " и Curriculum " + curriculumId + " не найдена");
        }

        curriculum.getClassrooms().removeIf(c -> c.getId() == classroomId);

        curriculumRepository.save(curriculum);
    }
}
