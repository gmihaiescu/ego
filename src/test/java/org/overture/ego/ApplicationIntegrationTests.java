package org.overture.ego;

import com.google.api.client.http.HttpStatusCodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overture.ego.model.dto.PageDTO;
import org.overture.ego.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void unknownResource_noAuth_responseUnauthoruzed() throws Exception {
    ResponseEntity<String> response = this.restTemplate.exchange("/thisisnota/real/resource", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertThat(
      response.getStatusCodeValue()
    ).isEqualTo(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
  }

  @Test
  public void unknownResource_auth_responseNotFound() throws Exception {
    ResponseEntity<String> response = this.restTemplate.exchange("/thisisnota/real/resource", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertThat(
      response.getStatusCodeValue()
    ).isEqualTo(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
  }

  @Test
  public void unknownResource_bodyIsJson() throws Exception {
    ResponseEntity<String> response = this.restTemplate.exchange("/thisisnota/real/resource", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertThat(
      JSONUtils.isValidJSON(response.getBody())
    ).isTrue();
  }

}
