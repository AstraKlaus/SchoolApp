package ak.spring.models;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Builder
@Table(name = "submission")
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "feedback")
    private String feedback;

    @Lob
    @Column(name = "file", length = 1000)
    private byte[] file;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Person student;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
