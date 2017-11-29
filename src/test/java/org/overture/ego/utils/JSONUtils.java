package org.overture.ego.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JSONUtils {
  private JSONUtils() {}

  final static ObjectMapper mapper = new ObjectMapper();

  public static boolean isValidJSON(String jsonString) {
    try {
      mapper.readTree(jsonString);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public static JsonNode parseJson(String jsonString) throws IOException {
    return mapper.readTree(jsonString);
  }
}
