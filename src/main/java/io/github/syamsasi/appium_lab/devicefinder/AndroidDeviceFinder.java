package io.github.syamsasi.appium_lab.devicefinder;

import io.github.syamsasi.appium_lab.model.DeviceModel;
import io.github.syamsasi.appium_lab.utlity.AppiumLabConstants;
import io.github.syamsasi.appium_lab.utlity.DeviceUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidDeviceFinder extends BaseDeviceFinder {

  @Override
  public List<DeviceModel> getAllRealDevices() throws Exception {
    Map<String, List<String>> allAndroidUdidMap = getAllAndroidUdids();
    List<String> realDeviceAndroidUdidList = allAndroidUdidMap.get(AppiumLabConstants.REAL_DEVICE);

    return getDeviceInfoListFromUdidList(realDeviceAndroidUdidList);
  }

  @Override
  public List<DeviceModel> getAllVirtualDevices() throws Exception {
    Map<String, List<String>> allAndroidUdidMap = getAllAndroidUdids();
    List<String> virtualDeviceAndroidUdidList =
        allAndroidUdidMap.get(AppiumLabConstants.VIRTUAL_DEVICE);

    return getDeviceInfoListFromUdidList(virtualDeviceAndroidUdidList);
  }

  private List<DeviceModel> getDeviceInfoListFromUdidList(List<String> udidList) {

    List<DeviceModel> allDeviceList = new ArrayList<DeviceModel>();
    for (String udid : udidList) {

      String androidOsVersion =
          DeviceUtility.exeCommand(
              AppiumLabConstants.ADB, "-s", udid, "shell", "getprop", "ro.build.version.release");

      String manufacture =
          DeviceUtility.exeCommand(
              AppiumLabConstants.ADB, "-s", udid, "shell", "getprop", "ro.product.model");

      DeviceModel deviceModel = new DeviceModel(udid, "Android", androidOsVersion, manufacture);
      allDeviceList.add(deviceModel);
    }

    return allDeviceList;
  }

  private Map<String, List<String>> getAllAndroidUdids() throws Exception {
    List<String> emulatorUdidList = new ArrayList<String>();
    List<String> realAndroidDeviceUdidList = new ArrayList<String>();

    String adbLog = DeviceUtility.exeCommand(AppiumLabConstants.ADB, AppiumLabConstants.DEVICES);

    if (adbLog == null) {
      throw new Exception("The adb command is not working");
    }
    String adbStr[] = adbLog.split("\n");
    if (adbStr.length == 1) {
      throw new Exception("Could not find any android devices");
    } else {
      for (int i = 1; i < adbStr.length; i++) {
        String deviceNameTemp = adbStr[i];
        String[] deviceNameStr = deviceNameTemp.split("device");
        String deviceNameWithSpace = deviceNameStr[0];
        String androidUdid = deviceNameWithSpace.trim();
        if (androidUdid.contains(AppiumLabConstants.EMULATOR)) {
          emulatorUdidList.add(androidUdid);
        } else {
          realAndroidDeviceUdidList.add(androidUdid);
        }
      }
    }
    Map<String, List<String>> allAndroidUdidMap = new HashMap<String, List<String>>();
    allAndroidUdidMap.put(AppiumLabConstants.REAL_DEVICE, realAndroidDeviceUdidList);
    allAndroidUdidMap.put(AppiumLabConstants.VIRTUAL_DEVICE, emulatorUdidList);
    return allAndroidUdidMap;
  }

  @Override
  public List<DeviceModel> getAllDevices() throws Exception {

    List<DeviceModel> allRealDeviceList = new ArrayList<DeviceModel>();
    Map<String, List<String>> allAndroidUdidMap = getAllAndroidUdids();
    System.out.println("allAndroidUdidMap=" + allAndroidUdidMap);

    return null;
  }

  public static void main(String[] args) {
    try {
      System.out.println("*******************************************");
      List<DeviceModel> allAndroidRealDeviceList = new AndroidDeviceFinder().getAllRealDevices();
      System.out.println("allAndroidRealDeviceList=" + allAndroidRealDeviceList);
      System.out.println("*******************************************");
      List<DeviceModel> allAndroidVirtualDeviceList =
          new AndroidDeviceFinder().getAllVirtualDevices();
      System.out.println("allAndroidVirtualDeviceList=" + allAndroidVirtualDeviceList);
      System.out.println("*******************************************");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}