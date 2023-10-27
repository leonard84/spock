package org.spockframework.check;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.arbitraries.IteratorArbitrary;
import org.spockframework.runtime.IDataIterator;
import org.spockframework.runtime.extension.IDataDriver;
import org.spockframework.runtime.extension.IIterationRunner;
import org.spockframework.runtime.model.ParameterInfo;

import java.util.List;

public class PropertyDataDriver implements IDataDriver {
  private final Property annotation;

  public PropertyDataDriver(Property annotation) {
    this.annotation = annotation;
  }

  @Override
  public void runIterations(IDataIterator dataIterator, IIterationRunner iterationRunner, List<ParameterInfo> parameters) {
    if (!dataIterator.hasNext()) {
      throw new PropertyTestConfigurationException("No data variables defined");
    }
    Object[] arbitraries = dataIterator.next();
    if (dataIterator.hasNext()) {
      throw new PropertyTestConfigurationException("Only one set of data variables is allowed");
    }
    int limit = annotation.tries() == -1 ? 1000 : annotation.tries();
    for (int count = 0; count < limit; count++) {
      Object[] output = new Object[arbitraries.length];
      for (int i = 0; i < arbitraries.length; i++) {
        output[i] = ((Arbitrary) arbitraries[i]).sample();
      }
      iterationRunner.runIteration(output, dataIterator.getEstimatedNumIterations());
    }
  }
}
