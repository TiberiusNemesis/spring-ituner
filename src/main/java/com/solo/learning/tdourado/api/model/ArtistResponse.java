package com.solo.learning.tdourado.api.model;

import com.solo.learning.tdourado.persistence.domain.Artist;
import java.util.List;
import lombok.Data;

/**
 * Model for the iTunes JSON response.
 *
 * @author tiberiusdourado
 */
@Data
public class ArtistResponse {
  Integer resultCount;
  List<Artist> results;
}
