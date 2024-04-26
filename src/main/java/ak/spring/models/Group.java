package ak.spring.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

@Getter
@Setter
@Builder
@Entity
@Table(name = "Group")
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_teacher", referencedColumnName = "id")
    private Person teacher;

    @OneToMany(mappedBy = "group")
    private List<Person> persons;
}
