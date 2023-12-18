package ak.spring.services;

import ak.spring.models.Accord;
import ak.spring.models.Author;
import ak.spring.models.Song;
import ak.spring.repositories.AccordRepository;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AccordService {

    private final AccordRepository accordRepository;

    @Autowired
    public AccordService(AccordRepository accordRepository) {
        this.accordRepository = accordRepository;
    }

    public Accord uploadAccord(MultipartFile file) throws IOException {
        Accord newAccord = Accord.builder()
                .uuid(UUID.randomUUID())
                .name(file.getOriginalFilename())
                .image(file.getBytes()).build();
        return accordRepository.save(newAccord);
    }

    public Accord updateAccord(int id,String name, byte[] file) throws IOException {
        Accord accord = findById(id);
        if (accord != null) {
            accord.setName(name);
            accord.setImage(file);
            return accordRepository.save(accord);
        }
        return null;
    }

    public Page<Accord> findWithPagination(int offset, int pageSize){
        return accordRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Accord findById(int id){
        return accordRepository.findById(id).orElse(null);
    }

    public Accord findByUuid(UUID uuid){
        return accordRepository.findByUuid(uuid).orElse(null);
    }

    public byte[] downloadAccord(String name){
        Optional<Accord> accord = accordRepository.findByNameContainingIgnoreCase(name);
        if (accord.isPresent()) { return accord.get().getImage(); }
        else { return new byte[0]; }
    }

    public Accord findByName(String name){
        Optional<Accord> accord = accordRepository.findByNameContainingIgnoreCase(name);
        return accord.orElse(null);
    }

    public void save(Accord accord) {
        accordRepository.save(accord);
    }

    public List<Accord> findAll() {
        return accordRepository.findAll();
    }

    public void deleteAccord(Accord accord) {
        accordRepository.delete(accord);
    }
}
