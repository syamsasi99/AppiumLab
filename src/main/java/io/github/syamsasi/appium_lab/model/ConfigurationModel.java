package io.github.syamsasi.appium_lab.model;

import java.util.Map;

/** Created by Syam Sasi on May, 2018 */
public class ConfigurationModel {

  private String mode;
  private String environment;
  private Map<String, Map<String, Object>> distributedMap;
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

  public Map<String, Map<String, Object>> getDistributedMap() {
    return distributedMap;
  }

  public void setDistributedMap(Map<String, Map<String, Object>> distributedMap) {
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
    return "ConfigurationModel{" +
            "mode='" + mode + '\'' +
            ", environment='" + environment + '\'' +
            ", distributedMap=" + distributedMap +
            ", parallelMap=" + parallelMap +
            '}';
  }
}
