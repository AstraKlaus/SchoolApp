package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {

    @NotNull(message = "Студент обязателен для регистрации")
    @Valid
    private PersonDTO student;

    @NotNull(message = "Курс обязателен для регистрации")
    @Valid
    private CourseDTO course;

    private Timestamp createdAt;
}

