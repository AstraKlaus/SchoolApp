package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@ToString
@Table(name = "curriculum")
@AllArgsConstructor
@NoArgsConstructor
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название учебного плана не может быть пустым")
    @Size(min = 2, max = 150, message = "Название учебного плана должно содержать от 2 150 символов")
    @Column(name = "name", nullable = false, length = 150, unique = true)
    private String name;

    @Size(max = 5000, message = "Описание учебного плана не должно превышать 5000 символов")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Поле 'доступ' не может быть пустым")
    @Column(name = "access", nullable = false)
    private Boolean access;

    @ToString.Exclude
    @OneToMany(mappedBy = "curriculum", fetch = FetchType.LAZY)
    @Size(max = 50, message = "Максимальное количество курсов — 50")
    @Builder.Default
    private List<@Valid Course> courses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "curriculum", fetch = FetchType.LAZY)
    @Size(max = 30, message = "Максимальное количество классов — 30")
    @Builder.Default
    private List<@Valid Classroom> classrooms = new ArrayList<>();
}
