package com.solo.learning.tdourado.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Model for the iTunes Artist JSON object's relevant attributes.
 *
 * @author tiberiusdourado
 */
@JsonIgnoreProperties(value = "artistUniqueId", ignoreUnknown = true)
@Entity
@NoArgsConstructor
public class Artist {
  @Id @GeneratedValue @Getter private Long artistUniqueId;
  @Getter private Long artistId;
  @Getter private String artistName;
  @Getter private String primaryGenreName;

  /**
   * Custom constructor to avoid setting the artistUniqueId attribute.
   *
   * @param artistId The Artist's iTunes ID.
   * @param artistName The name of the Artist on the iTunes database.
   * @param primaryGenreName The primary genre of the artist (according to iTunes).
   */
  @Generated
  public Artist(Long artistId, String artistName, String primaryGenreName) {
    this.artistId = artistId;
    this.artistName = artistName;
    this.primaryGenreName = primaryGenreName;
  }
}
