package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "classroom")
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "curriculum_id", referencedColumnName = "id")
    private Curriculum curriculum;

    @ManyToOne
    @JoinColumn(name = "id_teacher", referencedColumnName = "id")
    private Person teacher;

    @OneToMany(mappedBy = "classroom")
    private List<Person> persons = new ArrayList<>();
}
