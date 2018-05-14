package io.github.syamsasi.appium_lab.model;

import java.util.List;
import java.util.Map;

/** Created by Syam Sasi on May, 2018 */
public class ParallelNodeDataModel {

  private List<String> testFiles;
  private List<String> includeTags;
  private List<String> excludeTags;
  private String platformType;

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

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }
}
