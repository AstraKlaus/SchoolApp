package ak.spring.services;

import ak.spring.controllers.SongRequest;
import ak.spring.models.Accord;
import ak.spring.models.Song;
import ak.spring.repositories.AuthorRepository;
import ak.spring.repositories.SongRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SongService {

    private final SongRepository songRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public SongService(SongRepository songRepository, AuthorRepository authorRepository) {
        this.songRepository = songRepository;
        this.authorRepository = authorRepository;
    }

    public List<Song> findAll() { return songRepository.findAll(); }

    public Page<Song> findWithPagination(int offset, int pageSize){
        return songRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Song findByUuid(UUID uuid){ return songRepository.findByUuid(uuid).orElse(null);}

    public List<Song> findByName(String name){
        return songRepository.findByNameContainingIgnoreCase(name).orElse(null);
    }

    public Song uploadSong(SongRequest song){
        Song newSong = Song.builder()
                .text(song.getText())
                .name(song.getName())
                .uuid(UUID.randomUUID())
                .accords(song.getAccords())
                .author(song.getAuthor())
                .build();
        authorRepository.save(song.getAuthor());
        return songRepository.save(newSong);
    }

    public Song findById(int id){
        return songRepository.findById(id).orElse(null);
    }

    public void deleteSong(Song song){
        songRepository.delete(song);
    }

    public Song updateSong(int id, SongRequest song){
        Song pastSong = findById(id);

        Song newSong = Song.builder()
                .id(pastSong.getId())
                .text(song.getText())
                .name(song.getName())
                .uuid(pastSong.getUuid())
                .accords(song.getAccords())
                .author(song.getAuthor())
                .build();

        return songRepository.save(newSong);
    }

    public List<Accord> getAccords(UUID id) {
        return findByUuid(id).getAccords();
    }
}
