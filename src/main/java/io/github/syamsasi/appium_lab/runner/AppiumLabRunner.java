package io.github.syamsasi.appium_lab.runner;

import io.github.syamsasi.appium_lab.config.ConfigFileReader;
import io.github.syamsasi.appium_lab.devicefinder.AndroidDeviceFinder;
import io.github.syamsasi.appium_lab.devicefinder.IOSDeviceFinder;
import io.github.syamsasi.appium_lab.model.ConfigurationModel;
import io.github.syamsasi.appium_lab.model.DeviceModel;
import io.github.syamsasi.appium_lab.utlity.AppiumLabConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppiumLabRunner {

  public static void main(String[] args) throws Exception {
    String filePath = System.getProperty("user.dir") + "/config.json";
    AppiumLabRunner.runAppiumSessions(AppiumLabConstants.MAVEN, new File(filePath));
  }

  private static void runAppiumSessions(String exeType, File filePath) throws Exception {
    ConfigurationModel configurationModel = ConfigFileReader.readConfigJson(filePath);

    // Identify whether we need to to run android or or iOS or both
    Map<String, List<DeviceModel>> allPlatformDeviceMap = getAllRealDeviceInfo(configurationModel);
    System.out.println("allPlatformDeviceMap=" + allPlatformDeviceMap);
  }

  private static Map<String, List<DeviceModel>> getAllRealDeviceInfo(
      ConfigurationModel configurationModel) throws Exception {
    boolean isIOS = false;
    boolean isAndroid = false;
    if (configurationModel.getMode().equalsIgnoreCase("distributed")) {
      Map<String, List<String>> distMap = configurationModel.getDistributedMap();
      System.out.println("distMap=" + distMap);
      List<String> allNodes = new ArrayList<String>(distMap.keySet());
      for (String node : allNodes) {
        if ((node.startsWith("android") && (!isAndroid))) {
          isAndroid = true;
        } else if ((node.startsWith("ios") && (!isIOS))) {
          isIOS = true;
        }
        if (isAndroid && isIOS) {
          break;
        }
      }

      System.out.println("isIOS=" + isIOS);
      System.out.println("isAndroid=" + isAndroid);

    } else {
      Map<String, String> parlMap = configurationModel.getParallelMap();
      String platform = parlMap.get("platform");
      if (platform.equalsIgnoreCase("android")) {
        isAndroid = true;
        isIOS = false;
      } else if (platform.equalsIgnoreCase("ios")) {
        isAndroid = false;
        isIOS = true;
      } else if (platform.equalsIgnoreCase("both")) {
        isAndroid = true;
        isIOS = true;
      }

      System.out.println("isIOS=" + isIOS);
      System.out.println("isAndroid=" + isAndroid);
    }

    if (!isIOS && !isAndroid) {
      throw new Exception("Incorrect configuration!!!");
    }

    Map<String, List<DeviceModel>> allDeviceMap = new LinkedHashMap<String, List<DeviceModel>>();
    if (isIOS) {
      allDeviceMap.put(AppiumLabConstants.IOS, new IOSDeviceFinder().getAllRealDevices());
    }
    if (isAndroid) {
      allDeviceMap.put(AppiumLabConstants.ANDROID, new AndroidDeviceFinder().getAllRealDevices());
    }
    return allDeviceMap;
  }
}
