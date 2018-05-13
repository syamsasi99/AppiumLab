package io.github.syamsasi.appium_lab.model;

import java.util.List;

/** Created by Syam Sasi on May, 2018 */
public class BuildModel {
  private String udId;
  private String platformName;
  private String platformVersion;
  private int appiumPort;
  private int systemPort;
  private int wdaPort;
  private String runningMode;
  private String environment;
  private List<String> testFiles;
  private List<String> includeTags;
  private List<String> excludeTags;

  public BuildModel() {}

  public BuildModel(
      String udId,
      String platformName,
      String platformVersion,
      int appiumPort,
      int systemPort,
      int wdaPort,
      String runningMode,
      String environment,
      List<String> testFiles,
      List<String> includeTags,
      List<String> excludeTags) {
    this.udId = udId;
    this.platformName = platformName;
    this.platformVersion = platformVersion;
    this.appiumPort = appiumPort;
    this.systemPort = systemPort;
    this.wdaPort = wdaPort;
    this.runningMode = runningMode;
    this.environment = environment;
    this.testFiles = testFiles;
    this.includeTags = includeTags;
    this.excludeTags = excludeTags;
  }

  public String getUdId() {
    return udId;
  }

  public void setUdId(String udId) {
    this.udId = udId;
  }

  public String getPlatformName() {
    return platformName;
  }

  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }

  public String getPlatformVersion() {
    return platformVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  public int getAppiumPort() {
    return appiumPort;
  }

  public void setAppiumPort(int appiumPort) {
    this.appiumPort = appiumPort;
  }

  public int getSystemPort() {
    return systemPort;
  }

  public void setSystemPort(int systemPort) {
    this.systemPort = systemPort;
  }

  public int getWdaPort() {
    return wdaPort;
  }

  public void setWdaPort(int wdaPort) {
    this.wdaPort = wdaPort;
  }

  public String getRunningMode() {
    return runningMode;
  }

  public void setRunningMode(String runningMode) {
    this.runningMode = runningMode;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public List<String> getTestFiles() {
    return testFiles;
  }

  public void setTestFiles(List<String> testFiles) {
    this.testFiles = testFiles;
  }

  public List<String> getIncludeTags() {
    return includeTags;
  }

  public void setIncludeTags(List<String> includeTags) {
    this.includeTags = includeTags;
  }

  public List<String> getExcludeTags() {
    return excludeTags;
  }

  public void setExcludeTags(List<String> excludeTags) {
    this.excludeTags = excludeTags;
  }

    @Override
    public String toString() {
        return "BuildModel{" +
                "udId='" + udId + '\'' +
                ", platformName='" + platformName + '\'' +
                ", platformVersion='" + platformVersion + '\'' +
                ", appiumPort=" + appiumPort +
                ", systemPort=" + systemPort +
                ", wdaPort=" + wdaPort +
                ", runningMode='" + runningMode + '\'' +
                ", environment='" + environment + '\'' +
                ", testFiles=" + testFiles +
                ", includeTags=" + includeTags +
                ", excludeTags=" + excludeTags +
                '}';
    }
}
