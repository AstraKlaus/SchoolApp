package ak.spring.controllers;

import ak.spring.models.Person;
import ak.spring.repositories.PersonRepository;
import ak.spring.services.AdminService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins =  "http://localhost:8080")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/admin")
    public Person createPerson(Person person){
        return adminService.uploadPerson(person);
    }

    @PatchMapping("/admin/{id}")
    public void updatePerson(@PathVariable("id") String id, Person person){
        adminService.updatePerson(Integer.parseInt(id), person);
    }

    @DeleteMapping("/admin/{id}")
    public void deletePerson(Person person){
        adminService.deletePerson(person);
    }

    @GetMapping("/admin/{id}")
    public Person getPerson(@PathVariable("id") String id){
        return adminService.findById(Integer.parseInt(id));
    }

    @GetMapping("/admin")
    public List<Person> getPeople(){
        return adminService.findAll();
    }
}
