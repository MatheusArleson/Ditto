package br.com.acme.ditto.model.context.template;

import br.com.acme.ditto.model.context.code.FileLocation;
import lombok.NonNull;

public record CodeTemplateContext(
    @NonNull FileLocation controllerTemplateLocation,
    @NonNull FileLocation commandTemplateLocation,
    @NonNull FileLocation resultTemplateLocation,
    @NonNull FileLocation serviceInterfaceTemplateLocation,
    @NonNull FileLocation serviceImplementationTemplateLocation,
    @NonNull FileLocation pqlExchangeRequestTemplateLocation,
    @NonNull FileLocation pqlExchangeResponseTemplateLocation,
    @NonNull FileLocation pqlExchangeTemplateLocation,
    @NonNull FileLocation pqlQueryBuilderTemplateLocation,
    @NonNull FileLocation pqlResultMapperTemplateLocation,
    @NonNull FileLocation commandAdapterTemplateLocation,
    @NonNull FileLocation resultAdapterTemplateLocation,
    @NonNull FileLocation requestValidatorTemplateLocation
) {

}
