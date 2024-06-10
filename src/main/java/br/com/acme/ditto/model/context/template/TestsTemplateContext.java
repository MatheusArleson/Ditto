package br.com.acme.ditto.model.context.template;

import br.com.acme.ditto.model.context.code.FileLocation;
import lombok.NonNull;

public record TestsTemplateContext(
    @NonNull FileLocation controllerTestTemplateLocation,
    @NonNull FileLocation serviceImplementationTestTemplateLocation,
    @NonNull FileLocation pqlQueryBuilderTestTemplateLocation,
    @NonNull FileLocation pqlResultMapperTestTemplateLocation,
    @NonNull FileLocation commandAdapterTestTemplateLocation,
    @NonNull FileLocation resultAdapterTestTemplateLocation,
    @NonNull FileLocation requestValidatorTestTemplateLocation,
    @NonNull FileLocation testDataFactoryTemplateLocation
) {

}
