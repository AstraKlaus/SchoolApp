package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {

    private int id;

    @NotBlank(message = "Название урока не может быть пустым")
    @Size(min = 2, max = 150, message = "Название урока должно содержать от 2 до 150 символов")
    private String name;


    @Size(min = 2, max = 5000, message = "Содержимое урока должно содержать от 2 до 5000 символов")
    private String content;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    private List<String> attachments;

    @Size(max = 500, message = "Описание урока не должно превышать 500 символов")
    private String description;

    @Positive(message = "Идентификатор домашнего задания должен быть положительным числом")
    @NotNull(message = "Идентификатор домашнего задания обязателен для заполнения")
    private Integer courseId;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    private Boolean access;

    private Timestamp createdAt;

    @PastOrPresent(message = "Дата обновления не может быть в будущем")
    private Timestamp updatedAt;
}

