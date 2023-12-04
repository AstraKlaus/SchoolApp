package ak.spring.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Accord")
@NoArgsConstructor
@AllArgsConstructor
public class Accord {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "image", length = 1000)
    private byte[] image;

    @JsonIgnore
    @ManyToMany(mappedBy = "accords")
    private List<Song> songs;

}
