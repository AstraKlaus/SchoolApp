package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDTO {

    private int id;

    @NotBlank(message = "Название домашнего задания не может быть пустым")
    @Size(min = 2, max = 150, message = "Название домашнего задания должно содержать от 5 до 150 символов")
    private String name;

    @Size(max = 5000, message = "Описание домашнего задания не должно превышать 5000 символов")
    private String description;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    private List<String> attachments;

    @Positive(message = "Идентификатор домашнего задания должен быть положительным числом")
    @NotNull(message = "Идентификатор домашнего задания обязателен для заполнения")
    private Integer courseId;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    private Boolean access;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}

