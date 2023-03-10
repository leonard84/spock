package aPackage
import spock.lang.*

class ASpec extends Specification {
  def "aFeature"() {
/*--------- tag::snapshot[] ---------*/
@org.spockframework.runtime.model.FeatureMetadata(name = 'a feature', ordinal = 0, line = 1, blocks = [@org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.SETUP, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.WHEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = [])], parameterNames = [])
public void $spock_feature_0_0() {
    org.spockframework.runtime.ErrorCollector $spock_errorCollector = org.spockframework.runtime.ErrorRethrower.INSTANCE
    java.util.List list = this.MockImpl('list', java.util.List)
    this.getSpecificationContext().getMockController().enterScope()
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(8, 5, '1 * list.add(1)').setFixedCount(1).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addEqualArg(1).build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(9, 5, '(1..2) * list.add({ i < 10 })').setRangeCount(1, 2, true).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addCodeArg({ ->
        org.spockframework.runtime.ValueRecorder $spock_valueRecorder1 = new org.spockframework.runtime.ValueRecorder()
        try {
            org.spockframework.runtime.SpockRuntime.verifyCondition($spock_errorCollector, $spock_valueRecorder1.reset(), 'i < 10', 9, 25, null, $spock_valueRecorder1.record($spock_valueRecorder1.startRecordingValue(2), $spock_valueRecorder1.record($spock_valueRecorder1.startRecordingValue(0), i) < $spock_valueRecorder1.record($spock_valueRecorder1.startRecordingValue(1), 10)))
        }
        catch (java.lang.Throwable $spock_condition_throwable) {
            org.spockframework.runtime.SpockRuntime.conditionFailedWithException($spock_errorCollector, $spock_valueRecorder1, 'i < 10', 9, 25, null, $spock_condition_throwable)}
        finally {
        }
    }).build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(10, 5, '(1.._) * list.add(_ as Integer)').setRangeCount(1, _, true).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addEqualArg(_).typeLastArg(java.lang.Integer).build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(11, 5, '_ * list.add(_)').setFixedCount(_).addEqualTarget(list).addEqualMethodName('add').setArgListKind(true, false).addEqualArg(_).build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(12, 5, '_ * _._').setFixedCount(_).addEqualTarget(_).addEqualPropertyName('_').build())
    this.getSpecificationContext().getMockController().addInteraction(new org.spockframework.mock.runtime.InteractionBuilder(13, 5, '_ * _').setFixedCount(_).addWildcardTarget().addEqualMethodName('_').build())
    10.times({ ->
        list.add(1)
    })
    this.getSpecificationContext().getMockController().leaveScope()
    this.getSpecificationContext().getMockController().leaveScope()
}
/*--------- end::snapshot[] ---------*/
  }
}
