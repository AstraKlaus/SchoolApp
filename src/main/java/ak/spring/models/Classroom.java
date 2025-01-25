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
    @ManyToOne
    @JoinColumn(name = "curriculum_id", referencedColumnName = "id")
    private Curriculum curriculum;

    @ToString.Exclude
    @Size(max = 50, message = "Максимальное количество студентов в классе — 50")
    @OneToMany(mappedBy = "classroom", orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<@Valid Person> persons = new ArrayList<>();
}

