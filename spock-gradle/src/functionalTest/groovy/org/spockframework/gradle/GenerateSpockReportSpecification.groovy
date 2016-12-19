package org.spockframework.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import spock.lang.Specification


class GenerateSpockReportSpecification extends Specification {

  @Rule
  TemporaryFolder testProjectDir = new TemporaryFolder()

  File buildFile

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
  }

  def "GenerateSpockReport can be configured manually"() {
    given:
    buildFile << '''     
        plugins {
            id 'org.spockframework.base'
        }
        
        task testReport(type: org.spockframework.gradle.GenerateSpockReport) {  
          reportName 'test'
          outputDirectory file("${project.buildDir}/reports/spock/test")  
        }
        '''

    when:
    def result = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withArguments('testReport', '--stacktrace', '--debug')
      .withPluginClasspath()
      .withDebug(true)
      .build()

    then:
    result.output.contains('Hello world!')
    result.task(":testReport").outcome == SUCCESS
  }


  def "base plugin can be applied"() {
    given:
    buildFile << """
        plugins {
            id 'org.spockframework.base'
        }
        
        task test(type: Test) {
          doLast {
            println {systemProperty}
          }
        }
        """

    and:
    testProjectDir.newFile("SpockTestConfig.groovy") << ""

    when:
    def result = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withArguments('test', '--stacktrace', '--debug')
      .withPluginClasspath()
      .withDebug(true)
      .build()

    then:
    result.output.contains('Hello world!')
    result.task(":test").outcome == SUCCESS
  }


  def "report plugin can be applied"() {
    given:
    buildFile << """
        plugins {
            id 'org.spockframework.report'
        }
        
        task test(type: Test) {
          doLast {
            println {systemProperty}
          }
        }
        """

    and:
    testProjectDir.newFile("SpockTestConfig.groovy") << ""

    when:
    def result = GradleRunner.create()
      .withProjectDir(testProjectDir.root)
      .withArguments('test', '--stacktrace', '--debug')
      .withPluginClasspath()
      .withDebug(true)
      .build()

    then:
    result.output.contains('Hello world!')
    result.task(":test").outcome == SUCCESS
    result.task(":testSpockReport").outcome == SUCCESS
  }
}
