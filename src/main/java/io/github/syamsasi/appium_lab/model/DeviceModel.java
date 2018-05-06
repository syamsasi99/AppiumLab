package io.github.syamsasi.appium_lab.model;

public class DeviceModel {

  String udid;
  String platformName;
  String osVersion;
  String manufacture;

  public DeviceModel(String udid, String platformName, String platformVersion, String manufacture){
      this.udid=udid;
      this.platformName=platformName;
      this.osVersion =platformVersion;
      this.manufacture=manufacture;
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

  public String getManufacture() {
    return manufacture;
  }

  public void setManufacture(String manufacture) {
    this.manufacture = manufacture;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

    @Override
    public String toString() {
        return String.format("udid: "+udid+" - "+"platformName: "+platformName+" - "+"osVersion: "+osVersion+" - "+"manufacture: "+manufacture);
    }
}
