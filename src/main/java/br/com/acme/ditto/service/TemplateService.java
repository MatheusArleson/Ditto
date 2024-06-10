package br.com.acme.ditto.service;

import br.com.acme.ditto.model.context.DittoContext;
import br.com.acme.ditto.model.context.code.FileLocation;
import br.com.acme.ditto.model.context.io.CodeGenerationOutputContext;
import br.com.acme.ditto.model.context.io.TestsGenerationOutputContext;
import br.com.acme.ditto.model.context.scaffold.CodeScaffoldContext;
import br.com.acme.ditto.model.context.scaffold.TestsScaffoldContext;
import br.com.acme.ditto.model.context.template.CodeTemplateContext;
import br.com.acme.ditto.model.context.template.TemplateContext;
import br.com.acme.ditto.model.context.template.TestsTemplateContext;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class TemplateService {

  private final Map<CodeTemplateTypes, Mustache> codeTemplateTypeToTemplateInstanceMap = new EnumMap<>(CodeTemplateTypes.class);
  private final Map<TestTemplateTypes, Mustache> testTemplateTypeToTemplateInstanceMap = new EnumMap<>(TestTemplateTypes.class);

  private enum CodeTemplateTypes {
    CONTROLLER,
    COMMAND,
    RESULT,
    SERVICE_INTERFACE,
    SERVICE_IMPL,
    PQL_EXCHANGE_REQUEST,
    PQL_EXCHANGE_RESPONSE,
    PQL_EXCHANGE,
    PQL_QUERY_BUILDER,
    PQL_RESULT_MAPPER,
    COMMAND_ADAPTER,
    RESULT_ADAPTER,
    REQUEST_VALIDATOR
    ;
  }

  private enum TestTemplateTypes {
    CONTROLLER_TEST,
    SERVICE_IMPL_TEST,
    PQL_QUERY_BUILDER_TEST,
    PQL_RESULT_MAPPER_TEST,
    COMMAND_ADAPTER_TEST,
    RESULT_ADAPTER_TEST,
    REQUEST_VALIDATOR_TEST,
    TEST_DATA_FACTORY
    ;
  }

  public static @NonNull TemplateService createTemplateFactory(
      @NonNull TemplateContext templates
  ) throws IOException {
    var templateService = new TemplateService();
    var mustacheFactory = new DefaultMustacheFactory();

    log.info("Creating code templates.");
    bootCodeTemplates(
        templates.codeTemplateContext(),
        mustacheFactory,
        templateService
    );

    log.info("Creating tests templates.");
    bootTestTemplates(
        templates.testsTemplateContext(),
        mustacheFactory,
        templateService
    );

    return templateService;
  }

  private static void bootCodeTemplates(
      @NonNull CodeTemplateContext codeTemplateContext,
      @NonNull MustacheFactory mustacheFactory,
      @NonNull TemplateService templateService
  ) throws IOException {
    Map<CodeTemplateTypes, FileLocation> templateTypeToTemplateFileMap = new EnumMap<>(CodeTemplateTypes.class);
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.CONTROLLER, codeTemplateContext.controllerTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.COMMAND, codeTemplateContext.commandTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.RESULT, codeTemplateContext.resultTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.SERVICE_INTERFACE, codeTemplateContext.serviceInterfaceTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.PQL_EXCHANGE_REQUEST, codeTemplateContext.pqlExchangeRequestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.PQL_EXCHANGE_RESPONSE, codeTemplateContext.pqlExchangeResponseTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.PQL_EXCHANGE, codeTemplateContext.pqlExchangeTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.PQL_QUERY_BUILDER, codeTemplateContext.pqlQueryBuilderTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.PQL_RESULT_MAPPER, codeTemplateContext.pqlResultMapperTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.SERVICE_IMPL, codeTemplateContext.serviceImplementationTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.COMMAND_ADAPTER, codeTemplateContext.commandAdapterTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.RESULT_ADAPTER, codeTemplateContext.resultAdapterTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        CodeTemplateTypes.REQUEST_VALIDATOR, codeTemplateContext.requestValidatorTemplateLocation()
    );

    loadTemplateStream(
        templateTypeToTemplateFileMap, mustacheFactory,
        templateService.codeTemplateTypeToTemplateInstanceMap
    );
  }

  private static void bootTestTemplates(
      @NonNull TestsTemplateContext testTemplateContext,
      @NonNull MustacheFactory mustacheFactory,
      @NonNull TemplateService templateService
  ) throws IOException {
    Map<TestTemplateTypes, FileLocation> templateTypeToTemplateFileMap = new EnumMap<>(TestTemplateTypes.class);

    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.CONTROLLER_TEST, testTemplateContext.controllerTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.SERVICE_IMPL_TEST, testTemplateContext.serviceImplementationTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.PQL_QUERY_BUILDER_TEST, testTemplateContext.pqlQueryBuilderTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.PQL_RESULT_MAPPER_TEST, testTemplateContext.pqlResultMapperTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.COMMAND_ADAPTER_TEST, testTemplateContext.commandAdapterTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.RESULT_ADAPTER_TEST, testTemplateContext.resultAdapterTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.REQUEST_VALIDATOR_TEST, testTemplateContext.requestValidatorTestTemplateLocation()
    );
    templateTypeToTemplateFileMap.put(
        TestTemplateTypes.TEST_DATA_FACTORY, testTemplateContext.testDataFactoryTemplateLocation()
    );

    loadTemplateStream(
        templateTypeToTemplateFileMap, mustacheFactory,
        templateService.testTemplateTypeToTemplateInstanceMap
    );
  }

  private static <E> void loadTemplateStream(
      @NonNull Map<E, FileLocation> templateTypeToTemplateFileMap,
      @NonNull MustacheFactory mustacheFactory,
      @NonNull Map<E, Mustache> templateTypeToMustacheMap
  ) throws IOException {
    for (Entry<E, FileLocation> entry : templateTypeToTemplateFileMap.entrySet()) {
      E templateType = entry.getKey();
      FileLocation templateFileLocation = entry.getValue();
      String templateFileName = templateFileLocation.fileName();
      var templateFile = new File(templateFileLocation.fileParentAbsolutePath(), templateFileName);
      var templateFileReader = new FileReader(templateFile);
      var moustacheTemplate = mustacheFactory.compile(templateFileReader, templateFileName);
      templateTypeToMustacheMap.putIfAbsent(templateType, moustacheTemplate);
    }
  }

  public static void execute(
      @NonNull TemplateService templateService,
      @NonNull DittoContext dittoCtx
  ) throws IOException {
    var scaffold = dittoCtx.scaffold();

    log.info("Working on code templates transformation.");
    executeCodeGeneration(
        scaffold.codeScaffoldContext(),
        dittoCtx.codeOutput(),
        templateService.codeTemplateTypeToTemplateInstanceMap
    );

    log.info("Working on tests templates transformation.");
    executeTestsGeneration(
        scaffold.testsScaffoldContext(),
        dittoCtx.testsOutput(),
        templateService.testTemplateTypeToTemplateInstanceMap
    );
  }

  private static void executeCodeGeneration(
      @NonNull CodeScaffoldContext codeScaffoldContext,
      @NonNull CodeGenerationOutputContext codeOutputContext,
      @NonNull Map<CodeTemplateTypes, Mustache> codeTemplateTypeToTemplateInstanceMap
  ) throws IOException {
    for (Entry<CodeTemplateTypes, Mustache> entry : codeTemplateTypeToTemplateInstanceMap.entrySet()) {
      var templateType = entry.getKey();
      var moustacheTemplate = entry.getValue();

      FileLocation fl;
      switch (templateType) {
        case CONTROLLER -> fl = codeScaffoldContext.controllerFileDestination();
        case COMMAND -> fl = codeScaffoldContext.commandFileDestination();
        case RESULT -> fl = codeScaffoldContext.resultFileDestination();
        case SERVICE_INTERFACE -> fl = codeScaffoldContext.serviceInterfaceFileDestination();
        case SERVICE_IMPL -> fl = codeScaffoldContext.serviceImplementationFileDestination();
        case PQL_EXCHANGE_REQUEST -> fl = codeScaffoldContext.pqlExchangeRequestFileDestination();
        case PQL_EXCHANGE_RESPONSE -> fl = codeScaffoldContext.pqlExchangeResponseFileDestination();
        case PQL_EXCHANGE -> fl = codeScaffoldContext.pqlExchangeFileDestination();
        case PQL_QUERY_BUILDER -> fl = codeScaffoldContext.pqlQueryBuilderFileDestination();
        case PQL_RESULT_MAPPER -> fl = codeScaffoldContext.pqlResultMapperFileDestination();
        case COMMAND_ADAPTER -> fl = codeScaffoldContext.commandAdapterFileDestination();
        case RESULT_ADAPTER -> fl = codeScaffoldContext.resultAdapterFileDestination();
        case REQUEST_VALIDATOR -> fl = codeScaffoldContext.requestValidatorFileDestination();
        default -> throw new IllegalStateException("Unexpected template type: " + templateType);
      }

      executeSingleCodeGeneration(moustacheTemplate, codeOutputContext, fl);
    }
  }

  private static void executeTestsGeneration(
      @NonNull TestsScaffoldContext testsScaffoldContext,
      @NonNull TestsGenerationOutputContext testsGenerationOutputContext,
      @NonNull Map<TestTemplateTypes, Mustache> testTemplateTypeToTemplateInstanceMap
  ) throws IOException {
    for (Entry<TestTemplateTypes, Mustache> entry : testTemplateTypeToTemplateInstanceMap.entrySet()) {
      var templateType = entry.getKey();
      var moustacheTemplate = entry.getValue();

      FileLocation fl;
      switch (templateType) {
        case CONTROLLER_TEST -> fl = testsScaffoldContext.controllerTestFileDestination();
        case SERVICE_IMPL_TEST -> fl = testsScaffoldContext.serviceImplementationTestFileDestination();
        case PQL_QUERY_BUILDER_TEST -> fl = testsScaffoldContext.pqlQueryBuilderTestFileDestination();
        case PQL_RESULT_MAPPER_TEST -> fl = testsScaffoldContext.pqlResultMapperTestFileDestination();
        case COMMAND_ADAPTER_TEST -> fl = testsScaffoldContext.commandAdapterTestFileDestination();
        case RESULT_ADAPTER_TEST -> fl = testsScaffoldContext.resultAdapterTestFileDestination();
        case REQUEST_VALIDATOR_TEST -> fl = testsScaffoldContext.requestValidatorTestFileDestination();
        case TEST_DATA_FACTORY -> fl = testsScaffoldContext.testsDataFactoryFileDestination();
        default -> throw new IllegalStateException("Unexpected template type: " + templateType);
      }

      executeSingleCodeGeneration(moustacheTemplate, testsGenerationOutputContext, fl);
    }
  }

  private static void executeSingleCodeGeneration(
      @NonNull Mustache moustacheTemplate,
      @NonNull Object templateContextObj,
      @NonNull FileLocation outputFileLocation
  ) throws IOException {
    String outputParentFolderPath = outputFileLocation.fileParentAbsolutePath();
    File outputParentFolder = new File(outputParentFolderPath);
    if (!outputParentFolder.exists()) {
      outputParentFolder.mkdirs();
    }

    File outputFile = new File(outputParentFolder, outputFileLocation.fileName());
    if(outputFile.exists()){
      log.error(
          "Skipping generation of file because it already exists. File name: {}, File path: {}",
          outputFileLocation.fileName(),
          outputFileLocation.fileParentAbsolutePath()
      );
    } else {
      try (FileWriter fileWriter = new FileWriter(outputFile)) {
        moustacheTemplate.execute(fileWriter, templateContextObj);
        fileWriter.flush();
      }
    }
  }

}
