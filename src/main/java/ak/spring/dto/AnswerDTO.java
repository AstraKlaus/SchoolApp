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

    @Size(min = 2, max = 500, message = "Текст ответа должен содержать от 2 до 500 символов")
    private String text;

    @Size(max = 300, message = "Комментарий не должен превышать 300 символов")
    private String comment;

    @Size(max = 5, message = "Максимальное количество вложений — 5")
    private List<String> attachments;

    @Positive(message = "Идентификатор студента должен быть положительным числом")
    @NotNull(message = "Идентификатор студента обязателен для заполнения")
    private Integer studentId;

    @Positive(message = "Идентификатор домашнего задания должен быть положительным числом")
    @NotNull(message = "Идентификатор домашнего задания обязателен для заполнения")
    private Integer homeworkId;

    @Min(value = 0, message = "Недопустимый статус. Минимальное значение: 0")
    @Max(value = 5, message = "Недопустимый статус. Максимальное значение: 5")
    private int statusId;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}

