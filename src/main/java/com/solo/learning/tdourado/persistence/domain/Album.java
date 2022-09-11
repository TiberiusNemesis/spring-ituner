package com.solo.learning.tdourado.persistence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Model for the iTunes Album JSON object's relevant attributes.
 *
 * @author tiberiusdourado
 */
@Getter
@Entity
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {
  // IDs
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long albumId;

  private Integer artistId;
  private Integer collectionId;

  // Names
  private String artistName;
  private String collectionName;

  // Additional information
  private Double collectionPrice;
  private String currency;
  private String primaryGenreName;
  private String copyright;
}
