package br.com.acme.ditto.model.context.code;

import lombok.NonNull;

public record ControllerInterfaceMethodContext(
    @NonNull String name,
    @NonNull ClassContext inputType,
    @NonNull ClassContext outputType,
    @NonNull AnnotationContext requestMappingAnnotation
) {

  public @NonNull String getRequestMappingUrl(){
    return requestMappingAnnotation.params().get("value");
  }

}
