package de.caritas.cob.uploadservice.api.service.dto;

import lombok.Data;

@Data
public class StringifiedMessageResponse {

  private String message;

  private Boolean success;
}
