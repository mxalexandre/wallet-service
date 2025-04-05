package com.recargapay.walletservice.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.owasp.encoder.Encode;

public class StringSanitizerDeserializer extends StdDeserializer<String> {

  public StringSanitizerDeserializer() {
    this(null);
  }

  public StringSanitizerDeserializer(Class<String> t) { super(t); }

  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String original = p.getValueAsString();
    if (original == null) {
      return null;
    }

    String sanitized = sanitize(original);

    sanitized = sanitized.replaceAll("<.*?>", "");

    sanitized = Encode.forHtmlContent(sanitized);

    return preventSqlInjection(sanitized);
  }

  private String sanitize(String input) {
    if (input == null) {
      return null;
    }

    return input
        .trim()                      // Remove leading/trailing spaces
        .replaceAll("\\s+", " ");    // Replace multiple internal spaces with a single space
  }

  private String preventSqlInjection(String input) {
    if (input == null) {
      return null;
    }

    return input
        .replaceAll("(['\";])+","")  // Remove single quotes, double quotes, semicolons
        .replaceAll("--", "")        // Remove double hyphens (SQL comment start)
        .replaceAll("\\b(SELECT|INSERT|DELETE|UPDATE|DROP|ALTER|CREATE|TRUNCATE)\\b", "") // Remove SQL keywords
        .trim();
  }
}
