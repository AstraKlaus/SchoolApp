package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.models.Curriculum;
import ak.spring.models.Person;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CurriculumService {

    private final CurriculumDTOMapper curriculumDTOMapper;
    private final CourseDTOMapper courseDTOMapper;
    private final ClassroomDTOMapper classroomDTOMapper;
    private final  CurriculumRepository curriculumRepository;
    private final ClassroomRepository classroomRepository;
    private final PersonRepository personRepository;

    @Autowired
    public CurriculumService(CurriculumDTOMapper curriculumDTOMapper,
                             CourseDTOMapper courseDTOMapper,
                             ClassroomDTOMapper classroomDTOMapper,
                             CurriculumRepository curriculumRepository,
                             ClassroomRepository classroomRepository,
                             PersonRepository personRepository) {
        this.curriculumDTOMapper = curriculumDTOMapper;
        this.courseDTOMapper = courseDTOMapper;
        this.classroomDTOMapper = classroomDTOMapper;
        this.curriculumRepository = curriculumRepository;
        this.classroomRepository = classroomRepository;
        this.personRepository = personRepository;
    }

    public List<CurriculumDTO> getAllCurricula() {
        return curriculumRepository.findAll()
                .stream()
                .map(curriculumDTOMapper)
                .toList();
    }

    public CurriculumDTO getCurriculumById(int id) {
        return curriculumRepository.findById(id)
                .map(curriculumDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", id));
    }

    public CurriculumDTO saveCurriculum(Curriculum curriculum) {
        curriculumRepository.save(curriculum);
        return curriculumDTOMapper.apply(curriculum);
    }

    public void deleteCurriculum(int id) {
        curriculumRepository.deleteById(id);
    }

    public List<CourseDTO> getCourseById(int id) {
        return curriculumRepository.findById(id)
                .map(curriculum -> curriculum.getCourses()
                        .stream()
                        .map(courseDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", id));
    }

    public List<ClassroomDTO> getClassroomById(int id) {
        return curriculumRepository.findById(id)
                .map(curriculum -> curriculum.getClassrooms()
                        .stream()
                        .map(classroomDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", id));
    }

    public CurriculumDTO updateCurriculum(int id, CurriculumDTO curriculumDetails) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", id));

        curriculum.setName(curriculumDetails.getName());
        curriculum.setDescription(curriculumDetails.getDescription());
        curriculum.setAccess(curriculumDetails.getAccess());

        curriculumRepository.save(curriculum);
        return curriculumDTOMapper.apply(curriculum);
    }

    public List<CurriculumDTO> findByName(String name) {
        return curriculumRepository.findByName(name)
                .stream()
                .map(curriculumDTOMapper)
                .toList();
    }

    public CurriculumDTO addClassroomToCurriculum(int curriculumId, int classroomId) {
        return curriculumRepository.findById(curriculumId)
                .map(curriculum -> {
                    curriculum.getClassrooms().add(classroomRepository.findById(classroomId)
                            .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId)));
                    curriculumRepository.save(curriculum);
                    return curriculumDTOMapper.apply(curriculum);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId));
    }

    public void deleteClassroomFromCurriculum(int curriculumId, int classroomId) {
        // Получаем Curriculum
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId));

        // Получаем Classroom
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId));

        // Удаляем Classroom из Curriculum
        curriculum.getClassrooms().remove(classroom);

        // Обнуляем classroomId у всех студентов, связанных с этим Classroom
        List<Person> persons = personRepository.findByClassroomId(classroomId);
        for (Person person : persons) {
            person.setClassroom(null); // Обнуляем связь с Classroom
        }
        personRepository.saveAll(persons); // Сохраняем изменения для всех студентов

        // Сохраняем изменения в Curriculum
        curriculumRepository.save(curriculum);
    }


    public Page<CurriculumDTO> findWithPagination(int page, int size) {
        Page<Curriculum> curricula = curriculumRepository.findAll(PageRequest.of(page, size, Sort.by("name").ascending()));
        return curricula.map(curriculumDTOMapper);
    }

    public Page<ClassroomDTO> getPaginatedClassrooms(int curriculumId, int page, int size) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId));

        List<Classroom> classrooms = curriculum.getClassrooms();
        PageRequest pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getPageSize(), classrooms.size());

        if(start > classrooms.size()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, classrooms.size());
        }

        return new PageImpl<>(
                classrooms.subList(start, end),
                pageRequest,
                classrooms.size()
        ).map(classroomDTOMapper);
    }

}
