package ak.spring.controllers;

import ak.spring.auth.AuthenticationResponse;
import ak.spring.auth.AuthenticationService;
import ak.spring.auth.RegisterRequest;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.services.ExcelService;
import ak.spring.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("v1/api/admin")
public class AdminController {

    private final PersonService personService;
    private final ExcelService excelService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AdminController(PersonService personService, ExcelService excelService, AuthenticationService authenticationService) {
        this.personService = personService;
        this.excelService = excelService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<PersonDTO>> getAllUsers() {
        List<PersonDTO> users = personService.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<PersonDTO> createUser(@Valid @RequestBody Person person) {
        PersonDTO savedPerson = personService.uploadPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<PersonDTO> updateUser(@PathVariable int id, @Valid @RequestBody PersonDTO updatedPerson) {
        PersonDTO person = personService.update(id, updatedPerson);
        return ResponseEntity.ok(person);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable int userId, @RequestBody Role role) {
        personService.assignRoleToUser(userId, role);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/users/import")
    public ResponseEntity<String> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл пуст или не предоставлен");
        }

        try {
            // Читаем пользователей из Excel
            List<AuthenticationResponse> users = authenticationService.registerUsersFromExcel(file.getInputStream());
            return ResponseEntity.ok(String.format("Пользователи успешно импортированы %s", users.toString()));
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при чтении файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Некорректный формат данных в файле: " + e.getMessage());
        }
    }


    @GetMapping("/users/export")
    public ResponseEntity<Resource> exportUsersToExcel() {
        try {
            // Получаем список пользователей
            List<PersonDTO> users = personService.findAll();

            // Генерируем Excel-файл
            ByteArrayInputStream inputStream = excelService.exportUsersToExcel(users);

            // Настраиваем HTTP-ответ для скачивания файла
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e) {
            // Обрабатываем исключения (в данном случае, возвращаем 500)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}