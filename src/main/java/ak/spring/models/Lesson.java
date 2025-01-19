package ak.spring.models;


import ak.spring.mappers.StringListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "lesson")
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "Идентификатор урока должен быть положительным числом")
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название урока не может быть пустым")
    @Size(min = 5, max = 150, message = "Название урока должно содержать от 5 до 150 символов")
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @NotBlank(message = "Содержимое урока не может быть пустым")
    @Size(min = 10, max = 5000, message = "Содержимое урока должно содержать от 10 до 5000 символов")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    @Convert(converter = StringListToJsonConverter.class)
    @Column(name = "attachments", columnDefinition = "TEXT")
    private List<@Pattern(regexp = "^[\\w,\\s-]+\\.[A-Za-z]{1,9}$",
                    message = "Недопустимый формат имени файла. Допустимы латинские буквы, цифры, дефисы и расширения 3-4 символа")
                    String> attachments;

    @Size(max = 500, message = "Описание урока не должно превышать 500 символов")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    @Column(name = "access", nullable = false)
    private Boolean access;

    @NotNull(message = "Курс обязателен")
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;

    @NotNull(message = "Дата создания не может быть пустой")
    @PastOrPresent(message = "Дата создания не может быть в будущем")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @PastOrPresent(message = "Дата обновления не может быть в будущем")
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}

