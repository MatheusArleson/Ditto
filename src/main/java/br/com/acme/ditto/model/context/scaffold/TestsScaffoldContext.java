package br.com.acme.ditto.model.context.scaffold;

import br.com.acme.ditto.model.context.code.FileLocation;
import lombok.NonNull;

public record TestsScaffoldContext(
    @NonNull FileLocation controllerTestFileDestination,
    @NonNull FileLocation serviceImplementationTestFileDestination,
    @NonNull FileLocation pqlQueryBuilderTestFileDestination,
    @NonNull FileLocation pqlResultMapperTestFileDestination,
    @NonNull FileLocation commandAdapterTestFileDestination,
    @NonNull FileLocation resultAdapterTestFileDestination,
    @NonNull FileLocation requestValidatorTestFileDestination,
    @NonNull FileLocation testsDataFactoryFileDestination
) {

}
