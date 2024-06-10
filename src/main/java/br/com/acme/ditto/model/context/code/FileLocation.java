package br.com.acme.ditto.model.context.code;

import lombok.NonNull;

public record FileLocation(
    @NonNull String fileParentAbsolutePath,
    @NonNull String fileName
) {

}
