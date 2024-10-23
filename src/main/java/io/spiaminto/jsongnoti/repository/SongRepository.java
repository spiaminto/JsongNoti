package io.spiaminto.jsongnoti.repository;

import io.spiaminto.jsongnoti.domain.Brand;
import io.spiaminto.jsongnoti.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("select s from Song s where s.brand = :brand and s.createdAt > :localDateTime")
    List<Song> findSongsByBrandAndTimeAfter(Brand brand, LocalDateTime localDateTime);

}
