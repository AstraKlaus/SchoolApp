package ak.spring.services;

import ak.spring.models.Accord;
import ak.spring.models.Song;
import ak.spring.repositories.SongRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Song findByUuid(UUID uuid){ return songRepository.findByUuid(uuid).orElse(null);}

    public List<Song> findByName(String name){
        return songRepository.findByNameContainingIgnoreCase(name).orElse(null);
    }

    public Song uploadSong(Song song){
        return songRepository.save(song);
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

    public List<Accord> getAccords(int id) {
        return findById(id).getAccords();
    }
}
