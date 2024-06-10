package br.com.acme.ditto.service;

import br.com.acme.ditto.model.context.io.DittoCLIOptions;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DittoCLIService {

  public static final Options CLI_OPTIONS = createCliOptions();
  public static final String CLI_ASCII_ART =
   """
       \n
       ____   __   ____  ____   __ \s
      (    \\ (  ) (_  _)(_  _) /  \\\s
       ) D (  )(    )(    )(  (  O )
      (____/ (__)  (__)  (__)  \\__/\s
   """;

  private static @NonNull Options createCliOptions() {
    var options = new Options();

    Arrays.stream(DittoCLIOptions.values()).forEach(
        enumMember -> options.addOption(enumMember.getEnumMemberAsApacheCliOption())
    );

    return options;
  }

  public static @NonNull CommandLine parse(
      @NonNull String[] args
  ) throws ParseException {
    var parser = new DefaultParser();
    return parser.parse(CLI_OPTIONS, args);
  }

  public static boolean isArgPresent(
      @NonNull DittoCLIOptions dittoCliOption,
      @NonNull CommandLine commandLine
  ) {
    var cliOption = dittoCliOption.getEnumMemberAsApacheCliOption();
    return commandLine.hasOption(cliOption);
  }

  public static @NonNull String getCliArgValue(
      @NonNull DittoCLIOptions dittoCliOption,
      @NonNull CommandLine commandLine
  ){
    var cliOption = dittoCliOption.getEnumMemberAsApacheCliOption();
    String cliOptionValue = commandLine.getOptionValue(cliOption);

    if(Objects.isNull(cliOptionValue)){
      throw new IllegalStateException(
          "No value provided for cli option: " + cliOption.getArgName()
      );

    } else {
      cliOptionValue = cliOptionValue.trim();

      if(!cliOptionValue.isEmpty()){
        return cliOptionValue;
      } else {
        throw new IllegalStateException(
            "Blank value provided for cli option: " + cliOption.getArgName()
        );
      }
    }
  }

}
