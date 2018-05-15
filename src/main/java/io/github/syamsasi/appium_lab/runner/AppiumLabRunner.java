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
import io.github.syamsasi.appium_lab.model.DistributedAttributeDataModel;
import io.github.syamsasi.appium_lab.model.DistributedNodeDataModel;
import io.github.syamsasi.appium_lab.model.ParallelNodeDataModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
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
    LogManager.getLogManager().reset();

    LOGGER.info("Entering in getAllBuildAttributesFromConfigFile(File configFile)");
    LOGGER.info("configFile=" + configFile);
    allPortList = new ArrayList<Integer>();
    ConfigurationModel configurationModel = ConfigFileReader.readConfigJson(configFile);
    Map<String, List<DeviceModel>> allRealDeviceMap = getAllRealDeviceInfo(configurationModel);
    checkAllRealDevicesAreConnected(configurationModel, allRealDeviceMap);
    Map<String, Object> buildAttributesMap =
        generateBuildCommands(configurationModel, allRealDeviceMap);
    LOGGER.info("buildAttributesMap=" + buildAttributesMap);
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
  public static Map<String, Object> getAllBuildAttributesFromConfigModel(
      ConfigurationModel configurationModel) throws AppiumLabException {
    LogManager.getLogManager().reset();

    LOGGER.info(
        "Entering in getAllBuildAttributesFromConfigModel(ConfigurationModel configurationModel)");
    LOGGER.info("configurationModel=" + configurationModel);

    allPortList = new ArrayList<Integer>();
    Map<String, List<DeviceModel>> allPlatformDeviceMap = getAllRealDeviceInfo(configurationModel);
    LOGGER.info("allPlatformDeviceMap=" + allPlatformDeviceMap);
    checkAllRealDevicesAreConnected(configurationModel, allPlatformDeviceMap);
    Map<String, Object> buildAttributesMap =
        generateBuildCommands(configurationModel, allPlatformDeviceMap);
    LOGGER.info("buildAttributesMap=" + buildAttributesMap);
    LOGGER.info("Exiting getAllBuildAttributesFromConfigFile()");

    return buildAttributesMap;
  }

  private static Map<String, Object> generateBuildCommands(
      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)
      throws AppiumLabException {
    LOGGER.info(
        "Entering in generateBuildCommands(\n"
            + "      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)");

    LOGGER.info("configurationModel=" + configurationModel);
    LOGGER.info("allPlatformDeviceMap=" + allPlatformDeviceMap);
    Map<String, Object> deviceSpecificData = new LinkedHashMap<String, Object>();

    if (configurationModel.getMode().equalsIgnoreCase(RunType.DISTRIBUTED.name())) {
      DistributedNodeDataModel distributedNodeDataModel =
          configurationModel.getDistributedNodeDataModel();
      Map<String, DistributedAttributeDataModel> androidNodeMap =
          distributedNodeDataModel.getAndroidNodeMap();
      Map<String, DistributedAttributeDataModel> iOSNodeMap =
          distributedNodeDataModel.getiOSNodeMap();

      for (Map.Entry<String, DistributedAttributeDataModel> entry : androidNodeMap.entrySet()) {
        System.out.println(entry.getKey() + "/" + entry.getValue());
        DistributedAttributeDataModel distributedAttributeDataModel = entry.getValue();
        List<DeviceModel> allAndroidDeviceModelList =
            allPlatformDeviceMap.get(DeviceType.ANDROID_REAL_DEVICE.name());
        String udId = distributedAttributeDataModel.getUdId();
        List<String> testFilesList = distributedAttributeDataModel.getTestFiles();
        List<String> includeTagList = distributedAttributeDataModel.getIncludeTags();
        List<String> excludeTagList = distributedAttributeDataModel.getExcludeTags();
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

      ParallelNodeDataModel parallelNodeDataModel = configurationModel.getParallelNodeDataModel();
      List<String> testFilesList = parallelNodeDataModel.getTestFiles();
      List<String> includeTagList = parallelNodeDataModel.getIncludeTags();
      List<String> excludeTagList = parallelNodeDataModel.getExcludeTags();

      String runningPlatform = parallelNodeDataModel.getPlatformType();

      List<DeviceModel> allAndroidDeviceList = null;
      List<DeviceModel> allIOSDeviceList = null;

      if (runningPlatform.equalsIgnoreCase(Platform.ANDROID.name())) {
        allAndroidDeviceList = allPlatformDeviceMap.get(DeviceType.ANDROID_REAL_DEVICE.name());
        if (allAndroidDeviceList == null || allAndroidDeviceList.size() == 0) {
          throw new AppiumLabException("No Android devices are connected!!");
        }
      } else if (runningPlatform.equalsIgnoreCase(Platform.IOS.name())) {
        allIOSDeviceList = allPlatformDeviceMap.get(DeviceType.IOS_REAL_DEVICE.name());
        if (allIOSDeviceList == null || allIOSDeviceList.size() == 0) {
          throw new AppiumLabException("No iOS devices are connected!!");
        }
      } else if (runningPlatform.equalsIgnoreCase(Platform.BOTH.name())) {
        allAndroidDeviceList = allPlatformDeviceMap.get(DeviceType.ANDROID_REAL_DEVICE.name());
        allIOSDeviceList = allPlatformDeviceMap.get(DeviceType.IOS_REAL_DEVICE.name());
        if (allAndroidDeviceList.size() == 0 && allIOSDeviceList.size() == 0) {
          throw new AppiumLabException("Neither iOS nor android devices are connected!!");
        }
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
    LOGGER.info("deviceSpecificData=" + deviceSpecificData);
    LOGGER.info(
        "Leaving generateBuildCommands(\n"
            + "      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)");

    return deviceSpecificData;
  }

  private static BuildModel getNewBuildModel(
      DeviceModel deviceModel,
      String environment,
      List<String> testFilesList,
      List<String> includeTagList,
      List<String> excludeTagList,
      String runType) {

    LOGGER.info(
        "Entering in getNewBuildModel(\n"
            + "      DeviceModel deviceModel,\n"
            + "      String environment,\n"
            + "      List<String> testFilesList,\n"
            + "      List<String> includeTagList,\n"
            + "      List<String> excludeTagList,\n"
            + "      String runType)");
    LOGGER.info("deviceModel=" + deviceModel);
    LOGGER.info("environment=" + environment);
    LOGGER.info("testFilesList=" + testFilesList);
    LOGGER.info("includeTagList=" + includeTagList);
    LOGGER.info("excludeTagList=" + excludeTagList);
    LOGGER.info("runType=" + runType);

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
    LOGGER.info(
        "Leaving getNewBuildModel(\n"
            + "      DeviceModel deviceModel,\n"
            + "      String environment,\n"
            + "      List<String> testFilesList,\n"
            + "      List<String> includeTagList,\n"
            + "      List<String> excludeTagList,\n"
            + "      String runType)");
    LOGGER.info("buildModel=" + buildModel);
    return buildModel;
  }

  private static List<String> getParallelDataList(
      Map<String, String> parallelDataMap, String attribute) throws AppiumLabException {
    LOGGER.info(
        "Entering in getParallelDataList(\n"
            + "      Map<String, String> parallelDataMap, String attribute)");
    LOGGER.info("parallelDataMap=" + parallelDataMap);
    LOGGER.info("attribute=" + attribute);
    String dataWithComma = parallelDataMap.get(ConfigElement.FEATURE.name().toLowerCase());
    String[] dataCommaStrArr = null;
    try {
      String allTestsSeparatedByComma = parallelDataMap.get(attribute);
      dataCommaStrArr = allTestsSeparatedByComma.split(",");
    } catch (Exception e) {
      throw new AppiumLabException("The attribute " + attribute + " not found!!");
    }
    LOGGER.info(
        "Leaving getParallelDataList(\n"
            + "      Map<String, String> parallelDataMap, String attribute)");
    return Arrays.asList(dataCommaStrArr);
  }

  private static List<String> getNodeAttributeAsAList(
      String node, Map<String, Map<String, Object>> distMap, String attribute)
      throws AppiumLabException {
    LOGGER.info(
        "Entering in getNodeAttributeAsAList(\n"
            + "      String node, Map<String, Map<String, Object>> distMap, String attribute)");
    LOGGER.info("distMap=" + distMap);
    LOGGER.info("attribute=" + attribute);
    String[] allTestsSeparatedByCommaArray = null;
    try {
      Map<String, Object> deviceConfigMap = distMap.get(node);
      String allTestsSeparatedByComma = (String) deviceConfigMap.get(attribute);
      allTestsSeparatedByCommaArray = allTestsSeparatedByComma.split(",");
    } catch (Exception e) {
      throw new AppiumLabException("The attribute " + attribute + " not found!!");
    }
    LOGGER.info(
        "Leaving getNodeAttributeAsAList(\n"
            + "      String node, Map<String, Map<String, Object>> distMap, String attribute)");
    return Arrays.asList(allTestsSeparatedByCommaArray);
  }

  private static int getNewPortIfTaken(int appiumPort) {
    LOGGER.info("Entering in getNewPortIfTaken(int appiumPort)");
    LOGGER.info("appiumPort=" + appiumPort);

    while ((isPortInTheList(appiumPort, allPortList))) {
      appiumPort++;
    }
    allPortList.add(appiumPort);
    LOGGER.info("appiumPort=" + appiumPort);
    LOGGER.info("allPortList=" + allPortList);
    LOGGER.info("Leaving getNewPortIfTaken(int appiumPort)");
    return appiumPort;
  }

  private static boolean isPortInTheList(int appiumPort, ArrayList<Integer> allPortList) {
    LOGGER.info("Entering in isPortInTheList(int appiumPort, ArrayList<Integer> allPortList) ");
    LOGGER.info("appiumPort=" + appiumPort);
    LOGGER.info("allPortList=" + allPortList);
    for (int port : allPortList) {
      if (port == appiumPort) {
        LOGGER.info("Leaving isPortInTheList(int appiumPort, ArrayList<Integer> allPortList) ");
        return true;
      }
    }
    LOGGER.info("Leaving isPortInTheList(int appiumPort, ArrayList<Integer> allPortList) ");
    return false;
  }

  private static int generateUniquePort(String udId) {
    LOGGER.info("Entering in generateUniquePort(String udId) ");
    LOGGER.info("udId=" + udId);

    int hashCode = Math.abs(udId.hashCode());
    String first4char = String.valueOf(hashCode).substring(0, 4);
    int port = Integer.parseInt(first4char);
    LOGGER.info("port=" + port);
    LOGGER.info("Leaving generateUniquePort(String udId) ");
    return port;
  }

  private static String getPlatformNameFromUdid(String udId, List<DeviceModel> allDeviceModelList)
      throws AppiumLabException {
    LOGGER.info(
        "Entering in getPlatformNameFromUdid(String udId, List<DeviceModel> allDeviceModelList) ");
    LOGGER.info("udId=" + udId);
    LOGGER.info("allDeviceModelList=" + allDeviceModelList);

    for (DeviceModel model : allDeviceModelList) {
      String platformName = null;
      if (model.getUdid().equalsIgnoreCase(udId)) {
        platformName = model.getPlatformName();
        LOGGER.info("platformName=" + platformName);
        LOGGER.info(
            "Leaving getPlatformNameFromUdid(String udId, List<DeviceModel> allDeviceModelList) ");
        return platformName;
      }
    }
    throw new AppiumLabException("Could not find the platform name with udid " + udId);
  }

  private static String getPlatformVersionFromUdid(
      String udId, List<DeviceModel> allDeviceModelList) throws AppiumLabException {
    LOGGER.info(
        "Entering in getPlatformVersionFromUdid(\n"
            + "      String udId, List<DeviceModel> allDeviceModelList) ");
    LOGGER.info("udId=" + udId);
    LOGGER.info("allDeviceModelList=" + allDeviceModelList);

    for (DeviceModel model : allDeviceModelList) {
      String platformVersion = null;
      if (model.getUdid().equalsIgnoreCase(udId)) {
        platformVersion = model.getOsVersion();
        LOGGER.info("platformVersion=" + platformVersion);
        LOGGER.info(
            "Leaving getPlatformVersionFromUdid(\n"
                + "      String udId, List<DeviceModel> allDeviceModelList) ");
        return platformVersion;
      }
    }
    throw new AppiumLabException("Could not find the platform version with udid " + udId);
  }

  private static void checkAllRealDevicesAreConnected(
      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)
      throws AppiumLabException {

    LOGGER.info(
        "Entering in checkAllRealDevicesAreConnected(\n"
            + "      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)");
    LOGGER.info("configurationModel=" + configurationModel);
    LOGGER.info("allPlatformDeviceMap=" + allPlatformDeviceMap);

    if (configurationModel.getMode().equalsIgnoreCase(RunType.DISTRIBUTED.name())) {

      DistributedNodeDataModel distributedNodeDataModel =
          configurationModel.getDistributedNodeDataModel();

      Map<String, DistributedAttributeDataModel> androidNodeMap =
          distributedNodeDataModel.getAndroidNodeMap();

      Map<String, DistributedAttributeDataModel> iOSNodeMap =
          distributedNodeDataModel.getiOSNodeMap();

      checkAllDevicesAreConnected(
          androidNodeMap, DeviceType.ANDROID_REAL_DEVICE.name(), allPlatformDeviceMap);
      checkAllDevicesAreConnected(
          androidNodeMap, DeviceType.IOS_REAL_DEVICE.name(), allPlatformDeviceMap);
    }
    LOGGER.info(
        "Leaving checkAllRealDevicesAreConnected(\n"
            + "      ConfigurationModel configurationModel, Map<String, List<DeviceModel>> allPlatformDeviceMap)");
  }

  private static void checkAllDevicesAreConnected(
      Map<String, DistributedAttributeDataModel> deviceNodeMap,
      String deviceType,
      Map<String, List<DeviceModel>> allPlatformDeviceMap)
      throws AppiumLabException {

    LOGGER.info(
        "Entering in checkAllDevicesAreConnected(\n"
            + "      Map<String, DistributedAttributeDataModel> deviceNodeMap,\n"
            + "      String deviceType,\n"
            + "      Map<String, List<DeviceModel>> allPlatformDeviceMap)");

    LOGGER.info("deviceNodeMap=" + deviceNodeMap);
    LOGGER.info("deviceType=" + deviceType);
    LOGGER.info("allPlatformDeviceMap=" + allPlatformDeviceMap);

    for (Map.Entry<String, DistributedAttributeDataModel> entry : deviceNodeMap.entrySet()) {
      DistributedAttributeDataModel distributedAttributeDataModel = entry.getValue();
      String udidFromConfigFile = distributedAttributeDataModel.getUdId();
      if (!isTheDevicePresent(udidFromConfigFile, allPlatformDeviceMap, deviceType)) {
        throw new AppiumLabException(
            "The " + deviceType + " with udid " + udidFromConfigFile + " is not connected!!");
      }
    }
    LOGGER.info(
        "Leaving checkAllDevicesAreConnected(\n"
            + "      Map<String, DistributedAttributeDataModel> deviceNodeMap,\n"
            + "      String deviceType,\n"
            + "      Map<String, List<DeviceModel>> allPlatformDeviceMap)");
  }

  private static boolean isTheDevicePresent(
      String udidFromConfigFile,
      Map<String, List<DeviceModel>> allPlatformDeviceMap,
      String deviceType) {

    LOGGER.info(
        "Entering in isTheDevicePresent(\n"
            + "      String udidFromConfigFile,\n"
            + "      Map<String, List<DeviceModel>> allPlatformDeviceMap,\n"
            + "      String deviceType)");
    LOGGER.info("udidFromConfigFile=" + udidFromConfigFile);
    LOGGER.info("allPlatformDeviceMap=" + allPlatformDeviceMap);
    LOGGER.info("deviceType=" + deviceType);

    List<DeviceModel> allAndroidDeviceModelList = allPlatformDeviceMap.get(deviceType);
    boolean udidPresent = isUdidConstainsInList(udidFromConfigFile, allAndroidDeviceModelList);
    LOGGER.info("udidPresent=" + udidPresent);
    LOGGER.info(
        "Leaving isTheDevicePresent(\n"
            + "      String udidFromConfigFile,\n"
            + "      Map<String, List<DeviceModel>> allPlatformDeviceMap,\n"
            + "      String deviceType)");
    return udidPresent;
  }

  private static boolean isUdidConstainsInList(
      String udidFromConfigFile, List<DeviceModel> deviceModelList) {
    LOGGER.info(
        "Entering in isUdidConstainsInList(\n"
            + "      String udidFromConfigFile, List<DeviceModel> deviceModelList)");
    LOGGER.info("udidFromConfigFile=" + udidFromConfigFile);
    LOGGER.info("deviceModelList=" + deviceModelList);

    for (DeviceModel deviceModel : deviceModelList) {
      if (deviceModel.getUdid().equalsIgnoreCase(udidFromConfigFile)) {
        LOGGER.info(
            "Leaving isUdidConstainsInList(\n"
                + "      String udidFromConfigFile, List<DeviceModel> deviceModelList)");
        return true;
      }
    }
    LOGGER.info(
        "Leaving isUdidConstainsInList(\n"
            + "      String udidFromConfigFile, List<DeviceModel> deviceModelList)");
    return false;
  }

  private static Map<String, List<DeviceModel>> getAllRealDeviceInfo(
      ConfigurationModel configurationModel) throws AppiumLabException {
    LOGGER.info("Entering in getAllRealDeviceInfo( ConfigurationModel configurationModel)");
    LOGGER.info("configurationModel=" + configurationModel);
    boolean isIOS = false;
    boolean isAndroid = false;
    if (configurationModel.getMode().equalsIgnoreCase(RunType.DISTRIBUTED.name().toLowerCase())) {
      DistributedNodeDataModel distributedNodeDataModel =
          configurationModel.getDistributedNodeDataModel();
      Map<String, DistributedAttributeDataModel> androidNodeMap =
          distributedNodeDataModel.getAndroidNodeMap();
      Map<String, DistributedAttributeDataModel> iOSNodeMap =
          distributedNodeDataModel.getiOSNodeMap();
      if (androidNodeMap.size() > 0) {
        isAndroid = true;
      }
      if (iOSNodeMap.size() > 0) {
        isIOS = true;
      }

    } else if (configurationModel
        .getMode()
        .equalsIgnoreCase(RunType.PARALLEL.name().toLowerCase())) {

      ParallelNodeDataModel parallelNodeDataModel = configurationModel.getParallelNodeDataModel();
      String platform = parallelNodeDataModel.getPlatformType();

      if (platform.equalsIgnoreCase(Platform.ANDROID.name())) {
        isAndroid = true;
        isIOS = false;
      } else if (platform.equalsIgnoreCase(Platform.IOS.name())) {
        isAndroid = false;
        isIOS = true;
      } else if (platform.equalsIgnoreCase(Platform.BOTH.name())) {
        isAndroid = true;
        isIOS = true;
      }
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
    LOGGER.info("allDeviceMap=" + allDeviceMap);
    LOGGER.info("Leaving getAllRealDeviceInfo( ConfigurationModel configurationModel)");
    return allDeviceMap;
  }
}
