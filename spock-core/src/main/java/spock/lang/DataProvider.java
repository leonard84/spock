package spock.lang;


import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.spockframework.compiler.DataProviderTransformation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@GroovyASTTransformationClass(classes = DataProviderTransformation.class)
public @interface DataProvider {
}
