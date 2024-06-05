package ak.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Curriculum")
@AllArgsConstructor
@NoArgsConstructor
public class Curriculum {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    private Boolean access;

    @OneToMany(mappedBy = "curriculum")
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "curriculum")
    private List<Classroom> Classroom = new ArrayList<>();

}