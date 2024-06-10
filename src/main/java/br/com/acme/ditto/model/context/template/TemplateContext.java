package br.com.acme.ditto.model.context.template;

import lombok.NonNull;

public record TemplateContext(
    @NonNull CodeTemplateContext codeTemplateContext,
    @NonNull TestsTemplateContext testsTemplateContext
) {

}
