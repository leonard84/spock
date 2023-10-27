package org.spockframework.check;

import org.spockframework.runtime.extension.IAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;

public class PropertyExtension implements IAnnotationDrivenExtension<Property> {
  @Override
  public void visitFeatureAnnotation(Property annotation, FeatureInfo feature) {
    feature.setDataDriver(new PropertyDataDriver(annotation));
    feature.addInterceptor(new PropertyInterceptor());
  }
}
