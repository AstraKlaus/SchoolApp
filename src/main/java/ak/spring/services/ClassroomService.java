package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.repositories.ClassroomRepository;
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
    private final PersonRepository personRepository;
    private final ClassroomDTOMapper classroomDTOMapper;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository,
                            PersonRepository personRepository,
                            ClassroomDTOMapper classroomDTOMapper) {
        this.classroomRepository = classroomRepository;
        this.personRepository = personRepository;
        this.classroomDTOMapper = classroomDTOMapper;
    }

    public List<Classroom> findByName(String name) {
        return classroomRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "name", name));
    }

    public Page<Classroom> findWithPagination(int offset, int pageSize) {
        return classroomRepository.findAll(PageRequest.of(offset, pageSize));

    }

    public Classroom findById(int id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));
    }

    public Classroom updateGroup(int id, Classroom updatedClassroom) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));;
        existingClassroom.setName(updatedClassroom.getName());
        existingClassroom.setTeacher(updatedClassroom.getTeacher());
        return classroomRepository.save(existingClassroom);
    }


    public void deleteGroup(int id) {
        Classroom existingClassroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", id));
        classroomRepository.delete(existingClassroom);
    }
    public Classroom uploadGroup(Classroom classroom){
        Classroom newClassroom = Classroom.builder()
                .name(classroom.getName())
                .teacher(classroom.getTeacher())
                .build();
        return classroomRepository.save(newClassroom);
    }

    public List<Classroom> findAll() {
        return classroomRepository.findAll();
    }


//    public PersonDTO getTeacher(int id) {
//        return findById(id).getTeacher();
//    }

//    public List<PersonDTO> getStudents(int id) {
//        return findById(id).getPersons();
//    }
}
