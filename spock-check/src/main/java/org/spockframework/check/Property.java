package org.spockframework.check;

import org.spockframework.runtime.extension.ExtensionAnnotation;
import spock.lang.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtensionAnnotation(PropertyExtension.class)
public @interface Property {
  int tries() default -1;
}
