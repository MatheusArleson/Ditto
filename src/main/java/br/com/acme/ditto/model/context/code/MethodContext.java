package br.com.acme.ditto.model.context.code;

import lombok.NonNull;

public record MethodContext(
    @NonNull String name,
    @NonNull ClassContext inputType,
    @NonNull ClassContext outputType
) {

}
