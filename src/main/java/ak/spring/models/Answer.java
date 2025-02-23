package ak.spring.models;


import ak.spring.mappers.StringListToJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import jakarta.validation.constraints.*;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "answer")
@NoArgsConstructor
@AllArgsConstructor
public class Answer implements AttachableEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 5000, message = "Комментарий не должен превышать 5000 символов")
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;


    @Size(max = 5000, message = "Текст ответа должен содержать до 5000 символов")
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Size(max = 10, message = "Максимальное количество вложений — 10")
    @Convert(converter = StringListToJsonConverter.class)
    @Column(name = "attachments", columnDefinition = "TEXT")
    private List<String> attachments;

    @ToString.Exclude
    @NotNull(message = "Домашнее задание обязательно")
    @ManyToOne
    @JoinColumn(name = "homework_id", nullable = false)
    private Homework homework;

    @ToString.Exclude
    @NotNull(message = "Студент обязателен")
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private Person student;

    @NotNull(message = "Статус обязателен")
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Override
    public List<String> getAttachments() { return this.attachments; }

    @Override
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}

