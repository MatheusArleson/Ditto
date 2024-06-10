package br.com.acme.ditto.model.context.code;

import lombok.NonNull;

public record ClassContext(
    @NonNull String packageName,
    @NonNull String className
) {

  public @NonNull String getArgName(){
    char[] classNameChars = className.toCharArray();
    classNameChars[0] = Character.toLowerCase(classNameChars[0]);
    return new String(classNameChars);
  }

}
