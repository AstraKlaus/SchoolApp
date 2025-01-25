package ak.spring.models;

import ak.spring.mappers.StringListToJsonConverter;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@Table(name = "homework")
@AllArgsConstructor
@NoArgsConstructor
public class Homework implements AttachableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название домашнего задания не может быть пустым")
    @Size(min = 2, max = 150, message = "Название домашнего задания должно содержать от 2 до 150 символов")
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @Size(max = 5000, message = "Описание домашнего задания не должно превышать 5000 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    @Convert(converter = StringListToJsonConverter.class)
    @Column(name = "attachments", columnDefinition = "TEXT")
    private List<@Pattern(regexp = "^[\\w,\\s-]+\\.[A-Za-z]{3,4}$",
                    message = "Недопустимый формат имени файла. Допустимы латинские буквы, цифры, дефисы и расширения 3-4 символа")
                    String> attachments;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    @Column(name = "access", nullable = false)
    private Boolean access;

    @ToString.Exclude
    @NotNull(message = "Курс обязателен")
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ToString.Exclude
    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Size(max = 300, message = "Максимальное количество ответов — 300")
    @Builder.Default
    private List<@Valid Answer> answers = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public List<String> getAttachments() { return this.attachments; }

    @Override
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}


