package com.solo.learning.tdourado.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.solo.learning.tdourado.persistence.domain.Album;
import com.solo.learning.tdourado.persistence.domain.Artist;
import java.util.List;
import lombok.Data;

/**
 * Model for the iTunes JSON response.
 *
 * @author tiberiusdourado
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumResponse {
  Integer resultCount;
  Artist artist;
  List<Album> results;
}
