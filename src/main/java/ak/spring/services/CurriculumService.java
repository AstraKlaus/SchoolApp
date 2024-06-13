package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.models.Curriculum;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurriculumService {

    private final CurriculumDTOMapper curriculumDTOMapper;
    private final CourseDTOMapper courseDTOMapper;
    private final ClassroomDTOMapper classroomDTOMapper;
    private final  CurriculumRepository curriculumRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public CurriculumService(CurriculumDTOMapper curriculumDTOMapper,
                             CourseDTOMapper courseDTOMapper,
                             ClassroomDTOMapper classroomDTOMapper,
                             CurriculumRepository curriculumRepository,
                             ClassroomRepository classroomRepository) {
        this.curriculumDTOMapper = curriculumDTOMapper;
        this.courseDTOMapper = courseDTOMapper;
        this.classroomDTOMapper = classroomDTOMapper;
        this.curriculumRepository = curriculumRepository;
        this.classroomRepository = classroomRepository;
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
        curriculumRepository.findById(curriculumId)
                .map(curriculum -> {
                    curriculum.getClassrooms().remove(classroomRepository.findById(classroomId)
                            .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId)));
                    curriculumRepository.save(curriculum);
                    return curriculumDTOMapper.apply(curriculum);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum", "id", curriculumId));
    }
}
