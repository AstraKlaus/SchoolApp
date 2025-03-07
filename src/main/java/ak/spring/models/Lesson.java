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
@ToString
@Table(name = "lesson")
@NoArgsConstructor
@AllArgsConstructor
public class Lesson implements AttachableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название урока не может быть пустым")
    @Size(min = 2, max = 150, message = "Название урока должно содержать от 2 до 150 символов")
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @Size(max = 5000, message = "Содержимое урока должно содержать до 5000 символов")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    @Convert(converter = StringListToJsonConverter.class)
    @Column(name = "attachments", columnDefinition = "TEXT")
    private List<String> attachments;

    @Size(max = 5000, message = "Описание урока не должно превышать 5000 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    @Column(name = "access", nullable = false)
    private Boolean access;

    @ToString.Exclude
    @NotNull(message = "Курс обязателен")
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public List<String> getAttachments() { return this.attachments; }

    @Override
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}

