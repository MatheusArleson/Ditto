package br.com.acme.ditto.service;

import br.com.acme.ditto.model.context.DittoContext;
import br.com.acme.ditto.model.context.code.ClassContext;
import br.com.acme.ditto.model.context.code.FileLocation;
import br.com.acme.ditto.model.context.code.MethodContext;
import br.com.acme.ditto.model.context.io.CodeGenerationOutputContext;
import br.com.acme.ditto.model.context.io.InputContext;
import br.com.acme.ditto.model.context.io.TestsGenerationOutputContext;
import br.com.acme.ditto.model.context.scaffold.CodeScaffoldContext;
import br.com.acme.ditto.model.context.scaffold.ScaffoldContext;
import br.com.acme.ditto.model.context.scaffold.TestsScaffoldContext;
import br.com.acme.ditto.model.context.template.CodeTemplateContext;
import br.com.acme.ditto.model.context.template.TemplateContext;
import br.com.acme.ditto.model.context.template.TestsTemplateContext;
import java.io.File;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DittoContextFactory {

  private static final String JAVA_FILE_EXTENSION = ".java";

  //TODO externalize this suffixes
  private static final String CONTROLLER_CLASS_SUFFIX = "Controller";
  private static final String COMMAND_CLASS_NAME_SUFFIX = "Command";
  private static final String COMMAND_ADAPTER_CLASS_NAME_SUFFIX = "CommandAdapter";
  private static final String RESULT_CLASS_NAME_SUFFIX = "Result";
  private static final String RESULT_ADAPTER_CLASS_NAME_SUFFIX = "ResultAdapter";
  private static final String REQUEST_VALIDATOR_CLASS_NAME_SUFFIX = "Validator";
  private static final String SERVICE_INTERFACE_CLASS_NAME_SUFFIX = "Service";
  private static final String SERVICE_IMPLEMENTATION_CLASS_NAME_SUFFIX = "ServiceImpl";
  private static final String PQL_EXCHANGE_REQUEST_CLASS_NAME_SUFFIX = "PqlExchangeRequest";
  private static final String PQL_EXCHANGE_RESPONSE_CLASS_NAME_SUFFIX = "PqlExchangeResponse";
  private static final String PQL_EXCHANGE_CLASS_NAME_SUFFIX = "PqlExchange";
  private static final String PQL_QUERY_BUILDER_CLASS_NAME_SUFFIX = "PqlQueryBuilder";
  private static final String PQL_RESULT_MAPPER_CLASS_NAME_SUFFIX = "PqlResultMapper";

  private static final String TEST_CLASS_NAME_SUFFIX = "Test";
  private static final String TEST_DATA_FACTORY_CLASS_NAME_SUFFIX = "TestDataFactory";

  private static final String TEMPLATE_FOLDER_CODE = "code";
  private static final String TEMPLATE_FOLDER_TESTS = "tests";

  public static @NonNull DittoContext getDittoCtx(
      @NonNull String templatesRootFolderAbsolutePath,
      @NonNull String controllerInterfaceAbsolutePath,
      @NonNull String codeOutputRootFolderAbsolutePath,
      @NonNull String testOutputRootFolderAbsolutePath,
      @NonNull String targetPkgDotPath
  ) throws IOException {
    File controllerInterfaceFile = new File(controllerInterfaceAbsolutePath);
    String controllerInterfaceClassName = controllerInterfaceFile.getName();

    File controllerInterfaceParentFolder = controllerInterfaceFile.getParentFile();
    String controllerInterfaceParentFolderAbsolutePath = controllerInterfaceParentFolder.getAbsolutePath();

    var inputCtx = getInputArgumentsContext(
        controllerInterfaceParentFolderAbsolutePath,
        controllerInterfaceClassName
    );

    var codeOutputCtx = getCodeOutputContext(inputCtx, targetPkgDotPath);
    var testsOutputCtx = getTestsOutputContext(codeOutputCtx, targetPkgDotPath);

    var scaffoldCtx = getScaffoldContext(
        codeOutputRootFolderAbsolutePath, codeOutputCtx,
        testOutputRootFolderAbsolutePath, testsOutputCtx
    );

    var templateCtx = getTemplateContext(templatesRootFolderAbsolutePath);

    return new DittoContext(
        inputCtx, templateCtx,
        codeOutputCtx, testsOutputCtx,
        scaffoldCtx
    );
  }

  private static @NonNull InputContext getInputArgumentsContext(
      @NonNull String controllerRootFolderPath,
      @NonNull String controllerFileName
  ) throws IOException {
    return JavaClassParserService.extractControllerInterfaceInfo(
        controllerRootFolderPath + File.separator + controllerFileName
    );
  }

  private static @NonNull ScaffoldContext getScaffoldContext(
      @NonNull String codeOutputFolderRootAbsolutePath,
      @NonNull CodeGenerationOutputContext codeGenerationOutputContext,
      @NonNull String testOutputFolderRootAbsolutePath,
      @NonNull TestsGenerationOutputContext testsGenerationOutputContext
  ) {
    var codeScaffoldCtx = getCodeScaffoldContext(
        codeOutputFolderRootAbsolutePath,
        codeGenerationOutputContext
    );

    var testScaffoldCtx = getTestsScaffoldContext(
        testOutputFolderRootAbsolutePath,
        testsGenerationOutputContext
    );

    return new ScaffoldContext(
        codeScaffoldCtx,
        testScaffoldCtx
    );
  }

  private static @NonNull CodeScaffoldContext getCodeScaffoldContext(
      @NonNull String codeOutputFolderRootAbsolutePath,
      @NonNull CodeGenerationOutputContext codeGenerationOutputContext
  ) {
    String domainPkgFilePath = codeOutputFolderRootAbsolutePath + File.separator + "domain";
    String modelPkgFilePath = domainPkgFilePath + File.separator + "model";
    String servicePkgFilePath = domainPkgFilePath + File.separator + "service";

    String pqlPkgFilePath = codeOutputFolderRootAbsolutePath + File.separator + "pql";

    String webExchangePkgFilePath = codeOutputFolderRootAbsolutePath + File.separator + "webexchange";
    String adapterPkgFilePath = webExchangePkgFilePath + File.separator + "adapter";
    String validationPkgFilePath = webExchangePkgFilePath + File.separator + "validation";

    return new CodeScaffoldContext(
        new FileLocation(
            webExchangePkgFilePath,
            codeGenerationOutputContext.controller().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            modelPkgFilePath,
            codeGenerationOutputContext.command().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            modelPkgFilePath,
            codeGenerationOutputContext.result().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            servicePkgFilePath,
            codeGenerationOutputContext.serviceInterface().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            servicePkgFilePath,
            codeGenerationOutputContext.serviceImplementation().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            codeGenerationOutputContext.pqlExchangeRequest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            codeGenerationOutputContext.pqlExchangeResponse().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            codeGenerationOutputContext.pqlExchange().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            codeGenerationOutputContext.pqlQueryBuilder().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            codeGenerationOutputContext.pqlResultMapper().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            adapterPkgFilePath,
            codeGenerationOutputContext.commandAdapter().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            adapterPkgFilePath,
            codeGenerationOutputContext.resultAdapter().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            validationPkgFilePath,
            codeGenerationOutputContext.requestValidator().className() + JAVA_FILE_EXTENSION
        )
    );
  }

  private static @NonNull TestsScaffoldContext getTestsScaffoldContext(
      @NonNull String testOutputFolderRootAbsolutePath,
      @NonNull TestsGenerationOutputContext testsGenerationOutputContext
  ) {
    String testDomainPkgFilePath = testOutputFolderRootAbsolutePath + File.separator + "domain";
    String testServicePkgFilePath = testDomainPkgFilePath + File.separator + "service";

    String pqlPkgFilePath = testOutputFolderRootAbsolutePath + File.separator + "pql";

    String testWebExchangePkgFilePath = testOutputFolderRootAbsolutePath + File.separator + "webexchange";
    String testAdapterPkgFilePath = testWebExchangePkgFilePath + File.separator + "adapter";
    String testValidationPkgFilePath = testWebExchangePkgFilePath + File.separator + "validation";

    return new TestsScaffoldContext(
        new FileLocation(
            testWebExchangePkgFilePath,
            testsGenerationOutputContext.controllerTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            testServicePkgFilePath,
            testsGenerationOutputContext.serviceImplementationTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            testsGenerationOutputContext.pqlQueryBuilderTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            pqlPkgFilePath,
            testsGenerationOutputContext.pqlResultMapperTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            testAdapterPkgFilePath,
            testsGenerationOutputContext.commandAdapterTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            testAdapterPkgFilePath,
            testsGenerationOutputContext.resultAdapterTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            testValidationPkgFilePath,
            testsGenerationOutputContext.requestValidatorTest().className() + JAVA_FILE_EXTENSION
        ),
        new FileLocation(
            testOutputFolderRootAbsolutePath,
            testsGenerationOutputContext.testsDataFactory().className() + JAVA_FILE_EXTENSION
        )
    );
  }

  private static @NonNull TemplateContext getTemplateContext(
      @NonNull String templatesFolderRootAbsolutePath
  ) {
    String codeTemplateFolderAbsolutePath = templatesFolderRootAbsolutePath + File.separator + TEMPLATE_FOLDER_CODE;
    String testsTemplateFolderAbsolutePath = templatesFolderRootAbsolutePath + File.separator + TEMPLATE_FOLDER_TESTS;

    @NonNull var codeTemplateCtx = new CodeTemplateContext(
        new FileLocation(codeTemplateFolderAbsolutePath, "controller.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "command.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "result.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "service_interface.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "service_implementation.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "pql_exchange_request.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "pql_exchange_response.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath,"pql_exchange.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath,"pql_query_builder.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath,"pql_result_mapper.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "command_adapter.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "result_adapter.ditto"),
        new FileLocation(codeTemplateFolderAbsolutePath, "request_validator.ditto")
    );
    @NonNull TestsTemplateContext testTemplateCtx = new TestsTemplateContext(
        new FileLocation(testsTemplateFolderAbsolutePath, "controller_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "service_implementation_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "pql_query_builder_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "pql_result_mapper_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "command_adapter_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "result_adapter_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "request_validator_test.ditto"),
        new FileLocation(testsTemplateFolderAbsolutePath, "test_data_factory.ditto")
    );

    return new TemplateContext(
        codeTemplateCtx, testTemplateCtx
    );
  }

  private static @NonNull CodeGenerationOutputContext getCodeOutputContext(
      @NonNull InputContext inputCtx,
      @NonNull String rootPkgDotPath
  ) {
    var controllerInterfaceClassCtx = inputCtx.controllerInterface();
    String controllerInterfaceClassName = controllerInterfaceClassCtx.className();

    int controllerClassNameSuffixIdx = controllerInterfaceClassName.lastIndexOf(CONTROLLER_CLASS_SUFFIX);
    String controllerInterfaceBaseClassName = (controllerClassNameSuffixIdx != -1)
        ? controllerInterfaceClassName.substring(0, controllerClassNameSuffixIdx)
        : controllerInterfaceClassName;

    //TODO improve to more methods later
    var controllerInterfaceMethodCtx = inputCtx.controllerInterfaceMethods().get(0);
    String controllerInterfaceMethodName = controllerInterfaceMethodCtx.name();

    var controllerInterfaceMethodInputClassCtx = controllerInterfaceMethodCtx.inputType();
    String controllerMethodInputClassName = controllerInterfaceMethodInputClassCtx.className();

    String controllerClassName = controllerInterfaceBaseClassName + CONTROLLER_CLASS_SUFFIX;
    String commandClassName = controllerMethodInputClassName + COMMAND_CLASS_NAME_SUFFIX;
    String commandAdapterClassName = controllerMethodInputClassName + COMMAND_ADAPTER_CLASS_NAME_SUFFIX;
    String resultClassName = controllerMethodInputClassName + RESULT_CLASS_NAME_SUFFIX;
    String resultAdapterClassName = controllerMethodInputClassName + RESULT_ADAPTER_CLASS_NAME_SUFFIX;
    String requestValidatorClassName = controllerMethodInputClassName + REQUEST_VALIDATOR_CLASS_NAME_SUFFIX;
    String serviceInterfaceClassName = controllerInterfaceBaseClassName + SERVICE_INTERFACE_CLASS_NAME_SUFFIX;
    String serviceImplementationClassName = controllerInterfaceBaseClassName + SERVICE_IMPLEMENTATION_CLASS_NAME_SUFFIX;

    String pqlExchangeRequestClassName = controllerMethodInputClassName + PQL_EXCHANGE_REQUEST_CLASS_NAME_SUFFIX;
    String pqlExchangeResponseClassName = controllerMethodInputClassName + PQL_EXCHANGE_RESPONSE_CLASS_NAME_SUFFIX;
    String pqlExchangeClassName = controllerMethodInputClassName + PQL_EXCHANGE_CLASS_NAME_SUFFIX;
    String pqlQueryBuilderClassName = controllerMethodInputClassName + PQL_QUERY_BUILDER_CLASS_NAME_SUFFIX;
    String pqlResultMapperClassName = controllerMethodInputClassName + PQL_RESULT_MAPPER_CLASS_NAME_SUFFIX;

    var controllerClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange", controllerClassName
    );

    var commandClassCtx = new ClassContext(
        rootPkgDotPath +  ".domain.model", commandClassName
    );
    var resultClassCtx = new ClassContext(
        rootPkgDotPath + ".domain.model", resultClassName
    );

    var serviceInterfaceClassCtx = new ClassContext(
        rootPkgDotPath + ".domain.service", serviceInterfaceClassName
    );
    var serviceImplementationClassCtx = new ClassContext(
        rootPkgDotPath + ".domain.service", serviceImplementationClassName
    );
    var serviceImplementationMethodCtx = new MethodContext(
        controllerInterfaceMethodName,
        commandClassCtx,
        resultClassCtx
    );

    var pqlExchangeRequestClassCtx = new ClassContext(
        rootPkgDotPath + ".pql", pqlExchangeRequestClassName
    );
    var pqlExchangeResponseClassCtx = new ClassContext(
        rootPkgDotPath +  ".pql", pqlExchangeResponseClassName
    );
    var pqlExchangeClassCtx = new ClassContext(
        rootPkgDotPath +  ".pql", pqlExchangeClassName
    );
    var pqlQueryBuilderClassCtx = new ClassContext(
        rootPkgDotPath +  ".pql", pqlQueryBuilderClassName
    );
    var pqlResultMapperClassCtx = new ClassContext(
        rootPkgDotPath +  ".pql", pqlResultMapperClassName
    );

    var commandAdapterClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange.adapter", commandAdapterClassName
    );
    var resultAdapterClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange.adapter", resultAdapterClassName
    );
    var requestValidatorClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange.validation", requestValidatorClassName
    );

    return new CodeGenerationOutputContext(
        rootPkgDotPath,
        controllerInterfaceClassCtx,
        controllerInterfaceMethodCtx,
        controllerClassCtx,
        commandClassCtx,
        resultClassCtx,
        serviceInterfaceClassCtx,
        serviceImplementationClassCtx,
        serviceImplementationMethodCtx,
        pqlExchangeRequestClassCtx,
        pqlExchangeResponseClassCtx,
        pqlExchangeClassCtx,
        pqlQueryBuilderClassCtx,
        pqlResultMapperClassCtx,
        commandAdapterClassCtx,
        resultAdapterClassCtx,
        requestValidatorClassCtx
    );
  }

  private static @NonNull TestsGenerationOutputContext getTestsOutputContext(
      @NonNull CodeGenerationOutputContext codeOutputCtx,
      @NonNull String rootPkgDotPath
  ){
    var controllerTestClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange",
        codeOutputCtx.controller().className() + TEST_CLASS_NAME_SUFFIX
    );
    var serviceImplTestClassCtx = new ClassContext(
        rootPkgDotPath + ".domain.service",
        codeOutputCtx.serviceImplementation().className() + TEST_CLASS_NAME_SUFFIX
    );

    var pqlQueryBuilderTest = new ClassContext(
        rootPkgDotPath + ".pql",
        codeOutputCtx.pqlQueryBuilder().className() + TEST_CLASS_NAME_SUFFIX
    );
    var pqlResultMapperTest = new ClassContext(
        rootPkgDotPath + ".pql",
        codeOutputCtx.pqlResultMapper().className() + TEST_CLASS_NAME_SUFFIX
    );

    var commandAdapterTestClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange.adapter",
        codeOutputCtx.commandAdapter().className() + TEST_CLASS_NAME_SUFFIX
    );
    var resultAdapterTestClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange",
        codeOutputCtx.resultAdapter().className() + TEST_CLASS_NAME_SUFFIX
    );
    var requestValidatorTestClassCtx = new ClassContext(
        rootPkgDotPath + ".webexchange",
        codeOutputCtx.requestValidator().className() + TEST_CLASS_NAME_SUFFIX
    );

    var testsDataFactoryClassCtx = new ClassContext(
        rootPkgDotPath,
        codeOutputCtx.controller().className() + TEST_DATA_FACTORY_CLASS_NAME_SUFFIX
    );

    return new TestsGenerationOutputContext(
        rootPkgDotPath,
        controllerTestClassCtx,
        serviceImplTestClassCtx,
        pqlQueryBuilderTest,
        pqlResultMapperTest,
        commandAdapterTestClassCtx,
        resultAdapterTestClassCtx,
        requestValidatorTestClassCtx,
        testsDataFactoryClassCtx,
        codeOutputCtx
    );
  }

}
