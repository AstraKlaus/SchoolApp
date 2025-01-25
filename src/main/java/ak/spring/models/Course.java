package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название курса не может быть пустым")
    @Size(min = 2, max = 150, message = "Название курса должно содержать от 2 до 150 символов")
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @Size(max = 5000, message = "Описание курса не должно превышать 5000 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    @Column(name = "access", nullable = false)
    private Boolean access;

    @ToString.Exclude
    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.LAZY)
    @Size(max = 50, message = "Максимальное количество уроков — 50")
    @Builder.Default
    private List<@Valid Lesson> lessons = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.LAZY)
    @Size(max = 100, message = "Максимальное количество домашних заданий — 100")
    @Builder.Default
    private List<@Valid Homework> homeworks = new ArrayList<>();

    @ToString.Exclude
    @NotNull(message = "Учебный план обязателен")
    @ManyToOne
    @JoinColumn(name = "curriculum_id", referencedColumnName = "id", nullable = false)
    private Curriculum curriculum;
}

