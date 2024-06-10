package br.com.acme.ditto.model.context.code;

import java.util.Map;
import lombok.NonNull;

public record AnnotationContext(
    @NonNull String packageName,
    @NonNull String className,
    @NonNull Map<String, String> params
) {

}
