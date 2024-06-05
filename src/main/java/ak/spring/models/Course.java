package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Course")
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "access")
    private boolean access;

    @OneToMany(mappedBy = "course")
    private List<Lesson> lessons = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "curriculum_id", referencedColumnName = "id")
    private Curriculum curriculum;

    @ManyToMany
    @JoinTable(
            name = "person_course",
            joinColumns = @JoinColumn(name = "id_person"),
            inverseJoinColumns = @JoinColumn(name = "id_course"))
    private List<Person> students;

}
