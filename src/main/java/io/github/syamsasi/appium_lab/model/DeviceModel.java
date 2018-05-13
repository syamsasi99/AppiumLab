package io.github.syamsasi.appium_lab.model;

/** Created by Syam Sasi on May, 2018 */
public class DeviceModel {

  String udid;
  String platformName;
  String osVersion;
  String deviceName;

  public DeviceModel(String udid, String platformName, String platformVersion, String deviceName) {
    this.udid = udid;
    this.platformName = platformName;
    this.osVersion = platformVersion;
    this.deviceName = deviceName;
  }

  public String getUdid() {
    return udid;
  }

  public void setUdid(String udid) {
    this.udid = udid;
  }

  public String getPlatformName() {
    return platformName;
  }

  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }


  @Override
  public String toString() {
    return "DeviceModel{" +
            "udid='" + udid + '\'' +
            ", platformName='" + platformName + '\'' +
            ", osVersion='" + osVersion + '\'' +
            ", deviceName='" + deviceName + '\'' +
            '}';
  }
}
