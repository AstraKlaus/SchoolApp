package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private PersonDTO student;
    private CourseDTO course;
    private Timestamp createdAt;

}
