package org.anasantana.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidarURL {
    // Regex restrito apenas para HTTP e HTTPS conforme esperado pelos testes [cite: 2025-12-23]
    String regex() default "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
}