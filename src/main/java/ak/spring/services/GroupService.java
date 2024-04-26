package ak.spring.services;

import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Course;
import ak.spring.models.Group;
import ak.spring.models.Person;
import ak.spring.repositories.GroupRepository;
import ak.spring.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, PersonRepository personRepository) {
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }

    public List<Group> findByName(String name) {
        return groupRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "name", name));
    }

    public Page<Group> findWithPagination(int offset, int pageSize){
        return groupRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Group findById(int id){
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
    }

    public Group updateGroup(int id, Group updatedGroup) {
        Group existingGroup = findById(id);
        existingGroup.setName(updatedGroup.getName());
        existingGroup.setTeacher(updatedGroup.getTeacher());
        return groupRepository.save(existingGroup);
    }


    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    public Group uploadGroup(Group group){
        Group newGroup = Group.builder()
                .name(group.getName())
                .teacher(group.getTeacher())
                .build();
        return groupRepository.save(newGroup);
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public Person getTeacher(Group group) {
        return group.getTeacher();
    }

    public List<Person> getStudents(Group group) {
        return group.getPersons();
    }
}
