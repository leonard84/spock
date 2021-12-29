package spock.guice;

import java.lang.annotation.*;

/**
 * Instructs Guice to create a spy for the annotated field.
 * <p>
 * Guice spies will behave like global mocks, in that they will intercept all calls to instances of the same type.
 * In fact, they will also intercept calls to subtypes of the given type, as long as the methods are present in the
 * type of the annotated field.
 *
 * @since 2.2
 * @author Leonard Brünings
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GuiceSpyInterceptor {
}
