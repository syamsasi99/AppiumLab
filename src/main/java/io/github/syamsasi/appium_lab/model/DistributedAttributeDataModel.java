package io.github.syamsasi.appium_lab.model;

import java.util.List;

/** Created by Syam Sasi on May, 2018 */
public class DistributedAttributeDataModel {
  private String udId;
  private List<String> testFiles;
  private List<String> includeTags;
  private List<String> excludeTags;

  public DistributedAttributeDataModel(String udId, List<String> testFiles, List<String> includeTags, List<String> excludeTags) {
    this.udId = udId;
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
    return "DistributedAttributeDataModel{"
        + "udId='"
        + udId
        + '\''
        + ", testFiles="
        + testFiles
        + ", includeTags="
        + includeTags
        + ", excludeTags="
        + excludeTags
        + '}';
  }
}
