package br.com.acme.ditto.model.context.scaffold;

import lombok.NonNull;

public record ScaffoldContext(
    @NonNull CodeScaffoldContext codeScaffoldContext,
    @NonNull TestsScaffoldContext testsScaffoldContext
) {

}
