package aPackage
import spock.lang.*

class ASpec extends Specification {
  def "aFeature"() {
/*--------- tag::snapshot[] ---------*/
@org.spockframework.runtime.model.FeatureMetadata(name = 'a feature', ordinal = 0, line = 1, blocks = [@org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.SETUP, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.WHEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.WHEN, texts = []), @org.spockframework.runtime.model.BlockMetadata(kind = org.spockframework.runtime.model.BlockKind.THEN, texts = [])], parameterNames = [])
public void $spock_feature_0_0() {
    org.spockframework.runtime.ErrorCollector $spock_errorCollector = org.spockframework.runtime.ErrorRethrower.INSTANCE
    org.spockframework.runtime.ValueRecorder $spock_valueRecorder = new org.spockframework.runtime.ValueRecorder()
    java.lang.Object list = [1]
    java.lang.Object $spock_oldValue0 = list.size()
    list.add(2)
    try {
        org.spockframework.runtime.SpockRuntime.verifyCondition($spock_errorCollector, $spock_valueRecorder.reset(), 'list.size() == old(list.size()) + 1', 8, 5, null, $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(13), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(3), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(0), list).$spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(1), 'size')()) == $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(12), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(10), this.$spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(4), 'oldImpl')($spock_oldValue0)) + $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(11), 1))))
    }
    catch (java.lang.Throwable $spock_condition_throwable) {
        org.spockframework.runtime.SpockRuntime.conditionFailedWithException($spock_errorCollector, $spock_valueRecorder, 'list.size() == old(list.size()) + 1', 8, 5, null, $spock_condition_throwable)}
    finally {
    }
    java.lang.Object $spock_oldValue1 = list.size()
    list.add(3)
    try {
        org.spockframework.runtime.SpockRuntime.verifyCondition($spock_errorCollector, $spock_valueRecorder.reset(), 'list.size() == old(list.size()) + 1', 14, 5, null, $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(13), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(3), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(0), list).$spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(1), 'size')()) == $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(12), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(10), this.$spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(4), 'oldImpl')($spock_oldValue1)) + $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(11), 1))))
    }
    catch (java.lang.Throwable $spock_condition_throwable) {
        org.spockframework.runtime.SpockRuntime.conditionFailedWithException($spock_errorCollector, $spock_valueRecorder, 'list.size() == old(list.size()) + 1', 14, 5, null, $spock_condition_throwable)}
    finally {
    }
    try {
        org.spockframework.runtime.SpockRuntime.verifyCondition($spock_errorCollector, $spock_valueRecorder.reset(), 'list.size() == 3', 15, 5, null, $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(5), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(3), $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(0), list).$spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(1), 'size')()) == $spock_valueRecorder.record($spock_valueRecorder.startRecordingValue(4), 3)))
    }
    catch (java.lang.Throwable $spock_condition_throwable) {
        org.spockframework.runtime.SpockRuntime.conditionFailedWithException($spock_errorCollector, $spock_valueRecorder, 'list.size() == 3', 15, 5, null, $spock_condition_throwable)}
    finally {
    }
    this.getSpecificationContext().getMockController().leaveScope()
}
/*--------- end::snapshot[] ---------*/
  }
}
