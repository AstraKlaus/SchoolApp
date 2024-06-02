package ak.spring.services;

import ak.spring.dto.EnrollmentDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.EnrollmentDTOMapper;
import ak.spring.models.Enrollment;
import ak.spring.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentDTOMapper enrollmentDTOMapper;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             EnrollmentDTOMapper enrollmentDTOMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentDTOMapper = enrollmentDTOMapper;
    }

    public List<EnrollmentDTO> findAll() {
        return enrollmentRepository.findAll()
                .stream()
                .map(enrollmentDTOMapper)
                .toList();
    }

    public EnrollmentDTO findById(int id) {
        return enrollmentRepository.findById(id).map(enrollmentDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
    }

    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public void delete(int id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
        enrollmentRepository.delete(enrollment);
    }

    public Enrollment update(int id, Enrollment updatedEnrollment) {
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
        existingEnrollment.setPerson(updatedEnrollment.getPerson());
        existingEnrollment.setCourse(updatedEnrollment.getCourse());
        return enrollmentRepository.save(existingEnrollment);
    }
}