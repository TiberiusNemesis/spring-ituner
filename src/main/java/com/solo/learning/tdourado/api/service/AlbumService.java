package com.solo.learning.tdourado.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solo.learning.tdourado.api.model.AlbumResponse;
import com.solo.learning.tdourado.persistence.domain.Album;
import com.solo.learning.tdourado.persistence.domain.Artist;
import com.solo.learning.tdourado.persistence.repository.AlbumRepository;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AlbumService {

  private final AlbumRepository albumRepository;
  private final RestTemplate restTemplate;

  @Value("${lookup.url}")
  private String iTunesLookup;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public AlbumService(AlbumRepository albumRepository, RestTemplate restTemplate) {
    this.albumRepository = albumRepository;
    this.restTemplate = restTemplate;
  }

  public List<Album> getAllAlbumsFromParticularArtist(Artist artist) {
    return albumRepository.getAlbumsByArtistId(Math.toIntExact(artist.getArtistId()));
  }

  public void saveReceivedAlbums(Collection<Album> albumCollection) {
    albumRepository.saveAll(albumCollection);
  }

  /**
   * Makes a request to iTunes for all albums associated to an ArtistId.
   *
   * @param artistId A valid artist ID in the iTunes store.
   * @return An AlbumResponse object containing an Artist and a list of Albums associated to this
   *     artist.
   * @throws JsonProcessingException If there are any errors processing the JSON response from
   *     iTunes.
   */
  public AlbumResponse fetchAlbumsFromItunes(final @NotNull String artistId)
      throws JsonProcessingException, RestClientException {
    // The full lookup URL, constructed by inserting the chosen artist's ID on the iTunes address.
    final String fullLookupUrl = String.format(iTunesLookup, artistId);
    String jsonQueryResult = restTemplate.getForObject(fullLookupUrl, String.class);
    log.debug(
        "Query results for artist ID {}: {}",
        artistId,
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonQueryResult));

    // However, the first result of this lookup is an artist.
    AlbumResponse albumResponse = objectMapper.readValue(jsonQueryResult, AlbumResponse.class);
    Artist albumAuthor =
        objectMapper.readValue(
            objectMapper.writeValueAsString(albumResponse.getResults().get(0)), Artist.class);

    // Jackson doesn't really handle this, so it needs to be done manually.
    albumResponse.setArtist(albumAuthor);
    List<Album> artistLessAlbumList = albumResponse.getResults();
    artistLessAlbumList.remove(0);
    albumResponse.setResults(artistLessAlbumList);

    return albumResponse;
  }
}
