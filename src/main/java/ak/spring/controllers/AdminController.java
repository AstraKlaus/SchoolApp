package ak.spring.controllers;

import ak.spring.models.Song;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdminController {

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/admin")
    public void adminPanel(){}
}
