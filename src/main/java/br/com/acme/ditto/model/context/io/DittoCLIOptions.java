package br.com.acme.ditto.model.context.io;

import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.cli.Option;

@Getter
public enum DittoCLIOptions {
  HELP(
      "h", "help", false,
      "Displays this help message."
  ),
  TEMPLATE_ROOT_FOLDER(
      "trf", "templateRootFolder", true,
      "Absolute file system path to the root folder where your ditto templates are."
  ),
  CONTROLLER_INTERFACE_ABSOLUTE_PATH(
      "ci", "controllerInterfaceAbsolutePath", true,
      "Absolute file system path to the controller interface java file."
  ),
  OUTPUT_ROOT_FOLDER_ABSOLUTE_PATH(
      "orf", "outputRootFolderAbsolutePath", true,
      "Absolute file system path to the output folder where the output files are generated."
  ),
  OUTPUT_PACKAGE_QUALIFIED_NAME(
      "opn", "outputPackageQualifiedName", true,
      "Qualified name of the output package (eg. com.acme.ditto.out)."
  );

  private final String shortName;
  private final String longName;
  private final boolean required;
  private final String description;
  private final Option enumMemberAsApacheCliOption;

  DittoCLIOptions(
      @NonNull String shortName,
      @NonNull String longName,
      boolean required,
      @NonNull String description
  ) {
    this.shortName = shortName;
    this.longName = longName;
    this.required = required;
    this.description = description;

    this.enumMemberAsApacheCliOption = new Option(shortName, longName, required, description);
  }

}
