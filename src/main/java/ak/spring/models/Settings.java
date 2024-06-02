package ak.spring.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne
    @JoinColumn(name = "font_size_id")
    private FontSize fontSize;

    @Column(nullable = false)
    private Boolean isSerif;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Person person;
}

