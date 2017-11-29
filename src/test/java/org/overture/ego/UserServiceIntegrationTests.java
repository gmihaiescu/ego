package org.overture.ego;

import com.google.api.client.http.HttpStatusCodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overture.ego.model.dto.PageDTO;
import org.overture.ego.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTests {

  private final String URL_ROOT = "/users";

  @Autowired
  private TestRestTemplate restTemplate;

  private static HttpHeaders authHeaders() {
    HttpHeaders output = nonAuthHeaders();
    output.add("Authorization","asdf");

    return output;
  }

  private static HttpHeaders nonAuthHeaders() {
    HttpHeaders output = new HttpHeaders();
    output.add("Accept","application/json");

    return output;
  }

  /* ************
     GET /users
   ************ */
  @Test
  public void users_get_exists() throws Exception {
    ResponseEntity<PageDTO> response = this.restTemplate.exchange(URL_ROOT, HttpMethod.GET, new HttpEntity<>(nonAuthHeaders()), PageDTO.class);
    assertThat(
      response.getStatusCode().value() != HttpStatusCodes.STATUS_CODE_NOT_FOUND
    ).isTrue();
  }

  @Test
  public void users_get_requiresAuthHeader() throws Exception {
    ResponseEntity<String> nonAuthResponse = this.restTemplate.exchange(URL_ROOT, HttpMethod.GET, new HttpEntity<>(nonAuthHeaders()), String.class);
    ResponseEntity<String> authResponse = this.restTemplate.exchange(URL_ROOT, HttpMethod.GET, new HttpEntity<>(authHeaders()), String.class);
    assertThat(
      nonAuthResponse.getStatusCode().is4xxClientError()
    ).isTrue();
    assertThat(
      authResponse.getStatusCode().is2xxSuccessful()
    ).isTrue();
  }

  @Test
  public void users_get_responseIsJson() throws Exception {
    ResponseEntity<String> response = this.restTemplate.exchange(URL_ROOT, HttpMethod.GET, new HttpEntity<>(authHeaders()), String.class);

    // Body is valid JSON string
    assertThat(
      JSONUtils.isValidJSON(response.getBody())
    ).isTrue();

  }

  @Test
  public void users_get_limitParam_responseMatchLimitParam() throws Exception {
    final int limit = 10;

    String URL = UriComponentsBuilder.fromUriString(URL_ROOT)
      .queryParam("limit", limit)
      .build().encode().toUriString();

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(authHeaders()), PageDTO.class);
    assertThat(
      response.getBody().getLimit() == limit
    ).isTrue();
  }

  @Test
  public void users_get_pageParam_responseMatchPageParam() throws Exception {
    final int PAGE = 2;

    String URL = UriComponentsBuilder.fromUriString(URL_ROOT)
      .queryParam("page", PAGE)
      .build().encode().toUriString();

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(authHeaders()), PageDTO.class);
    assertThat(
      response.getBody().getPage() == PAGE
    ).isTrue();
  }

  @Test
  public void users_get_limitParam_responseContentSizeMatchesLimit() throws Exception {
    final int limit = 1;

    String URL = UriComponentsBuilder.fromUriString(URL_ROOT)
      .queryParam("limit", limit)
      .build().encode().toUriString();

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(authHeaders()), PageDTO.class);
    assertThat(
      response.getBody().getResultSet().size() == (int)Math.min(limit, response.getBody().getCount())
    ).isTrue();
  }

  @Test
  public void users_get_invalidParams_response4xx() {
    final String limit = "asdf";
    String URL = UriComponentsBuilder.fromUriString(URL_ROOT)
      .queryParam("limit", limit)
      .build().encode().toUriString();

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(URL, HttpMethod.GET, new HttpEntity<>(authHeaders()), PageDTO.class);
    assertThat(
      response.getStatusCode().is4xxClientError()
    ).isTrue();
  }

  // TODO: Sort Order and Sort Field checks
  // Can include an environment variable flag to let tests know if they can expect the Dummy Data scripts in the DB

  /* ****************
     GET /users/:id
   **************** */

//  public void users_id_get_


}
