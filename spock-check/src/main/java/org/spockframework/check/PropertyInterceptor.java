package org.spockframework.check;

import net.jqwik.api.sessions.JqwikSession;
import org.spockframework.runtime.extension.IMethodInvocation;

public class PropertyInterceptor implements org.spockframework.runtime.extension.IMethodInterceptor {
  @Override
  public void intercept(IMethodInvocation invocation) throws Throwable {
    JqwikSession.start();
    invocation.proceed();
    JqwikSession.finish();
  }
}
