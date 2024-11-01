package ak.spring.models;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "homework")
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    private String attachment;

    private boolean access;

    @ManyToOne()
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "homework", cascade = CascadeType.ALL)
    private List<Answer> answers;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}

