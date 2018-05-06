package io.github.syamsasi.appium_lab.devicefinder;

import io.github.syamsasi.appium_lab.model.DeviceModel;
import io.github.syamsasi.appium_lab.utlity.AppiumLabConstants;
import io.github.syamsasi.appium_lab.utlity.DeviceUtility;
import org.buildobjects.process.StartupException;
import org.buildobjects.process.TimeoutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOSDeviceFinder extends BaseDeviceFinder {

  @Override
  public List<DeviceModel> getAllRealDevices() {
    return null;
  }

  @Override
  public List<DeviceModel> getAllVirtualDevices() throws Exception {
    Map<String, List<DeviceModel>> allIOSUdidMap = getAllIOSDevices();
    return null;
  }

  private Map<String, List<DeviceModel>> getAllIOSDevices() throws Exception {

    List<DeviceModel> simulatorList = new ArrayList<DeviceModel>();
    List<DeviceModel> realIOSDeviceList = new ArrayList<DeviceModel>();

    String instrumentsLog = null;
    // TOD: Do the same for adb
    try {
      instrumentsLog = DeviceUtility.exeCommand("instruments", "-s");
      System.out.println("instrumentsLog=" + instrumentsLog);
    } catch (StartupException e) {
      throw new Exception("Please install XCODE and verify 'instruments -s' command");
    } catch (TimeoutException e) {
      instrumentsLog = DeviceUtility.exeCommand("instruments", "-s");
    }

    System.out.println("instrumentsLog=" + instrumentsLog);

    String instrumentsLogStr[] = instrumentsLog.split("\n");
    for (int i = 0; i < instrumentsLogStr.length; i++) {
      String deviceNameTemp = instrumentsLogStr[i];

      if (deviceNameTemp.contains("(Simulator)")) {
        String[] allInfoArray = deviceNameTemp.split(" ");
        int len = allInfoArray.length;
        String deviceName = "";
        for (int j = 0; j < len - 3; j++) {
          deviceName += " " + allInfoArray[j];
        }
        String udid = allInfoArray[allInfoArray.length - 2];
        udid = udid.replace("[", "");
        udid = udid.replace("]", "");
        String osVersion = allInfoArray[allInfoArray.length - 3];
        osVersion = osVersion.replace("(", "");
        osVersion = osVersion.replace(")", "");

        DeviceModel deviceModel = new DeviceModel(udid, "iOS", osVersion, deviceName);
        simulatorList.add(deviceModel);

      } else {
        // realIOSDeviceUdidList.add(androidUdid);
        if (deviceNameTemp.contains("Known Templates")) {
          break;
        }

        if ((deviceNameTemp.contains("MacBook"))) {
          continue;
        }
        if ((deviceNameTemp.contains("Known Devices:"))) {
          continue;
        }

        String[] allInfoArray = deviceNameTemp.split(" ");
        int len = allInfoArray.length;
        System.out.println("len=" + len);
        String deviceName = "";
        try {
          if ((len - 1 <= 0)) {
            continue;
          }
          if(len==3){
            deviceName=allInfoArray[0];
          }
          else{
          for (int j = 0; j < len - 3; j++) {
            if (j < len - 3) {
              deviceName += " " + allInfoArray[j];
            }
          }
          }

          String udid = allInfoArray[allInfoArray.length - 1];
          udid = udid.replace("[", "");
          udid = udid.replace("]", "");
          String osVersion = allInfoArray[allInfoArray.length - 2];
          osVersion = osVersion.replace("(", "");
          osVersion = osVersion.replace(")", "");

          DeviceModel deviceModel = new DeviceModel(udid, "iOS", osVersion, deviceName);
          realIOSDeviceList.add(deviceModel);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    System.out.println("realIOSDeviceList=" + realIOSDeviceList);
    System.out.println("simulatorList=" + simulatorList);


    Map<String, List<DeviceModel>> allIOSDeviceMap = new HashMap<String, List<DeviceModel>>();
    allIOSDeviceMap.put(AppiumLabConstants.REAL_DEVICE, realIOSDeviceList);
    allIOSDeviceMap.put(AppiumLabConstants.VIRTUAL_DEVICE, simulatorList);
    return allIOSDeviceMap;
  }

  @Override
  public List<DeviceModel> getAllDevices() throws Exception {
    return null;
  }

  public static void main(String[] args) {
    try {
      /*
       System.out.println("*******************************************");
       List<DeviceModel> allAndroidRealDeviceList = new AndroidDeviceFinder().getAllRealDevices();
       System.out.println("allAndroidRealDeviceList=" + allAndroidRealDeviceList);
      */
      System.out.println("*******************************************");
      List<DeviceModel> allIOSVirtualDeviceList = new IOSDeviceFinder().getAllVirtualDevices();
      System.out.println("allIOSVirtualDeviceList=" + allIOSVirtualDeviceList);
      System.out.println("*******************************************");
      /*
      List<DeviceModel> allAndroidDeviceList = new AndroidDeviceFinder().getAllDevices();
      System.out.println("allAndroidDeviceList=" + allAndroidDeviceList);
      System.out.println("*******************************************");
      */

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
