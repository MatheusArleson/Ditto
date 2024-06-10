package br.com.acme.ditto.model.context.io;

import br.com.acme.ditto.model.context.code.ClassContext;
import br.com.acme.ditto.model.context.code.ControllerInterfaceMethodContext;
import br.com.acme.ditto.model.context.code.FileLocation;
import java.util.List;
import lombok.NonNull;

public record InputContext(
    @NonNull FileLocation controllerFileLocation,
    @NonNull ClassContext controllerInterface,
    @NonNull List<ControllerInterfaceMethodContext> controllerInterfaceMethods
) {

}
