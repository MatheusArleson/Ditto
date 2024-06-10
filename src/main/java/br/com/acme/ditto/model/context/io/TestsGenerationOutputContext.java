package br.com.acme.ditto.model.context.io;

import br.com.acme.ditto.model.context.code.ClassContext;
import lombok.NonNull;

//TODO see how to pass the methods to be tested here
public record TestsGenerationOutputContext(
    @NonNull String rootPackageDotPath,
    @NonNull ClassContext controllerTest,
    @NonNull ClassContext serviceImplementationTest,
    @NonNull ClassContext pqlQueryBuilderTest,
    @NonNull ClassContext pqlResultMapperTest,
    @NonNull ClassContext commandAdapterTest,
    @NonNull ClassContext resultAdapterTest,
    @NonNull ClassContext requestValidatorTest,
    @NonNull ClassContext testsDataFactory,
    @NonNull CodeGenerationOutputContext codeOutput
) {

}
