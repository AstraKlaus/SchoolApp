package ak.spring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

import java.sql.Timestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "Идентификатор записи должен быть положительным числом")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull(message = "Студент обязателен")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Person person;

    @NotNull(message = "Курс обязателен")
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Дата создания не может быть пустой")
    @PastOrPresent(message = "Дата создания не может быть в будущем")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}

