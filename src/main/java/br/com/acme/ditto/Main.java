package br.com.acme.ditto;

import br.com.acme.ditto.model.context.io.DittoCLIOptions;
import br.com.acme.ditto.service.DittoCLIService;
import br.com.acme.ditto.service.DittoContextFactory;
import br.com.acme.ditto.service.TemplateService;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

@Slf4j
public class Main {

  private static final String OUTPUT_PROJECT_CODE_ROOT_FOLDER = "src/main/java";
  private static final String OUTPUT_PROJECT_TESTS_ROOT_FOLDER = "src/test/java";

  private static final String ANY_DOT_CHAR_REGEX_PATTERN = Pattern.compile("\\.").pattern();
  private static final String HELP_MSG_USAGE_CMD_LINE = """
        java -jar /absolute/path/to/ditto/jar.jar \\
        -trf /absolute/path/to/ditto/templates/root/folder \\
        -ci  /absolute/path/to/your/controller/interface/java/class.java \\
        -orf /absolute/path/to/output/root/folder \\
        -opn your.destination.package.name
      """;

  public static void main(String[] args) throws Exception {
    log.info(DittoCLIService.CLI_ASCII_ART);

    log.info("Parsing cli args...");
    final var mainArgs = DittoCLIService.parse(args);

    if(DittoCLIService.isArgPresent(DittoCLIOptions.HELP, mainArgs)) {
      handleHelpArg();
    } else {
      log.info("Starting.");
      executeDittoTransform(mainArgs);
      log.info("Finished.");
    }
  }

  private static void handleHelpArg() {
    var formatter = new HelpFormatter();
    formatter.printHelp(HELP_MSG_USAGE_CMD_LINE, DittoCLIService.CLI_OPTIONS);
  }

  private static void executeDittoTransform(
      @NonNull CommandLine commandLineArgs
  ) throws IOException {
    String argTemplatesRootFolder = DittoCLIService.getCliArgValue(
        DittoCLIOptions.TEMPLATE_ROOT_FOLDER, commandLineArgs
    );
    String argControllerInterfaceAbsolutePath = DittoCLIService.getCliArgValue(
        DittoCLIOptions.CONTROLLER_INTERFACE_ABSOLUTE_PATH, commandLineArgs
    );
    String argOutputPackageQualifiedName = DittoCLIService.getCliArgValue(
        DittoCLIOptions.OUTPUT_PACKAGE_QUALIFIED_NAME, commandLineArgs
    );
    String argOutputRootFolderAbsolutePath = DittoCLIService.getCliArgValue(
        DittoCLIOptions.OUTPUT_ROOT_FOLDER_ABSOLUTE_PATH, commandLineArgs
    );

    String outputPkgFilePath = argOutputPackageQualifiedName.replaceAll(
        ANY_DOT_CHAR_REGEX_PATTERN,
        File.separator
    );

    String codeOutputRootFolderPath = argOutputRootFolderAbsolutePath + File.separator
        + OUTPUT_PROJECT_CODE_ROOT_FOLDER + File.separator
        + outputPkgFilePath;

    String testOutputRootFolderPath = argOutputRootFolderAbsolutePath + File.separator
        + OUTPUT_PROJECT_TESTS_ROOT_FOLDER + File.separator
        + outputPkgFilePath;

    log.info("Creating context.");
    var dittoCtx = DittoContextFactory.getDittoCtx(
        argTemplatesRootFolder,
        argControllerInterfaceAbsolutePath,
        codeOutputRootFolderPath,
        testOutputRootFolderPath,
        argOutputPackageQualifiedName
    );

    log.info("Reading templates.");
    var templateService = TemplateService.createTemplateFactory(dittoCtx.templates());

    log.info("Transforming templates.");
    TemplateService.execute(templateService, dittoCtx);
  }

}