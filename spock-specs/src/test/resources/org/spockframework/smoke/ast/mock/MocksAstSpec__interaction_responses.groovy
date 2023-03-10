package aPackage
import spock.lang.*

class ASpec extends Specification {
  def "aFeature"() {
/*--------- tag::snapshot[] ---------*/
@org.spockframework.runtime.model.FeatureMetadata(name = 'a feature', ordinal = 0, line = 1, blocks = [@org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.SETUP, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.WHEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = [])], parameterNames = [])
public void $spock_feature_0_0() {
    java.util.List list = this.MockImpl('list', java.util.List)
    this.getSpecificationContext().getMockController().enterScope()
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(11, 5, '1 * list.get(1) >> 1').setFixedCount(1).addEqualTarget(list).addEqualMethodName('get').setArgListKind(true, false).addEqualArg(1).addConstantResponse(1).build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(12, 5, '1 * list.get(2) >> { it[0] * 2 }').setFixedCount(1).addEqualTarget(list).addEqualMethodName('get').setArgListKind(true, false).addEqualArg(2).addCodeResponse({ ->
        it [ 0] * 2
    }).build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(13, 5, '2 * list.get(3) >> 1 >> { it[0] * 3 }').setFixedCount(2).addEqualTarget(list).addEqualMethodName('get').setArgListKind(true, false).addEqualArg(3).addCodeResponse({ ->
        it [ 0] * 3
    }).addConstantResponse(1).build())
    list.get(1)
    list.get(2)
    list.get(3)
    list.get(3)
    this.getSpecificationContext().getMockController().leaveScope()
    this.getSpecificationContext().getMockController().leaveScope()
}
/*--------- end::snapshot[] ---------*/
  }
}
