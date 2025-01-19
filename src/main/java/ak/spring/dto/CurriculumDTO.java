package ak.spring.dto;

import ak.spring.models.Classroom;
import ak.spring.models.Course;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurriculumDTO {

    @Positive(message = "Идентификатор учебного плана должен быть положительным числом")
    private int id;

    @NotBlank(message = "Название учебного плана не может быть пустым")
    @Size(min = 5, max = 150, message = "Название учебного плана должно содержать от 5 до 150 символов")
    private String name;

    @Size(max = 500, message = "Описание учебного плана не должно превышать 500 символов")
    private String description;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    private Boolean access;
}
