package com.solo.learning.tdourado.api.model;

import com.solo.learning.tdourado.persistence.domain.Artist;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model for the iTunes JSON response.
 *
 * @author tiberiusdourado
 */
@NoArgsConstructor
public class ArtistResponse {
  @Getter @Setter Integer resultCount;
  List<Artist> results;

  /**
   * Custom constructor to avoid exposing internal representation.
   *
   * @param resultCount The number of results from the JSON response.
   * @param results A list of JSON Artist objects.
   */
  public ArtistResponse(Integer resultCount, List<Artist> results) {
    this.resultCount = resultCount;
    this.results = new ArrayList<>(results);
  }

  /**
   * Custom setter to avoid exposing internal representation.
   *
   * @param results A list of JSON Artist objects.
   */
  public void setResults(List<Artist> results) {
    this.results = new ArrayList<>(results);
  }

  /**
   * Custom getter to avoid exposing internal representation.
   *
   * @return A list of JSON Artist objects.
   */
  public List<Artist> getResults() {
    return new ArrayList<>(results);
  }
}
