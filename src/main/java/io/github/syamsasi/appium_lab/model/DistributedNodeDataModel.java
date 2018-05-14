package io.github.syamsasi.appium_lab.model;

import java.util.Map;

/** Created by Syam Sasi on May, 2018 */
public class DistributedNodeDataModel {

  private Map<String, DistributedAttributeDataModel> androidNodeMap;
  private Map<String, DistributedAttributeDataModel> iOSNodeMap;

  public Map<String, DistributedAttributeDataModel> getAndroidNodeMap() {
    return androidNodeMap;
  }

  public void setAndroidNodeMap(Map<String, DistributedAttributeDataModel> androidNodeMap) {
    this.androidNodeMap = androidNodeMap;
  }

  public Map<String, DistributedAttributeDataModel> getiOSNodeMap() {
    return iOSNodeMap;
  }

  public void setiOSNodeMap(Map<String, DistributedAttributeDataModel> iOSNodeMap) {
    this.iOSNodeMap = iOSNodeMap;
  }

  @Override
  public String toString() {
    return "DistributedNodeDataModel{"
        + "androidNodeMap="
        + androidNodeMap
        + ", iOSNodeMap="
        + iOSNodeMap
        + '}';
  }
}
