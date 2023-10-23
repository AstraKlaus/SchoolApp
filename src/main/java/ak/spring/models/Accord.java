package ak.spring.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "image", length = 1000)
    private byte[] image;

    @JsonIgnore
    @ManyToMany(mappedBy = "accords")
    private List<Song> songs;

}
