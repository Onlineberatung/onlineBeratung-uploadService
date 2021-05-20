package de.caritas.cob.uploadservice.api.authorization;

import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
  USER("user"),
  CONSULTANT("consultant"),
  TECHNICAL("technical"),
  U25_CONSULTANT("u25-consultant"),
  ANONYMOUS("anonymous");

  private final String value;

  public static Optional<UserRole> getRoleByValue(String value) {
    return Arrays.stream(values()).filter(userRole -> userRole.value.equals(value)).findFirst();
  }
}
