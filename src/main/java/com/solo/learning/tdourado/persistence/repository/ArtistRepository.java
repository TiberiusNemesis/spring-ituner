package com.solo.learning.tdourado.persistence.repository;

import com.solo.learning.tdourado.persistence.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {}
