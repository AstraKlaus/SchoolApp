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
public class HomeworkDTO {

    @Positive(message = "Идентификатор домашнего задания должен быть положительным числом")
    private int id;

    @NotBlank(message = "Название домашнего задания не может быть пустым")
    @Size(min = 5, max = 150, message = "Название домашнего задания должно содержать от 5 до 150 символов")
    private String name;

    @Size(max = 1000, message = "Описание домашнего задания не должно превышать 1000 символов")
    private String description;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    private List<@Pattern(regexp = "^[\\w,\\s-]+\\.[A-Za-z]{3,4}$", message = "Недопустимый формат имени файла") String> attachments;

    @NotNull(message = "Дата создания не может быть пустой")
    @PastOrPresent(message = "Дата создания не может быть в будущем")
    private Timestamp createdAt;

    @PastOrPresent(message = "Дата обновления не может быть в будущем")
    private Timestamp updatedAt;
}

