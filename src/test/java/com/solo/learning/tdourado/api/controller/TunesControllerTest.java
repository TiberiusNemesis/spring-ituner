package com.solo.learning.tdourado.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.solo.learning.tdourado.api.model.AlbumResponse;
import com.solo.learning.tdourado.api.model.ArtistResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Unit tests for TunesController using WebClient.
 *
 * <p>These tests mock the WebClient chain to verify controller behavior without making actual HTTP
 * calls to the iTunes API.
 */
@ExtendWith(MockitoExtension.class)
class TunesControllerTest {

  private TunesController tunesController;
  private WebClient.Builder mockWebClientBuilder;
  private WebClient mockWebClient;
  private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
  private WebClient.RequestHeadersSpec mockRequestHeadersSpec;
  private WebClient.ResponseSpec mockResponseSpec;

  @BeforeEach
  void setUp() {
    // Create mocks for WebClient chain
    mockWebClientBuilder = mock(WebClient.Builder.class);
    mockWebClient = mock(WebClient.class);
    mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    mockResponseSpec = mock(WebClient.ResponseSpec.class);

    // Configure mock builder
    when(mockWebClientBuilder.codecs(any())).thenReturn(mockWebClientBuilder);
    when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

    // Initialize controller with mocked WebClient
    tunesController = new TunesController(mockWebClientBuilder);

    // Set the URLs using reflection (normally injected by @Value)
    ReflectionTestUtils.setField(
        tunesController,
        "iTunesSearch",
        "https://itunes.apple.com/search?term=%s&entity=musicArtist&limit=5");
    ReflectionTestUtils.setField(
        tunesController, "iTunesLookup", "https://itunes.apple.com/lookup?id=%s&entity=album");
  }

  @Test
  void
      fetchAlbumsByArtistIdTest_whenValidArtistId_shouldReturnValidResponseEntityContainingOkStatus() {
    String validJsonResponse =
        """
                {
                 "resultCount":21,
                 "results": [
                {"wrapperType":"artist", "artistType":"Movie Artist", "artistName":"Aishwarya Rai Bachchan", "artistLinkUrl":"https://itunes.apple.com/us/artist/aishwarya-rai-bachchan/255286914?uo=4", "artistId":255286914, "primaryGenreName":"Bollywood", "primaryGenreId":4431},
                {"wrapperType":"collection", "collectionType":"Album", "artistId":3249567, "collectionId":1537961309, "amgArtistId":278580, "artistName":"A.R. Rahman", "collectionName":"Jodhaa Akbar (Original Motion Picture Soundtrack)", "collectionCensoredName":"Jodhaa Akbar (Original Motion Picture Soundtrack)", "artistViewUrl":"https://music.apple.com/us/artist/a-r-rahman/3249567?uo=4", "collectionViewUrl":"https://music.apple.com/us/album/jodhaa-akbar-original-motion-picture-soundtrack/1537961309?uo=4", "artworkUrl60":"https://is3-ssl.mzstatic.com/image/thumb/Music124/v4/2e/76/b5/2e76b58a-bc9f-89ad-a145-2da4683919de/886448872016.jpg/60x60bb.jpg", "artworkUrl100":"https://is3-ssl.mzstatic.com/image/thumb/Music124/v4/2e/76/b5/2e76b58a-bc9f-89ad-a145-2da4683919de/886448872016.jpg/100x100bb.jpg", "collectionPrice":7.99, "collectionExplicitness":"notExplicit", "trackCount":7, "copyright":"℗ 2007 Sony Music Entertainment India Pvt. Ltd. Under License From UTV", "country":"USA", "currency":"USD", "releaseDate":"2007-12-31T08:00:00Z", "primaryGenreName":"Bollywood"}]
                }
                """;

    // Configure mock chain for WebClient
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validJsonResponse));

    ResponseEntity<AlbumResponse> response = tunesController.fetchAlbumsByArtistId("255286914");

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("Aishwarya Rai Bachchan", response.getBody().getArtist().getArtistName());
    assertEquals(1, response.getBody().getResults().size()); // Artist removed from results
  }

  @Test
  void
      fetchAlbumsByArtistIdTest_whenInvalidArtistId_shouldReturnValidResponseEntityContainingBadRequestStatus() {
    // Configure mock to throw WebClientResponseException
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class))
        .thenReturn(
            Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

    ResponseEntity<AlbumResponse> badRequestEntity =
        tunesController.fetchAlbumsByArtistId("1831534");

    assertNotNull(badRequestEntity);
    assertEquals(400, badRequestEntity.getStatusCode().value());
  }

  @Test
  void
      fetchArtistsByNameTest_whenValidArtistName_shouldReturnValidResponseEntityContainingOkStatus() {
    String validJsonResponse =
        """
                {
                 "resultCount":3,
                 "results": [
                {"wrapperType":"artist", "artistType":"Artist", "artistName":"Daft Punk", "artistLinkUrl":"https://music.apple.com/us/artist/daft-punk/5468295?uo=4", "artistId":5468295, "amgArtistId":168791, "primaryGenreName":"Dance", "primaryGenreId":17},
                {"wrapperType":"artist", "artistType":"Artist", "artistName":"Daft Punk is Dead", "artistLinkUrl":"https://music.apple.com/us/artist/daft-punk-is-dead/1566602984?uo=4", "artistId":1566602984, "primaryGenreName":"House", "primaryGenreId":1048},
                {"wrapperType":"artist", "artistType":"Artist", "artistName":"Daft Punk Experience", "artistLinkUrl":"https://music.apple.com/us/artist/daft-punk-experience/1633597459?uo=4", "artistId":1633597459, "primaryGenreName":"Electronic", "primaryGenreId":7}]
                }
                """;

    // Configure mock chain for WebClient
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validJsonResponse));

    ResponseEntity<ArtistResponse> response = tunesController.fetchArtistsByName("Daft Punk");

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("Daft Punk", response.getBody().getResults().get(0).getArtistName());
    assertEquals(3, response.getBody().getResults().size());
  }

  @Test
  void
      fetchArtistsByNameTest_whenInvalidArtistName_shouldReturnValidResponseEntityContainingBadRequestStatus() {
    // Configure mock to throw WebClientResponseException
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class))
        .thenReturn(
            Mono.error(
                WebClientResponseException.create(400, "Bad Request", null, null, null)));

    ResponseEntity<ArtistResponse> badRequestEntity =
        tunesController.fetchArtistsByName("waaahhhhhhhhhhhhhhhhhh");

    assertNotNull(badRequestEntity);
    assertEquals(400, badRequestEntity.getStatusCode().value());
  }

  @Test
  void
      fetchAlbumsByArtistIdTest_whenEmptyResults_shouldReturnOkStatusWithEmptyAlbumList() {
    String emptyResultsJson =
        """
                {
                 "resultCount":0,
                 "results": []
                }
                """;

    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(emptyResultsJson));

    ResponseEntity<AlbumResponse> response = tunesController.fetchAlbumsByArtistId("999999999");

    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
  }

  @Test
  void fetchArtistsByNameTest_whenJsonProcessingError_shouldReturnBadRequest() {
    String invalidJson = "{ invalid json }";

    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidJson));

    ResponseEntity<ArtistResponse> response = tunesController.fetchArtistsByName("test");

    assertNotNull(response);
    assertEquals(400, response.getStatusCode().value());
  }

  @Test
  void fetchAlbumsByArtistIdTest_whenUnexpectedException_shouldReturnInternalServerError() {
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class))
        .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

    ResponseEntity<AlbumResponse> response = tunesController.fetchAlbumsByArtistId("123");

    assertNotNull(response);
    assertEquals(500, response.getStatusCode().value());
  }

  @Test
  void fetchArtistsByNameTest_whenUnexpectedException_shouldReturnInternalServerError() {
    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class))
        .thenReturn(Mono.error(new NullPointerException("Unexpected null")));

    ResponseEntity<ArtistResponse> response = tunesController.fetchArtistsByName("artist");

    assertNotNull(response);
    assertEquals(500, response.getStatusCode().value());
  }

  @Test
  void fetchAlbumsByArtistIdTest_whenJsonProcessingError_shouldReturnBadRequest() {
    String invalidAlbumJson = "{ bad: json }";

    when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
    when(mockRequestHeadersUriSpec.uri(anyString())).thenReturn(mockRequestHeadersSpec);
    when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    when(mockResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidAlbumJson));

    ResponseEntity<AlbumResponse> response = tunesController.fetchAlbumsByArtistId("123");

    assertNotNull(response);
    assertEquals(400, response.getStatusCode().value());
  }
}
