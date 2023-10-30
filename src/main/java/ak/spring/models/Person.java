package ak.spring.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    @Column(name = "full_name")
    private String fullName;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    @Column(name = "username")
    private String username;

    @Column(name = "email")
    @Email
    private String email;

    @Min(value = 1900, message = "Год рождения должен быть больше, чем 1900")
    @Max(value = 2020, message = "Год рождения должен быть меньше, чем 2020")
    @Column(name = "year_of_birth")
    private int yearOfBirth;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @ManyToMany
    @JoinTable(
            name = "song_person",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs;


    public Person() {}

    public Person(String fullName, int yearOfBirth, String email) {
        this.fullName = fullName;
        this.yearOfBirth = yearOfBirth;
        this.email = email;
    }

}
