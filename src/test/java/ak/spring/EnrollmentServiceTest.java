package ak.spring;

import ak.spring.dto.EnrollmentDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Course;
import ak.spring.models.Enrollment;
import ak.spring.models.Person;
import ak.spring.repositories.EnrollmentRepository;
import ak.spring.services.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Enrollment enrollment1;
    private Enrollment enrollment2;

    @BeforeEach
    void setUp() {
        enrollment1 = Enrollment.builder()
                .id(1L)
                .person(new Person())
                .course(new Course())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        enrollment2 = Enrollment.builder()
                .id(2L)
                .person(new Person())
                .course(new Course())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Test
    void findAll_shouldReturnListOfEnrollments() {
        List<Enrollment> expectedEnrollments = Arrays.asList(enrollment1, enrollment2);
        when(enrollmentRepository.findAll()).thenReturn(expectedEnrollments);

        List<Enrollment> actualEnrollments = enrollmentRepository.findAll();

        assertEquals(expectedEnrollments, actualEnrollments);
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnEnrollment_whenEnrollmentExists() {
        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment1));

        Enrollment actualEnrollment = enrollmentRepository.findById(1).get();

        assertEquals(enrollment1, actualEnrollment);
        verify(enrollmentRepository, times(1)).findById(1);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenEnrollmentDoesNotExist() {
        when(enrollmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.findById(1));
        verify(enrollmentRepository, times(1)).findById(1);
    }

    @Test
    void save_shouldSaveEnrollment() {
        when(enrollmentRepository.save(enrollment1)).thenReturn(enrollment1);

        Enrollment savedEnrollment = enrollmentService.save(enrollment1);

        assertEquals(enrollment1, savedEnrollment);
        verify(enrollmentRepository, times(1)).save(enrollment1);
    }

    @Test
    void delete_shouldDeleteEnrollment() {
        enrollmentService.delete(1);

        verify(enrollmentRepository, times(1)).delete(enrollment1);
    }

    @Test
    void update_shouldUpdateEnrollment_whenEnrollmentExists() {
        Enrollment updatedEnrollment = Enrollment.builder()
                .id(1L)
                .person(new Person())
                .course(new Course())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        when(enrollmentRepository.findById(1)).thenReturn(Optional.of(enrollment1));
        when(enrollmentRepository.save(enrollment1)).thenReturn(updatedEnrollment);

        Enrollment actualEnrollment = enrollmentService.update(1, updatedEnrollment);

        assertEquals(updatedEnrollment, actualEnrollment);
        verify(enrollmentRepository, times(1)).findById(1);
        verify(enrollmentRepository, times(1)).save(enrollment1);
    }
}

