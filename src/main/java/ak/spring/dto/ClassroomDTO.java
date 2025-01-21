package ak.spring.dto;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {

    private int id;

    @NotBlank(message = "Название класса не может быть пустым")
    @Size(min = 3, max = 100, message = "Название класса должно содержать от 3 до 100 символов")
    private String name;

    @Size(max = 50, message = "Максимальное количество студентов в классе — 50")
    private List<@Valid PersonDTO> persons;
}

