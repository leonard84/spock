package aPackage
import spock.lang.*

class ASpec extends Specification {
  def "aFeature"() {
/*--------- tag::snapshot[] ---------*/
@org.spockframework.runtime.model.FeatureMetadata(name = 'a feature', ordinal = 0, line = 1, blocks = [@org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.SETUP, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.WHEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = [])], parameterNames = [])
public void $spock_feature_0_0() {
    java.util.List list = this.MockImpl('list', java.util.List)
    this.getSpecificationContext().getMockController().enterScope()
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(10, 5, '1 * list.add(1)').setFixedCount(1).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addEqualArg(1).build())
    this.getSpecificationContext().getMockController().addBarrier()
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(12, 5, '1 * list.add(2)').setFixedCount(1).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addEqualArg(2).build())
    this.getSpecificationContext().getMockController().addBarrier()
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(14, 5, '1 * list.add(3)').setFixedCount(1).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addEqualArg(3).build())
    list.add(1)
    list.add(2)
    list.add(3)
    this.getSpecificationContext().getMockController().leaveScope()
    this.getSpecificationContext().getMockController().leaveScope()
}
/*--------- end::snapshot[] ---------*/
  }
}
