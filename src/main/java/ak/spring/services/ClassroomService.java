package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.repositories.ClassroomRepository;
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

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository,
                            ClassroomDTOMapper classroomDTOMapper, CurriculumDTOMapper curriculumDTOMapper) {
        this.classroomRepository = classroomRepository;
        this.classroomDTOMapper = classroomDTOMapper;
        this.curriculumDTOMapper = curriculumDTOMapper;
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
}
