package de.caritas.cob.uploadservice.api.controller;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;

@Controller
@ApiIgnore
@RequestMapping(value = "${springfox.docuPath}")
public class CustomSwaggerUiPathController extends Swagger2Controller {

  public CustomSwaggerUiPathController(
      Environment environment,
      DocumentationCache documentationCache,
      ServiceModelToSwagger2Mapper mapper,
      JsonSerializer jsonSerializer) {
    super(environment, documentationCache, mapper, jsonSerializer);
  }
}
