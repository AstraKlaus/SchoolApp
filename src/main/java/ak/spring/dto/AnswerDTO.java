package ak.spring.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {

    private int id;

    @Size(max = 5000, message = "Текст ответа должен содержать до 5000 символов")
    private String text;

    @Size(max = 5000, message = "Комментарий не должен превышать 5000 символов")
    private String comment;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    private List<String> attachments;

    @Positive(message = "Идентификатор студента должен быть положительным числом")
    @NotNull(message = "Идентификатор студента обязателен для заполнения")
    private Integer studentId;

    @Positive(message = "Идентификатор домашнего задания должен быть положительным числом")
    @NotNull(message = "Идентификатор домашнего задания обязателен для заполнения")
    private Integer homeworkId;

    @Min(value = 1, message = "Недопустимый статус. Минимальное значение: 1")
    @Max(value = 3, message = "Недопустимый статус. Максимальное значение: 3")
    private int statusId;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}

