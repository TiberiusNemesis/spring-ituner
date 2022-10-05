package com.solo.learning.tdourado.persistence.repository;

import com.solo.learning.tdourado.persistence.domain.Album;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
  List<Album> getAlbumsByArtistId(Integer artistId);
}
