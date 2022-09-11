package com.solo.learning.tdourado.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for the iTunes Artist JSON object's relevant attributes.
 *
 * @author tiberiusdourado
 */
@JsonIgnoreProperties(value = "artistUniqueId", ignoreUnknown = true)
@Entity
@Data
@NoArgsConstructor
public class Artist {
  @Id @GeneratedValue private Long artistUniqueId;
  private Integer artistId;
  private String artistName;
  private String primaryGenreName;
}
