package ak.spring.controllers;

import ak.spring.models.Group;
import ak.spring.models.Person;
import ak.spring.services.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins =  "http://localhost:8080")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Group>> findByName(@PathVariable String name) {
        List<Group> groups = groupService.findByName(name);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Group>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<Group> groups = groupService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> findById(@PathVariable int id) {
        Group group = groupService.findById(id);
        return ResponseEntity.ok(group);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable int id, @Valid @RequestBody Group updatedGroup) {
        Group group = groupService.updateGroup(id, updatedGroup);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable int id) {
        Group group = groupService.findById(id);
        groupService.deleteGroup(group);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Group> uploadGroup(@Valid @RequestBody Group group) {
        Group savedGroup = groupService.uploadGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGroup);
    }

    @GetMapping
    public ResponseEntity<List<Group>> findAll() {
        List<Group> groups = groupService.findAll();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}/teacher")
    public ResponseEntity<Person> getTeacher(@PathVariable int id) {
        Group group = groupService.findById(id);
        Person teacher = groupService.getTeacher(group);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Person>> getStudents(@PathVariable int id) {
        Group group = groupService.findById(id);
        List<Person> students = groupService.getStudents(group);
        return ResponseEntity.ok(students);
    }
}