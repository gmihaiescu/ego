package org.overture.ego;

import com.google.api.client.http.HttpStatusCodes;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overture.ego.model.dto.PageDTO;
import org.overture.ego.model.entity.User;
import org.overture.ego.model.enums.UserRole;
import org.overture.ego.model.enums.UserStatus;
import org.overture.ego.token.TokenService;
import org.overture.ego.token.TokenUserInfo;
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
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;


import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTests {

  private final String URL_ROOT = "/users";

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private SimpleDateFormat formatter;

  private String TOKEN_ADMIN_APPROVED;
  private String TOKEN_ADMIN_PENDING;
  private String TOKEN_USER_APPROVED;

  private String getAdminApprovedToken() {
    User user = createUser(UserRole.ADMIN, UserStatus.APPROVED);
    return this.getToken(user);
  }
  private String getAdminPendingToken() {
    User user = createUser(UserRole.ADMIN, UserStatus.PENDING);
    return this.getToken(user);
  }
  private String getUserToken() {
    User user = createUser(UserRole.USER, UserStatus.APPROVED);
    return this.getToken(user);
  }
  private String getToken(User user) {
    return tokenService.generateUserToken(new TokenUserInfo(user));
  }

  private User createUser(String role, String status) {
    User user = new User();
    user.setName("tester");
    user.setEmail("tester@example.com");
    user.setFirstName("test");
    user.setLastName("er");
    user.setStatus(status);
    user.setCreatedAt(formatter.format(new Date()));
    user.setLastLogin(null);
    user.setRole(role);

    return user;
  }

  private static HttpHeaders authHeaders(String token) {
    HttpHeaders output = nonAuthHeaders();
    output.add("Authorization","Bearer "+token);

    return output;
  }

  private static HttpHeaders nonAuthHeaders() {
    HttpHeaders output = new HttpHeaders();
    output.add("Accept","application/json");

    return output;
  }

  @Before
  public void beforeClass() {
    if(StringUtils.isEmpty(this.TOKEN_USER_APPROVED)) {
      TOKEN_USER_APPROVED = getUserToken();
    }
    if(StringUtils.isEmpty(this.TOKEN_ADMIN_PENDING)) {
      TOKEN_ADMIN_PENDING = getAdminPendingToken();
    }
    if(StringUtils.isEmpty(this.TOKEN_ADMIN_APPROVED)) {
      TOKEN_ADMIN_APPROVED = getAdminApprovedToken();
    }
    System.out.println(TOKEN_ADMIN_APPROVED);
  }

  /* ************
     GET /users
   ************ */
  @Test
  public void users_get_exists() throws Exception {
    ResponseEntity<PageDTO> response = this.restTemplate.exchange(URL_ROOT, HttpMethod.GET, new HttpEntity<>(nonAuthHeaders()), PageDTO.class);
    assertThat(
      response.getStatusCodeValue()
    ).isNotEqualTo(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
  }

  @Test
  public void users_get_authMissing_returnsUnauthorized() throws Exception {
    ResponseEntity<String> nonAuthResponse = this.restTemplate.exchange(
      URL_ROOT,
      HttpMethod.GET,
      new HttpEntity<>(nonAuthHeaders()),
      String.class);

    assertThat(
      nonAuthResponse.getStatusCodeValue()
    ).isEqualTo(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
  }

  @Test
  public void users_get_authUser_returnsUnauthorized() throws Exception {
    ResponseEntity<String> nonAuthResponse = this.restTemplate.exchange(
      URL_ROOT,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_USER_APPROVED)),
      String.class);

    assertThat(
      nonAuthResponse.getStatusCodeValue()
    ).isEqualTo(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
  }

  @Test
  public void users_get_authAdminPending_returnsUnauthorized() throws Exception {
    ResponseEntity<String> nonAuthResponse = this.restTemplate.exchange(
      URL_ROOT,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_PENDING)),
      String.class);

    assertThat(
      nonAuthResponse.getStatusCodeValue()
    ).isEqualTo(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
  }

  @Test
  public void users_get_authAdminApproved_returnsOK() throws Exception {
    ResponseEntity<String> nonAuthResponse = this.restTemplate.exchange(
      URL_ROOT,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_APPROVED)),
      String.class);

    assertThat(
      nonAuthResponse.getStatusCodeValue()
    ).isEqualTo(HttpStatusCodes.STATUS_CODE_OK);
  }

  @Test
  public void users_get_responseIsJson() throws Exception {
    ResponseEntity<String> response = this.restTemplate.exchange(
      URL_ROOT,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_APPROVED)),
      String.class);

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

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(
      URL,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_APPROVED)),
      PageDTO.class);

    assertThat(
      response.getBody().getLimit()
    ).isEqualTo(limit);
  }

  @Test
  public void users_get_pageParam_responseMatchPageParam() throws Exception {
    final int PAGE = 2;

    String URL = UriComponentsBuilder.fromUriString(URL_ROOT)
      .queryParam("page", PAGE)
      .build().encode().toUriString();

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(
      URL,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_APPROVED)),
      PageDTO.class);

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

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(
      URL,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_APPROVED)),
      PageDTO.class);

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

    ResponseEntity<PageDTO> response = this.restTemplate.exchange(
      URL,
      HttpMethod.GET,
      new HttpEntity<>(authHeaders(TOKEN_ADMIN_APPROVED)),
      PageDTO.class);

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
