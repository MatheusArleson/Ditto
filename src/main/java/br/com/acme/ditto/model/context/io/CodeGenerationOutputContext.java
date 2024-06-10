package br.com.acme.ditto.model.context.io;

import br.com.acme.ditto.model.context.code.ClassContext;
import br.com.acme.ditto.model.context.code.ControllerInterfaceMethodContext;
import br.com.acme.ditto.model.context.code.MethodContext;
import lombok.NonNull;

public record CodeGenerationOutputContext(
    @NonNull String rootPackageDotPath,
    @NonNull ClassContext controllerInterface,
    @NonNull ControllerInterfaceMethodContext controllerInterfaceMethod,
    @NonNull ClassContext controller,
    @NonNull ClassContext command,
    @NonNull ClassContext result,
    @NonNull ClassContext serviceInterface,
    @NonNull ClassContext serviceImplementation,
    //NOTE this will have to become a collection later
    @NonNull MethodContext serviceMethod,
    @NonNull ClassContext pqlExchangeRequest,
    @NonNull ClassContext pqlExchangeResponse,
    @NonNull ClassContext pqlExchange,
    @NonNull ClassContext pqlQueryBuilder,
    @NonNull ClassContext pqlResultMapper,
    @NonNull ClassContext commandAdapter,
    @NonNull ClassContext resultAdapter,
    @NonNull ClassContext requestValidator
) {

}
