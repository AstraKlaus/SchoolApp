package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Getter
@Setter
@Builder
@Entity
@ToString
@Table(name = "classroom")
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @NotBlank(message = "Название класса не может быть пустым")
    @Size(min = 2, max = 100, message = "Название класса должно содержать от 2 до 100 символов")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @ToString.Exclude
    @ManyToMany(mappedBy = "classrooms")
    @Builder.Default
    private List<Curriculum> curricula = new ArrayList<>();

    @ToString.Exclude
    @Size(max = 50, message = "Максимальное количество студентов в классе — 50")
    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    @Builder.Default
    private List<@Valid Person> persons = new ArrayList<>();
}

