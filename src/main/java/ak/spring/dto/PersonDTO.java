package ak.spring.dto;

import ak.spring.models.Role;
import lombok.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    private int id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$", message = "Имя пользователя может содержать только латинские буквы, цифры и символ подчеркивания")
    private String username;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    @Pattern(regexp = "^[а-яА-ЯёЁйЙ\\-\\s]+$", message = "Имя может содержать только буквы, пробел и дефис")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    @Pattern(regexp = "^[а-яА-ЯёЁйЙ\\-\\s]+$", message = "Фамилия может содержать только буквы, пробел и дефис")
    private String lastName;

    @Size(max = 50, message = "Отчество не должно превышать 50 символов")
    @Pattern(regexp = "^[а-яА-ЯёЁйЙ\\-\\s]+$", message = "Отчество может содержать только буквы, пробел и дефис")
    private String patronymic;

    @NotNull(message = "Роль пользователя обязательна")
    private Role role;

    @Valid
    private SettingsDTO settings;

    @Positive(message = "Идентификатор класса должен быть положительным числом")
    private Integer classroomId;
}
