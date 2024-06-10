package br.com.acme.ditto.model.context;

import br.com.acme.ditto.model.context.io.CodeGenerationOutputContext;
import br.com.acme.ditto.model.context.io.InputContext;
import br.com.acme.ditto.model.context.io.TestsGenerationOutputContext;
import br.com.acme.ditto.model.context.scaffold.ScaffoldContext;
import br.com.acme.ditto.model.context.template.TemplateContext;
import lombok.NonNull;

public record DittoContext(
    @NonNull InputContext inputArgs,
    @NonNull TemplateContext templates,
    @NonNull CodeGenerationOutputContext codeOutput,
    @NonNull TestsGenerationOutputContext testsOutput,
    @NonNull ScaffoldContext scaffold
) {

}
