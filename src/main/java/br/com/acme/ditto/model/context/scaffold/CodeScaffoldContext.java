package br.com.acme.ditto.model.context.scaffold;

import br.com.acme.ditto.model.context.code.FileLocation;
import lombok.NonNull;

public record CodeScaffoldContext(
    @NonNull FileLocation controllerFileDestination,
    @NonNull FileLocation commandFileDestination,
    @NonNull FileLocation resultFileDestination,
    @NonNull FileLocation serviceInterfaceFileDestination,
    @NonNull FileLocation serviceImplementationFileDestination,
    @NonNull FileLocation pqlExchangeRequestFileDestination,
    @NonNull FileLocation pqlExchangeResponseFileDestination,
    @NonNull FileLocation pqlExchangeFileDestination,
    @NonNull FileLocation pqlQueryBuilderFileDestination,
    @NonNull FileLocation pqlResultMapperFileDestination,
    @NonNull FileLocation commandAdapterFileDestination,
    @NonNull FileLocation resultAdapterFileDestination,
    @NonNull FileLocation requestValidatorFileDestination
) {

}
