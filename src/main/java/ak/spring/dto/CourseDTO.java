package ak.spring.dto;

import ak.spring.models.Person;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private int id;

    @NotBlank(message = "Название курса не может быть пустым")
    @Size(min = 2, max = 150, message = "Название курса должно содержать от 2 до 150 символов")
    private String name;

    @Size(max = 5000, message = "Описание курса не должно превышать 5000 символов")
    private String description;

    private Boolean access;

    @Positive(message = "Идентификатор учебного плана должен быть положительным числом")
    @NotNull(message = "Идентификатор учебного плана обязателен для заполнения")
    private Integer curriculumId;
}

