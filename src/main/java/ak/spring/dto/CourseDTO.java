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
    @Size(min = 5, max = 100, message = "Название курса должно содержать от 5 до 100 символов")
    private String name;

    @Size(max = 500, message = "Описание курса не должно превышать 500 символов")
    private String description;

    private boolean access;
}

