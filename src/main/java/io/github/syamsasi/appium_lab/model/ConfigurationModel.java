package io.github.syamsasi.appium_lab.model;

import java.util.List;
import java.util.Map;

public class ConfigurationModel {

  private String mode;
  private String environment;
  private Map<String, List<String>> distributedMap;
  private Map<String, String> parallelMap;

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public Map<String, List<String>> getDistributedMap() {
    return distributedMap;
  }

  public void setDistributedMap(Map<String, List<String>> distributedMap) {
    this.distributedMap = distributedMap;
  }

  public Map<String, String> getParallelMap() {
    return parallelMap;
  }

  public void setParallelMap(Map<String, String> parallelMap) {
    this.parallelMap = parallelMap;
  }

  @Override
  public String toString() {
    return String.format(
        "mode: "
            + mode
            + " - "
            + "environment: "
            + environment
            + " - "
            + "distributedMap: "
            + distributedMap
            + " - "
            + "parallelMap: "
            + parallelMap);
  }
}
