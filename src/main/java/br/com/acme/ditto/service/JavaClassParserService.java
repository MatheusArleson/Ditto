package br.com.acme.ditto.service;

import br.com.acme.ditto.model.context.code.AnnotationContext;
import br.com.acme.ditto.model.context.code.ClassContext;
import br.com.acme.ditto.model.context.code.ControllerInterfaceMethodContext;
import br.com.acme.ditto.model.context.code.FileLocation;
import br.com.acme.ditto.model.context.io.InputContext;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.utils.Log;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavaClassParserService {

  public static InputContext extractControllerInterfaceInfo(
      @NonNull String javaSourceFileAbsolutePath
  ) throws IOException {
    Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

    var javaSourceFile = new File(javaSourceFileAbsolutePath);

    var controllerFileLocation = new FileLocation(
        javaSourceFile.getParentFile().getAbsolutePath(),
        javaSourceFile.getName()
    );

    var compilationUnit = StaticJavaParser.parse(javaSourceFile);

    String controllerPkgName = compilationUnit.getPackageDeclaration()
        .orElseThrow(() ->
            new IllegalStateException("Could not extract controller interface package statement.")
        ).getNameAsString();

    String controllerClassName = compilationUnit.getPrimaryTypeName()
        .orElseThrow(() ->
            new IllegalStateException("Could not extract controller interface class name.")
        );

    final ClassContext controllerInterfaceClassCtx = new ClassContext(
        controllerPkgName, controllerClassName
    );


    //TODO optimize import lookup, currently linear search multiple times
    var imports = compilationUnit.getImports();
    var controllerInterfaceMethods = compilationUnit.findAll(MethodDeclaration.class);

    final List<ControllerInterfaceMethodContext> controllerInterfaceMethodsCtx = new ArrayList<>();
    controllerInterfaceMethods.stream()
        .filter(md -> {
          boolean isReturnResponseEntityClass = md.getType()
              .asClassOrInterfaceType()
              .getNameAsString()
              .equalsIgnoreCase("ResponseEntity");
          boolean isInputSingleArg = Objects.equals(
              md.getParameters().size(), 1
          );
          return isReturnResponseEntityClass && isInputSingleArg;
        }).forEach(md -> {
          String methodName = md.getNameAsString();

          var outputTypeClassCtx = extractMethodReturnClassCtx(md, imports);
          var inputTypeClassCtx = extractMethodInputClassCtx(md, imports);
          var requestMappingAnnotationCtx = extractRequestMappingAnnotationContext(md);

          controllerInterfaceMethodsCtx.add(new ControllerInterfaceMethodContext(
              methodName,
              inputTypeClassCtx,
              outputTypeClassCtx,
              requestMappingAnnotationCtx
          ));
        });

    return new InputContext(
        controllerFileLocation, controllerInterfaceClassCtx, controllerInterfaceMethodsCtx
    );
  }

  private static ClassContext extractMethodInputClassCtx(
      @NonNull MethodDeclaration md,
      @NonNull NodeList<ImportDeclaration> imports
  ) {
    var allParameters = md.getParameters();
    var firstParameter = allParameters.get(0);

    var firstParamType = firstParameter.getType();
    var firstParamTypeClass = firstParamType.asClassOrInterfaceType();
    String firstParamClassName = firstParamTypeClass.getNameAsString();

    var importDeclaration = findImportDeclarationByClassName(imports, firstParamClassName);
    String firstParamTypePkgDotPath = importDeclaration.substring(
        0, importDeclaration.lastIndexOf(".")
    );

    return new ClassContext(
        firstParamTypePkgDotPath, firstParamClassName
    );
  }

  private static ClassContext extractMethodReturnClassCtx(
      @NonNull MethodDeclaration md,
      @NonNull NodeList<ImportDeclaration> imports
  ) {
    var responseEntityType = md.getType();
    var responseEntityTypeClass = responseEntityType.asClassOrInterfaceType();

    var responseEntityWrappedTypeClass = responseEntityTypeClass.getTypeArguments()
        .orElseThrow(() ->
            new IllegalStateException("Return Type of method does not wrap any type : "
                + responseEntityTypeClass.getNameAsString())
        )
        .get(0);

    var returnTypeClass = responseEntityWrappedTypeClass.asClassOrInterfaceType();
    String returnTypeClassName = returnTypeClass.getNameAsString();

    var importDeclaration = findImportDeclarationByClassName(imports, returnTypeClassName);
    String returnTypePkgDotPath = importDeclaration.substring(
        0, importDeclaration.lastIndexOf(".")
    );

    return new ClassContext(returnTypePkgDotPath, returnTypeClassName);
  }

  private static String findImportDeclarationByClassName(
      @NonNull NodeList<ImportDeclaration> imports,
      @NonNull String className
  ) {
    return imports.parallelStream()
        .filter(imp -> imp.getNameAsString().contains(className))
        .findFirst()
        .orElseThrow(() ->
            new IllegalStateException("Could not find import statement for class: " + className)
        )
        .getNameAsString();
  }

  private static @NonNull AnnotationContext extractRequestMappingAnnotationContext(
      @NonNull MethodDeclaration md
  ){
    AnnotationExpr annotation = md.getAnnotations().stream()
        .filter(a -> Objects.equals(a.getNameAsString(), "RequestMapping"))
        .findFirst()
        .orElseThrow(() ->
            new IllegalStateException("Could not extract RequestMapping annotation from method.")
        );

    Map<String, String> annotationParams = annotation.asNormalAnnotationExpr().getPairs().stream()
        .map(p -> {
          String annotationParamName = p.getNameAsString();

          String annotationParamRawValue = p.getValue().toString();
          String annotationParamCleanValue = sanitizeAnnotationParamValue(annotationParamRawValue);

          return new SimpleEntry<>(annotationParamName, annotationParamCleanValue);
        })
        .collect(
            Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)
        );

    return new AnnotationContext(
        "org.springframework.web.bind.annotation",
        "RequestMapping",
        annotationParams
    );
  }

  private static @NonNull String sanitizeAnnotationParamValue(@NonNull String rawValue){
    List<String> termsToClean = List.of("\"", "'", "&quot;");

    String cleanStr = rawValue;
    for (String term : termsToClean) {
      int startIndex = 0;
      int endIndex = cleanStr.length();
      boolean hasToModify = false;

      if(cleanStr.startsWith(term)){
        hasToModify = true;
        startIndex = term.length();
      }
      if(cleanStr.endsWith(term)){
        hasToModify = true;
        endIndex = endIndex - term.length();
      }

      if(hasToModify){
        cleanStr = cleanStr.substring(startIndex, endIndex);
      }
    }

    return cleanStr;
  }

}
