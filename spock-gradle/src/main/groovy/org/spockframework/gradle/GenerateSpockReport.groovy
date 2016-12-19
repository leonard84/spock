/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class GenerateSpockReport extends DefaultTask {
  private Object reportName

  @Input
  String getReportName() {
    reportName
  }

  void setReportName(Object reportName) {
    this.reportName = reportName
  }

  void reportName(Object reportName) {
    setReportName(reportName)
  }

  private String reportFileName

  @Input
  @Optional
  String getReportFileName() {
    reportName
  }

  void setReportFileName(Object reportFileName) {
    this.reportFileName = reportFileName
  }

  void reportFileName(Object reportFileName) {
    setReportName(reportFileName)
  }

  private List<Object> liveLogFiles = []

  @Input
  @Optional
  FileCollection getLiveLogFiles() {
    project.files(this.liveLogFiles)
  }

  void setLiveLogFiles(Object... liveLogFiles) {
    this.liveLogFiles.clear()
    this.liveLogFiles.addAll(liveLogFiles as List)
  }

  void liveLogFiles(Object... liveLogFiles) {
    this.liveLogFiles.addAll(liveLogFiles as List)
  }

  private Object outputDirectory

  @OutputDirectory
  File getOutputDirectory() {
    this.outputDirectory == null ? null : project.file(this.outputDirectory)
  }

  void setOutputDirectory(Object outputDirectory) {
    this.outputDirectory = outputDirectory
  }

  void outputDirectory(Object outputDirectory) {
    setOutputDirectory(outputDirectory)
  }

  @Input
  boolean local = true

  @Input
  boolean debug = true

  @InputFiles
  private  List<Object> spockReportClasspath = []

  FileCollection getSpockReportClasspath() {
    project.files(this.spockReportClasspath)
  }

  void setSpockReportClasspath(Object... spockReportClasspath) {
    this.spockReportClasspath.clear()
    this.spockReportClasspath.addAll(spockReportClasspath as List)
  }

  void spockReportClasspath(Object... spockReportClasspath) {
    this.spockReportClasspath.addAll(spockReportClasspath as List)
  }


  private List<Object>  logFileDirectories = []

  @InputFiles // should really be @InputDirectoryListing
  @Optional
  FileCollection getLogFileDirectories() {
    project.files(this.logFileDirectories)
  }

  void setLogFileDirectories(Object... logFileDirectories) {
    this.logFileDirectories.clear()
    this.logFileDirectories.addAll(logFileDirectories as List)
  }

  void logFileDirectories(Object... logFileDirectories) {
    this.logFileDirectories.addAll(logFileDirectories as List)
  }

  private List<Object> logFiles = []

  @InputFiles
  @Optional
  FileCollection getLogFiles() {
    project.files(this.logFiles)
  }

  void setLogFiles(Object... logFiles) {
    this.logFiles.clear()
    this.logFiles.addAll(logFiles as List)
  }

  void logFiles(Object... logFiles) {
    this.logFiles.addAll(logFiles as List)
  }

  @CompileDynamic
  @TaskAction
  void generate() {
    def classLoader = new URLClassLoader(getSpockReportClasspath().collect { it.toURI().toURL() } as URL[])
    def generator = classLoader.loadClass("org.spockframework.report.HtmlReportGenerator").newInstance()
    generator.reportName = getReportName()
    generator.reportFileName = getReportFileName()
    generator.logFiles = getLogFiles() +
        (getLogFileDirectories() as List).collectMany { (project.fileTree(it) as List) }
    generator.liveLogFiles = getLiveLogFiles()
    generator.outputDirectory = getOutputDirectory()
    generator.local = getLocal()
    generator.debug = isDebug()
    def reportFile = generator.generate()
    def reportUrl = classLoader.loadClass("org.spockframework.util.ConsoleUtil").asClickableFileUrl(reportFile)
    println "Spock report can be viewed at: $reportUrl"
  }
}
