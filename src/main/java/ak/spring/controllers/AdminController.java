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

}
