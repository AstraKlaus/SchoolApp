package ak.spring.services;

import ak.spring.controllers.SongRequest;
import ak.spring.models.Accord;
import ak.spring.models.Author;
import ak.spring.models.Person;
import ak.spring.models.Song;
import ak.spring.repositories.SongRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<Song> findAll() { return songRepository.findAll(); }

    public Page<Song> findWithPagination(int offset, int pageSize){
        return songRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Song findByUuid(UUID uuid){ return songRepository.findByUuid(uuid).orElse(null);}

    public List<Song> findByName(String name){
        return songRepository.findByNameContainingIgnoreCase(name).orElse(null);
    }

    public Song uploadSong(SongRequest song, Author author, List<Accord> accords){
        Song newSong = Song.builder()
                .text(song.getText())
                .name(song.getName())
                .uuid(UUID.randomUUID())
                .accords(accords)
                .author(author)
                .build();
        return songRepository.save(newSong);
    }

    public Song findById(int id){
        return songRepository.findById(id).orElse(null);
    }

    public void deleteSong(Song song){
        songRepository.delete(song);
    }

    public void updateSong(int id, Song song){
        Song pastSong = findById(id);

        song.setId(id);
        song.setAuthor(pastSong.getAuthor());
        song.setAccords(pastSong.getAccords());

        songRepository.save(song);
    }

    public List<Accord> getAccords(UUID id) {
        return findByUuid(id).getAccords();
    }
}
