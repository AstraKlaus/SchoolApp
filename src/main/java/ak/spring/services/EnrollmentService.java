package ak.spring.services;

import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Enrollment;
import ak.spring.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    public Enrollment findById(int id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
    }

    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    public void delete(Enrollment enrollment) {
        enrollmentRepository.delete(enrollment);
    }

    public Enrollment update(int id, Enrollment updatedEnrollment) {
        Enrollment existingEnrollment = findById(id);
        existingEnrollment.setPerson(updatedEnrollment.getPerson());
        existingEnrollment.setCourse(updatedEnrollment.getCourse());
        return enrollmentRepository.save(existingEnrollment);
    }
}