package io.github.syamsasi.appium_lab.config;

import io.github.syamsasi.appium_lab.constants.AppiumLabConstants;
import io.github.syamsasi.appium_lab.constants.ConfigElement;
import io.github.syamsasi.appium_lab.constants.Environment;
import io.github.syamsasi.appium_lab.constants.Platform;
import io.github.syamsasi.appium_lab.constants.RunType;
import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.model.DistributedAttributeDataModel;
import io.github.syamsasi.appium_lab.model.DistributedNodeDataModel;
import io.github.syamsasi.appium_lab.model.ParallelNodeDataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/** Created by Syam Sasi on May, 2018 */
public class ConfigValidator {
  private static final Logger LOGGER = Logger.getLogger(ConfigValidator.class.getName());

  private ConfigValidator() {}

  protected static void validateMode(String mode) throws AppiumLabException {
    if (mode == null) {
      throw new AppiumLabException("The mode attribute is missing");
    }
    if (!(mode.equalsIgnoreCase(RunType.DISTRIBUTED.name())
        || mode.equalsIgnoreCase(RunType.PARALLEL.name())
        || mode.equalsIgnoreCase(RunType.AUTO.name()))) {
      throw new AppiumLabException("Invalid mode attribute!!!");
    }
  }

  protected static void validateEnvironment(String environment) throws AppiumLabException {
    if (environment == null) {
      throw new AppiumLabException("The environment attribute is missing");
    }
    if (!(environment.equalsIgnoreCase(Environment.STAGING.name())
        || environment.equalsIgnoreCase(Environment.PRODUCTION.name()))) {
      throw new AppiumLabException("Invalid environment attribute!!!");
    }
  }

  protected static DistributedNodeDataModel validateDistributedData(
      Map<String, Map<String, Object>> distributedMap) throws AppiumLabException {

    if (distributedMap == null) {
      throw new AppiumLabException("The distributed section can not be null!");
    }
    int distriMapSize = distributedMap.size();
    if ((distriMapSize == 0)) {
      throw new AppiumLabException("The distributed section can not be empty!");
    }
    List<String> allNodesList = new ArrayList<String>(distributedMap.keySet());
    LOGGER.info("distributedMap=" + distributedMap);
    LOGGER.info("allNodesList=" + allNodesList);
    Map<String, DistributedAttributeDataModel> androidNodeMap = new LinkedHashMap<>();
    Map<String, DistributedAttributeDataModel> iOSNodeMap = new LinkedHashMap<>();
    for (String node : allNodesList) {
      if (!((node.toLowerCase().startsWith(Platform.ANDROID.name().toLowerCase())
          || (node.toLowerCase().startsWith(Platform.IOS.name().toLowerCase()))))) {

        throw new AppiumLabException(
            "The distributed nodes should have the prefix as either "
                + Platform.ANDROID.name().toLowerCase()
                + " or "
                + Platform.IOS.name().toLowerCase()
                + " !");
      }

      Map<String, Object> distributedNodeAttributeMap = distributedMap.get(node);
      String udId =
          (String) distributedNodeAttributeMap.get(ConfigElement.UDID.name().toLowerCase());
      validateUdid(udId);
      String testFiles =
          (String) distributedNodeAttributeMap.get(ConfigElement.FEATURE.name().toLowerCase());
      List<String> testFilesList = validateTestFiles(testFiles);
      String includeTags =
          (String) distributedNodeAttributeMap.get(ConfigElement.INCLUDE.name().toLowerCase());
      List<String> includeTagList = validateIncludeTags(includeTags);
      String excludeTags =
          (String) distributedNodeAttributeMap.get(ConfigElement.EXCLUDE.name().toLowerCase());
      List<String> excludeTagList = validateExcludeTags(includeTags);

      DistributedAttributeDataModel distributedAttributeDataModel =
          new DistributedAttributeDataModel(udId, testFilesList, includeTagList, excludeTagList);
      if ((node.toLowerCase().startsWith(Platform.ANDROID.name().toLowerCase()))) {
        androidNodeMap.put(node, distributedAttributeDataModel);
      } else if ((node.toLowerCase().startsWith(Platform.IOS.name().toLowerCase()))) {
        iOSNodeMap.put(node, distributedAttributeDataModel);
      }
    }
    DistributedNodeDataModel distributedNodeDataModel =
        new DistributedNodeDataModel(androidNodeMap, iOSNodeMap);
    LOGGER.info("distributedNodeDataModel=" + distributedNodeDataModel);
    return distributedNodeDataModel;
  }

  private static List<String> validateIncludeTags(String includeTags) throws AppiumLabException {
    return validateAttributeWithComma(includeTags, ConfigElement.INCLUDE);
  }

  private static List<String> validateExcludeTags(String excludeTags) throws AppiumLabException {
    return validateAttributeWithComma(excludeTags, ConfigElement.EXCLUDE);
  }

  private static List<String> validateAttributeWithComma(
      String attributeWithComma, ConfigElement configElement) throws AppiumLabException {

    if (attributeWithComma == null) {
      throw new AppiumLabException(
          "The attribute " + configElement.name().toLowerCase() + " should not be empty!");
    }
    String[] attributeArray = attributeWithComma.split(AppiumLabConstants.COMMA_SEPERATOR);
    List<String> attributeArrayList = Arrays.asList(attributeArray);
    LOGGER.info("attributeArrayList=" + attributeArrayList);
    for (String attr : attributeArrayList) {
      if (attr.trim().equalsIgnoreCase("")) {
        throw new AppiumLabException(
            "The " + configElement.name().toLowerCase() + " contains empty string!!");
      }
    }

    if (isListContainsDuplicates(attributeArrayList)) {
      throw new AppiumLabException(
          "The " + configElement.name().toLowerCase() + " contains duplicate data!!");
    }

    return attributeArrayList;
  }

  private static boolean isListContainsDuplicates(List<String> list) {
    Set<String> set = new HashSet<String>(list);

    if (set.size() < list.size()) {
      return true;
    }
    return false;
  }

  private static List<String> validateTestFiles(String testFiles) throws AppiumLabException {
    return validateAttributeWithComma(testFiles, ConfigElement.FEATURE);
  }

  private static void validateUdid(String udId) throws AppiumLabException {
    if (udId == null) {
      throw new AppiumLabException(
          "The attribute " + ConfigElement.UDID.name().toLowerCase() + " should not be empty!");
    }
  }

  protected static ParallelNodeDataModel validateParallelDataMap(Map<String, String> parallelMap)
      throws AppiumLabException {

    if (parallelMap == null) {
      throw new AppiumLabException("The parallel section can not be null!");
    }
    int parallelMapSize = parallelMap.size();
    if ((parallelMapSize == 0)) {
      throw new AppiumLabException("The parallel section can not be empty!");
    }

    String testFiles = (String) parallelMap.get(ConfigElement.FEATURE.name().toLowerCase());
    List<String> testFilesList = validateTestFiles(testFiles);
    String includeTags = (String) parallelMap.get(ConfigElement.INCLUDE.name().toLowerCase());
    List<String> includeTagList = validateIncludeTags(includeTags);
    String excludeTags = (String) parallelMap.get(ConfigElement.EXCLUDE.name().toLowerCase());
    List<String> excludeTagList = validateExcludeTags(includeTags);

    String platFormType = (String) parallelMap.get(ConfigElement.PLATFORM.name().toLowerCase());
    validatePlatformType(platFormType);

    ParallelNodeDataModel parallelNodeDataModel =
        new ParallelNodeDataModel(testFilesList, includeTagList, excludeTagList, platFormType);
    LOGGER.info("parallelNodeDataModel=" + parallelNodeDataModel);
    return parallelNodeDataModel;
  }

  private static void validatePlatformType(String platFormType) throws AppiumLabException {
    if (platFormType == null) {
      throw new AppiumLabException(
          "The attribute " + ConfigElement.PLATFORM.name().toLowerCase() + " should not be empty!");
    }
    if (!((platFormType.equalsIgnoreCase(Platform.ANDROID.name()))
        || (platFormType.equalsIgnoreCase(Platform.IOS.name()))
        || (platFormType.equalsIgnoreCase(Platform.BOTH.name())))) {
      throw new AppiumLabException(
          "The attribute "
              + ConfigElement.PLATFORM.name().toLowerCase()
              + " should be any of the following - \n"
              + " "
              + Platform.ANDROID.name().toLowerCase()
              + " or "
              + Platform.IOS.name().toLowerCase()
              + " or "
              + Platform.BOTH.name().toLowerCase());
    }
  }
}
