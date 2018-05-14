package io.github.syamsasi.appium_lab.runner;

import io.github.syamsasi.appium_lab.config.ConfigFileReader;
import io.github.syamsasi.appium_lab.constants.ConfigElement;
import io.github.syamsasi.appium_lab.constants.DeviceType;
import io.github.syamsasi.appium_lab.constants.Platform;
import io.github.syamsasi.appium_lab.constants.RunType;
import io.github.syamsasi.appium_lab.devicefinder.AndroidDeviceFinder;
import io.github.syamsasi.appium_lab.devicefinder.IOSDeviceFinder;
import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.model.BuildModel;
import io.github.syamsasi.appium_lab.model.ConfigurationModel;
import io.github.syamsasi.appium_lab.model.DeviceModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Created by Syam Sasi on May, 2018 */
public class AppiumLabRunner {
  private static final Logger LOGGER = Logger.getLogger(AppiumLabRunner.class.getName());

  private static ArrayList<Integer> allPortList;

  /**
   * Returns all the attributes needed for parallel/ditributed run
   *
   * @param configFile -> The config json file
   * @return buildAttributesMap
   * @throws Exception
   */
  public static Map<String, Object> getAllBuildAttributesFromConfigFile(File configFile)
      throws AppiumLabException {
    LOGGER.info("Entering getAllBuildAttributesFromConfigFile()");
    allPortList = new ArrayList<Integer>();
    ConfigurationModel configurationModel = ConfigFileReader.readConfigJson(configFile);
    LOGGER.info("configurationModel="+configurationModel);
    Map<String, List<DeviceModel>> allPlatformDeviceMap = getAllRealDeviceInfo(configurationModel);
    LOGGER.info("allPlatformDeviceMap="+allPlatformDeviceMap);
    checkAllRealDevicesAreConnected(configurationModel, allPlatformDeviceMap);
    Map<String, Object> buildAttributesMap =
        generateBuildCommands(configurationModel, allPlatformDeviceMap);
    LOGGER.info("buildAttributesMap="+buildAttributesMap);
    LOGGER.info("Exiting getAllBuildAttributesFromConfigFile()");

    return buildAttributesMap;
  }

  /**
   * Returns all the attributes needed for parallel/ditributed run
   *
   * @param configurationModel -> The config json file
   * @return buildAttributesMap
   * @throws Exception
   */
  public static Map<String, Object> getAllBuildAttributesFromConfigModel( ConfigurationModel configurationModel)
          throws AppiumLabException {
    LOGGER.info("Entering getAllBuildAttributesFromConfigModel()");
    allPortList = new ArrayList<Integer>();
    LOGGER.info("configurationModel="+configurationModel);
    Map<String, List<DeviceModel>> allPlatformDeviceMap = getAllRealDeviceInfo(configurationModel);
    LOGGER.info("allPlatformDeviceMap="+allPlatformDeviceMap);
    checkAllRealDevicesAreConnected(configurationModel, allPlatformDeviceMap);
    Map<String, Object> buildAttributesMap =
            generateBuildCommands(configurationModel, allPlatformDeviceMap);
    LOGGER.info("buildAttributesMap="+buildAttributesMap);
    LOGGER.info("Exiting getAllBuildAttributesFromConfigFile()");

    return buildAttributesMap;
  }


  private static Map<String, Object> generateBuildCommands(
      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)
      throws AppiumLabException {

    Map<String, Object> deviceSpecificData = new LinkedHashMap<String, Object>();

    if (configurationModel.getMode().equalsIgnoreCase(RunType.DISTRIBUTED.name())) {
      List<String> allNodes = getAllDistributionNodes(configurationModel);
      Map<String, Map<String, Object>> distMap = configurationModel.getDistributedMap();

      for (String node : allNodes) {

        String deviceType = null;
        if ((node.toLowerCase().startsWith(Platform.ANDROID.name().toLowerCase()))) {
          deviceType = DeviceType.ANDROID_REAL_DEVICE.name();
        } else if ((node.toLowerCase().startsWith(Platform.IOS.name().toLowerCase()))) {
          deviceType = DeviceType.IOS_REAL_DEVICE.name();
        }

        List<DeviceModel> allAndroidDeviceModelList = allPlatformDeviceMap.get(deviceType);

        String udId = getUdidFromConfigNode(node, distMap);
        String environment = configurationModel.getEnvironment();
        List<String> testFilesList = getTestFilesFromConfigNode(node, distMap);
        List<String> includeTagList = getIncludeTagsFromConfigNode(node, distMap);
        List<String> excludeTagList = getExcludeTagsFromConfigNode(node, distMap);
        String platformName = getPlatformNameFromUdid(udId, allAndroidDeviceModelList);
        String platformVersion = getPlatformVersionFromUdid(udId, allAndroidDeviceModelList);

        int appiumPort = generateUniquePort(udId);
        appiumPort = getNewPortIfTaken(appiumPort);
        int systemPort = appiumPort + 1;
        systemPort = getNewPortIfTaken(systemPort);
        int wdaPort = systemPort + 1;
        wdaPort = getNewPortIfTaken(wdaPort);

        BuildModel buildModel =
            new BuildModel(
                udId,
                platformName,
                platformVersion,
                appiumPort,
                systemPort,
                wdaPort,
                RunType.DISTRIBUTED.name().toLowerCase(),
                configurationModel.getEnvironment(),
                testFilesList,
                includeTagList,
                excludeTagList);

        deviceSpecificData.put(udId, buildModel);
      }

    } else if (configurationModel.getMode().equalsIgnoreCase(RunType.PARALLEL.name())) {
      Map<String, String> parallelDataMap = configurationModel.getParallelMap();
      List<String> testFilesList =
          getParallelDataList(parallelDataMap, ConfigElement.FEATURE.name().toLowerCase());
      List<String> includeTagList =
          getParallelDataList(parallelDataMap, ConfigElement.INCLUDE.name().toLowerCase());
      List<String> excludeTagList =
          getParallelDataList(parallelDataMap, ConfigElement.EXCLUDE.name().toLowerCase());

      String runningPlatform = parallelDataMap.get(ConfigElement.PLATFORM.name().toLowerCase());

      if (runningPlatform == null) {
        throw new AppiumLabException(
            "The platform configuration " + runningPlatform + " is invalid!!");
      }
      List<DeviceModel> allAndroidDeviceList = null;
      List<DeviceModel> allIOSDeviceList = null;
      if (runningPlatform.equalsIgnoreCase(Platform.ANDROID.name())) {
        allAndroidDeviceList = allPlatformDeviceMap.get(DeviceType.ANDROID_REAL_DEVICE.name());
        if(allAndroidDeviceList==null || allAndroidDeviceList.size()==0){
          throw new AppiumLabException("No Android devices are connected!!");
        }
      } else if (runningPlatform.equalsIgnoreCase(Platform.IOS.name())) {
        allIOSDeviceList = allPlatformDeviceMap.get(DeviceType.IOS_REAL_DEVICE.name());
        if(allIOSDeviceList==null || allIOSDeviceList.size()==0){
          throw new AppiumLabException("No iOS devices are connected!!");
        }
      } else if (runningPlatform.equalsIgnoreCase(Platform.BOTH.name())) {
        allAndroidDeviceList = allPlatformDeviceMap.get(DeviceType.ANDROID_REAL_DEVICE.name());
        allIOSDeviceList = allPlatformDeviceMap.get(DeviceType.IOS_REAL_DEVICE.name());
        if(allAndroidDeviceList.size()==0 && allIOSDeviceList.size()==0){
          throw new AppiumLabException("Neither iOS nor android devices are connected!!");
        }
      } else {
        throw new AppiumLabException(
            "The platform configuration "
                + runningPlatform
                + " is invalid!!, The valid "
                + "values are "
                + Platform.ANDROID.name().toLowerCase()
                + " or "
                + Platform.IOS.name().toLowerCase()
                + " or "
                + Platform.BOTH.name().toLowerCase());
      }


      if (allAndroidDeviceList != null) {
        for (DeviceModel deviceModel : allAndroidDeviceList) {
          BuildModel buildModel =
              getNewBuildModel(
                  deviceModel,
                  configurationModel.getEnvironment(),
                  testFilesList,
                  includeTagList,
                  excludeTagList,
                  RunType.PARALLEL.name().toLowerCase());
          deviceSpecificData.put(deviceModel.getUdid(), buildModel);
        }
      }
      if (allIOSDeviceList != null) {
        for (DeviceModel deviceModel : allIOSDeviceList) {

          BuildModel buildModel =
              getNewBuildModel(
                  deviceModel,
                  configurationModel.getEnvironment(),
                  testFilesList,
                  includeTagList,
                  excludeTagList,
                  RunType.PARALLEL.name().toLowerCase());
          deviceSpecificData.put(deviceModel.getUdid(), buildModel);
        }
      }
    }

    return deviceSpecificData;
  }

  private static BuildModel getNewBuildModel(
      DeviceModel deviceModel,
      String environment,
      List<String> testFilesList,
      List<String> includeTagList,
      List<String> excludeTagList,
      String runType) {

    String udid = deviceModel.getUdid();
    String platformVersion = deviceModel.getOsVersion();
    String platformName = deviceModel.getPlatformName();

    int appiumPort = generateUniquePort(udid);
    appiumPort = getNewPortIfTaken(appiumPort);
    int systemPort = appiumPort + 1;
    systemPort = getNewPortIfTaken(systemPort);
    int wdaPort = systemPort + 1;
    wdaPort = getNewPortIfTaken(wdaPort);

    BuildModel buildModel =
        new BuildModel(
            udid,
            platformName,
            platformVersion,
            appiumPort,
            systemPort,
            wdaPort,
            runType,
            environment,
            testFilesList,
            includeTagList,
            excludeTagList);

    return buildModel;
  }

  private static List<String> getParallelDataList(
      Map<String, String> parallelDataMap, String attribute) throws AppiumLabException {
    String dataWithComma = parallelDataMap.get(ConfigElement.FEATURE.name().toLowerCase());
    String[] dataCommaStrArr = null;
    try {
      String allTestsSeparatedByComma = parallelDataMap.get(attribute);
      dataCommaStrArr = allTestsSeparatedByComma.split(",");
    } catch (Exception e) {
      throw new AppiumLabException("The attribute " + attribute + " not found!!");
    }
    return Arrays.asList(dataCommaStrArr);
  }

  private static String getEnvironmentFromConfigNode(
      String node, Map<String, Map<String, Object>> distMap) {
    Map<String, Object> deviceConfigMap = distMap.get(node);
    return (String) deviceConfigMap.get(ConfigElement.ENVIRONMENT.name().toLowerCase());
  }

  private static List<String> getIncludeTagsFromConfigNode(
      String node, Map<String, Map<String, Object>> distMap) throws AppiumLabException {

    return getNodeAttributeAsAList(node, distMap, ConfigElement.INCLUDE.name().toLowerCase());
  }

  private static List<String> getExcludeTagsFromConfigNode(
      String node, Map<String, Map<String, Object>> distMap) throws AppiumLabException {

    return getNodeAttributeAsAList(node, distMap, ConfigElement.EXCLUDE.name().toLowerCase());
  }

  private static List<String> getNodeAttributeAsAList(
      String node, Map<String, Map<String, Object>> distMap, String attribute)
      throws AppiumLabException {
    String[] allTestsSeparatedByCommaArray = null;
    try {
      Map<String, Object> deviceConfigMap = distMap.get(node);
      String allTestsSeparatedByComma = (String) deviceConfigMap.get(attribute);
      allTestsSeparatedByCommaArray = allTestsSeparatedByComma.split(",");
    } catch (Exception e) {
      throw new AppiumLabException("The attribute " + attribute + " not found!!");
    }
    return Arrays.asList(allTestsSeparatedByCommaArray);
  }

  private static List<String> getTestFilesFromConfigNode(
      String node, Map<String, Map<String, Object>> distMap) throws AppiumLabException {

    return getNodeAttributeAsAList(node, distMap, ConfigElement.FEATURE.name().toLowerCase());
  }

  private static int getNewPortIfTaken(int appiumPort) {

    while ((isPortInTheList(appiumPort, allPortList))) {
      appiumPort++;
    }
    allPortList.add(appiumPort);
    return appiumPort;
  }

  private static boolean isPortInTheList(int appiumPort, ArrayList<Integer> allPortList) {
    for (int port : allPortList) {
      if (port == appiumPort) {
        return true;
      }
    }
    return false;
  }

  private static int generateUniquePort(String udId) {

    int hashCode = Math.abs(udId.hashCode());
    String first4char = String.valueOf(hashCode).substring(0, 4);
    int port = Integer.parseInt(first4char);
    return port;
  }

  private static String getPlatformNameFromUdid(String udId, List<DeviceModel> allDeviceModelList)
      throws AppiumLabException {
    for (DeviceModel model : allDeviceModelList) {
      String platformName = null;
      if (model.getUdid().equalsIgnoreCase(udId)) {
        platformName = model.getPlatformName();
        return platformName;
      }
    }
    throw new AppiumLabException("Could not find the platform name with udid " + udId);
  }

  private static String getPlatformVersionFromUdid(
      String udId, List<DeviceModel> allDeviceModelList) throws AppiumLabException {
    for (DeviceModel model : allDeviceModelList) {
      String platformVersion = null;
      if (model.getUdid().equalsIgnoreCase(udId)) {
        platformVersion = model.getOsVersion();
        return platformVersion;
      }
    }
    throw new AppiumLabException("Could not find the platform version with udid " + udId);
  }

  private static List<String> getAllDistributionNodes(ConfigurationModel configurationModel) {
    Map<String, Map<String, Object>> distMap = configurationModel.getDistributedMap();
    return new ArrayList<String>(distMap.keySet());
  }

  private static void checkAllRealDevicesAreConnected(
      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)
      throws AppiumLabException {
    if (configurationModel.getMode().equalsIgnoreCase(RunType.DISTRIBUTED.name())) {
      Map<String, Map<String, Object>> distMap = configurationModel.getDistributedMap();
      List<String> allNodes = new ArrayList<String>(distMap.keySet());
      for (String node : allNodes) {
        if ((node.toLowerCase().startsWith(Platform.ANDROID.name().toLowerCase()))) {

          String udidFromConfigFile = getUdidFromConfigNode(node, distMap);

          if (!isTheDevicePresent(
              node, allPlatformDeviceMap, distMap, DeviceType.ANDROID_REAL_DEVICE.name())) {
            throw new AppiumLabException(
                "The android device with udid " + udidFromConfigFile + " is not connected!!");
          }

        } else if ((node.toLowerCase().startsWith(Platform.IOS.name().toLowerCase()))) {

          String udidFromConfigFile = getUdidFromConfigNode(node, distMap);
          if (!isTheDevicePresent(
              node, allPlatformDeviceMap, distMap, DeviceType.IOS_REAL_DEVICE.name())) {
            throw new AppiumLabException(
                "The iOS device with udid " + udidFromConfigFile + " is not connected!!");
          }
        }
      }
    }
  }

  private static boolean isTheDevicePresent(
      String node,
      Map<String, List<DeviceModel>> allPlatformDeviceMap,
      Map<String, Map<String, Object>> distMap,
      String deviceType) {
    String udidFromConfigFile = getUdidFromConfigNode(node, distMap);
    List<DeviceModel> allAndroidDeviceModelList = allPlatformDeviceMap.get(deviceType);
    boolean udidPresent = isUdidConstainsInList(udidFromConfigFile, allAndroidDeviceModelList);
    return udidPresent;
  }

  private static String getUdidFromConfigNode(
      String node, Map<String, Map<String, Object>> distMap) {
    Map<String, Object> deviceConfigMap = distMap.get(node);
    return (String) deviceConfigMap.get(ConfigElement.UDID.name().toLowerCase());
  }

  private static boolean isUdidConstainsInList(
      String udidFromConfigFile, List<DeviceModel> allAndroidDeviceModelList) {
    for (DeviceModel deviceModel : allAndroidDeviceModelList) {
      if (deviceModel.getUdid().equalsIgnoreCase(udidFromConfigFile)) {
        return true;
      }
    }
    return false;
  }

  private static Map<String, List<DeviceModel>> getAllRealDeviceInfo(
      ConfigurationModel configurationModel) throws AppiumLabException {
    boolean isIOS = false;
    boolean isAndroid = false;
    if (configurationModel.getMode().equalsIgnoreCase(RunType.DISTRIBUTED.name().toLowerCase())) {
      List<String> allNodes = getAllDistributionNodes(configurationModel);
      for (String node : allNodes) {
        if ((node.toLowerCase().startsWith(Platform.ANDROID.name().toLowerCase()))
            && (!isAndroid)) {
          isAndroid = true;
        } else if ((node.toLowerCase().startsWith(Platform.IOS.name().toLowerCase())) && (!isIOS)) {
          isIOS = true;
        }
        if (isAndroid && isIOS) {
          break;
        }
      }

    } else if (configurationModel
        .getMode()
        .equalsIgnoreCase(RunType.PARALLEL.name().toLowerCase())) {
      Map<String, String> parlMap = configurationModel.getParallelMap();
      String platform = parlMap.get(ConfigElement.PLATFORM.name().toLowerCase());
      if (platform == null) {
        throw new AppiumLabException("The platform configuration " + platform + " is invalid!!");
      }
      if (platform.equalsIgnoreCase(Platform.ANDROID.name())) {
        isAndroid = true;
        isIOS = false;
      } else if (platform.equalsIgnoreCase(Platform.IOS.name())) {
        isAndroid = false;
        isIOS = true;
      } else if (platform.equalsIgnoreCase(Platform.BOTH.name())) {
        isAndroid = true;
        isIOS = true;
      } else {
        throw new AppiumLabException(
            "The platform configuration "
                + platform
                + " is invalid!!, The valid "
                + "values are "
                + Platform.ANDROID.name().toLowerCase()
                + " or "
                + Platform.IOS.name().toLowerCase()
                + " or "
                + Platform.BOTH.name().toLowerCase());
      }
    }

    if (!isIOS && !isAndroid) {
      throw new AppiumLabException("Incorrect configuration!!!");
    }

    Map<String, List<DeviceModel>> allDeviceMap = new LinkedHashMap<String, List<DeviceModel>>();
    if (isIOS) {
      allDeviceMap.put(
          DeviceType.IOS_REAL_DEVICE.name(), new IOSDeviceFinder().getAllRealDevices());
    }

    if (isAndroid) {
      allDeviceMap.put(
          DeviceType.ANDROID_REAL_DEVICE.name(), new AndroidDeviceFinder().getAllRealDevices());
    }
    return allDeviceMap;
  }
}
