package io.github.syamsasi.appium_lab.devicefinder;

import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.model.DeviceModel;
import io.github.syamsasi.appium_lab.constants.AppiumLabConstants;
import io.github.syamsasi.appium_lab.utlity.DeviceUtility;
import org.buildobjects.process.StartupException;
import org.buildobjects.process.TimeoutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Created by Syam Sasi on May, 2018 */
public class IOSDeviceFinder {
  private static final Logger LOGGER = Logger.getLogger(IOSDeviceFinder.class.getName());

  public List<DeviceModel> getAllRealDevices() throws AppiumLabException {
    LOGGER.info("Entering in getAllRealDevices()");
    Map<String, List<DeviceModel>> allIOSDevices = getAllIOSDevices();
    LOGGER.info("allIOSDevices=" + allIOSDevices);
    LOGGER.info("Leaving getAllRealDevices()");
    return allIOSDevices.get(AppiumLabConstants.REAL_DEVICE);
  }

  public List<DeviceModel> getAllVirtualDevices() throws Exception {
    LOGGER.info("Entering in getAllVirtualDevices()");
    Map<String, List<DeviceModel>> allIOSDevices = getAllIOSDevices();
    LOGGER.info("allIOSDevices=" + allIOSDevices);
    LOGGER.info("Leaving getAllVirtualDevices()");
    return allIOSDevices.get(AppiumLabConstants.VIRTUAL_DEVICE);
  }

  public List<DeviceModel> getAllDevices() throws Exception {
    LOGGER.info("Entering in getAllDevices()");
    List<DeviceModel> allDeviceList = new ArrayList<DeviceModel>();
    allDeviceList.addAll(getAllRealDevices());
    allDeviceList.addAll(getAllVirtualDevices());
    LOGGER.info("allDeviceList=" + allDeviceList);
    LOGGER.info("Leaving getAllDevices()");
    return allDeviceList;
  }

  private Map<String, List<DeviceModel>> getAllIOSDevices() throws AppiumLabException {
    LOGGER.info("Entering in getAllIOSDevices()");
    List<DeviceModel> simulatorList = new ArrayList<DeviceModel>();
    List<DeviceModel> realIOSDeviceList = new ArrayList<DeviceModel>();

    String instrumentsLog = null;
    try {
      instrumentsLog = DeviceUtility.exeCommand("instruments", "-s");
    } catch (StartupException e) {
      throw new AppiumLabException("Please install XCODE and verify 'instruments -s' command");
    } catch (TimeoutException e) {
      instrumentsLog = DeviceUtility.exeCommand("instruments", "-s");
    } catch (Exception e) {
      instrumentsLog = DeviceUtility.exeCommand("instruments", "-s");
    }

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

        if (deviceNameTemp.contains("null")) {
          continue;
        }
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
        String deviceName = "";
        try {
          if ((len - 1 <= 0)) {
            continue;
          }
          if (len == 3) {
            deviceName = allInfoArray[0];
          } else {
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

    Map<String, List<DeviceModel>> allIOSDeviceMap = new HashMap<String, List<DeviceModel>>();
    allIOSDeviceMap.put(AppiumLabConstants.REAL_DEVICE, realIOSDeviceList);
    allIOSDeviceMap.put(AppiumLabConstants.VIRTUAL_DEVICE, simulatorList);
    LOGGER.info("allIOSDeviceMap=" + allIOSDeviceMap);
    LOGGER.info("Leaving getAllIOSDevices()");

    return allIOSDeviceMap;
  }
}
