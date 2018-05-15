package io.github.syamsasi.appium_lab.model;

import java.util.Map;

/** Created by Syam Sasi on May, 2018 */
public class ConfigurationModel {

  private String mode;
  private String environment;
  private ParallelNodeDataModel parallelNodeDataModel;
  private DistributedNodeDataModel distributedNodeDataModel;

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

  public ParallelNodeDataModel getParallelNodeDataModel() {
    return parallelNodeDataModel;
  }

  public void setParallelNodeDataModel(ParallelNodeDataModel parallelNodeDataModel) {
    this.parallelNodeDataModel = parallelNodeDataModel;
  }

  public DistributedNodeDataModel getDistributedNodeDataModel() {
    return distributedNodeDataModel;
  }

  public void setDistributedNodeDataModel(DistributedNodeDataModel distributedNodeDataModel) {
    this.distributedNodeDataModel = distributedNodeDataModel;
  }

  @Override
  public String toString() {
    return "ConfigurationModel{" +
            "mode='" + mode + '\'' +
            ", environment='" + environment + '\'' +
            ", parallelNodeDataModel=" + parallelNodeDataModel +
            ", distributedNodeDataModel=" + distributedNodeDataModel +
            '}';
  }
}
