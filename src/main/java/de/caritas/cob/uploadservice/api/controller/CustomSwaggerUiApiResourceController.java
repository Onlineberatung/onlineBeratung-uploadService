package de.caritas.cob.uploadservice.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Controller
@ApiIgnore
@RequestMapping(value = "${springfox.docuPath}" + "/swagger-resources")
public class CustomSwaggerUiApiResourceController extends ApiResourceController {

  public CustomSwaggerUiApiResourceController(SwaggerResourcesProvider swaggerResources) {
    super(swaggerResources);
  }
}
