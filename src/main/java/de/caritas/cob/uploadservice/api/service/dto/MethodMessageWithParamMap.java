package de.caritas.cob.uploadservice.api.service.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class MethodMessageWithParamMap {

  private String msg = "method";

  private int id;

  private String method;

  private List<Map<String, String>> params;
}
