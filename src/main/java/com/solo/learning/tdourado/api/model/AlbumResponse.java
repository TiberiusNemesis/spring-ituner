package com.solo.learning.tdourado.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.solo.learning.tdourado.persistence.domain.Album;
import com.solo.learning.tdourado.persistence.domain.Artist;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model for the iTunes JSON response.
 *
 * @author tiberiusdourado
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class AlbumResponse {
  @Getter @Setter Integer resultCount;
  Artist artist;
  List<Album> results;

  /**
   * Custom constructor to avoid exposing internal representation.
   *
   * @param resultCount The number of results from the JSON response.
   * @param artist The JSON Artist object.
   * @param results A list of JSON Album objects.
   */
  @Generated // Not really generated, but there's no point in testing this.
  public AlbumResponse(Integer resultCount, Artist artist, List<Album> results) {
    this.resultCount = resultCount;
    this.artist =
        new Artist(artist.getArtistId(), artist.getArtistName(), artist.getPrimaryGenreName());
    this.results = new ArrayList<>(results);
  }

  /**
   * Custom setter to avoid exposing internal representation.
   *
   * @param artist The JSON Artist object.
   */
  @Generated
  public void setArtist(Artist artist) {
    this.artist =
        new Artist(artist.getArtistId(), artist.getArtistName(), artist.getPrimaryGenreName());
  }

  /**
   * Custom getter to avoid exposing internal representation.
   *
   * @return An Artist object.
   */
  @Generated
  public Artist getArtist() {
    return new Artist(artist.getArtistId(), artist.getArtistName(), artist.getPrimaryGenreName());
  }

  /**
   * Custom setter to avoid exposing internal representation.
   *
   * @param results A list of JSON Album objects.
   */
  @Generated
  public void setResults(List<Album> results) {
    this.results = new ArrayList<>(results);
  }

  /**
   * Custom getter to avoid exposing internal representation.
   *
   * @return A list of JSON Album objects.
   */
  @Generated
  public List<Album> getResults() {
    return new ArrayList<>(results);
  }
}
