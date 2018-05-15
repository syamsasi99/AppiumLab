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
    LOGGER.info("Entering in validateMode(String mode)");
    LOGGER.info("mode=" + mode);
    if (mode == null) {
      throw new AppiumLabException("The mode attribute is missing");
    }
    if (!(mode.equalsIgnoreCase(RunType.DISTRIBUTED.name())
        || mode.equalsIgnoreCase(RunType.PARALLEL.name())
        || mode.equalsIgnoreCase(RunType.AUTO.name()))) {
      throw new AppiumLabException("Invalid mode attribute!!!");
    }
    LOGGER.info("Leaving validateMode(String mode)");
  }

  protected static void validateEnvironment(String environment) throws AppiumLabException {
    LOGGER.info("Entering in validateEnvironment(String environment)");
    LOGGER.info("environment=" + environment);
    if (environment == null) {
      throw new AppiumLabException("The environment attribute is missing");
    }
    if (!(environment.equalsIgnoreCase(Environment.STAGING.name())
        || environment.equalsIgnoreCase(Environment.PRODUCTION.name()))) {
      throw new AppiumLabException("Invalid environment attribute!!!");
    }
    LOGGER.info("Leaving validateEnvironment(String environment)");
  }

  protected static DistributedNodeDataModel validateDistributedData(
      Map<String, Map<String, Object>> distributedMap) throws AppiumLabException {
    LOGGER.info(
        "Entering in validateDistributedData(\n"
            + "      Map<String, Map<String, Object>> distributedMap)");
    LOGGER.info("distributedMap=" + distributedMap);
    if (distributedMap == null) {
      throw new AppiumLabException("The distributed section can not be null!");
    }
    int distriMapSize = distributedMap.size();
    if ((distriMapSize == 0)) {
      throw new AppiumLabException("The distributed section can not be empty!");
    }
    List<String> allNodesList = new ArrayList<String>(distributedMap.keySet());
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
    LOGGER.info(
        "Leaving validateDistributedData(\n"
            + "      Map<String, Map<String, Object>> distributedMap)");
    return distributedNodeDataModel;
  }

  private static List<String> validateIncludeTags(String includeTags) throws AppiumLabException {
    LOGGER.info("Entering in validateIncludeTags(String includeTags)");
    LOGGER.info("includeTags=" + includeTags);
    LOGGER.info("Leaving validateIncludeTags(String includeTags)");

    return validateAttributeWithComma(includeTags, ConfigElement.INCLUDE);
  }

  private static List<String> validateExcludeTags(String excludeTags) throws AppiumLabException {
    LOGGER.info("Entering in invalidateExcludeTags(String excludeTags)");
    LOGGER.info("excludeTags=" + excludeTags);
    LOGGER.info("Leaving invalidateExcludeTags(String excludeTags)");

    return validateAttributeWithComma(excludeTags, ConfigElement.EXCLUDE);
  }

  private static List<String> validateAttributeWithComma(
      String attributeWithComma, ConfigElement configElement) throws AppiumLabException {
    LOGGER.info(
        "Entering in validateAttributeWithComma(\n"
            + "      String attributeWithComma, ConfigElement configElement)");
    LOGGER.info("attributeWithComma=" + attributeWithComma);
    LOGGER.info("configElement=" + configElement);
    if (attributeWithComma == null) {
      throw new AppiumLabException(
          "The attribute " + configElement.name().toLowerCase() + " should not be empty!");
    }
    String[] attributeArray = attributeWithComma.split(AppiumLabConstants.COMMA_SEPERATOR);
    List<String> attributeArrayList = Arrays.asList(attributeArray);
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
    LOGGER.info("attributeArrayList=" + attributeArrayList);
    LOGGER.info(
        "Leaving validateAttributeWithComma(\n"
            + "      String attributeWithComma, ConfigElement configElement)");
    return attributeArrayList;
  }

  private static boolean isListContainsDuplicates(List<String> list) {
    LOGGER.info("Entering in isListContainsDuplicates(List<String> list) ");
    LOGGER.info("list=" + list);

    Set<String> set = new HashSet<String>(list);

    if (set.size() < list.size()) {
      LOGGER.info("Leaving isListContainsDuplicates(List<String> list) ");
      return true;
    }
    LOGGER.info("Leaving isListContainsDuplicates(List<String> list) ");
    return false;
  }

  private static List<String> validateTestFiles(String testFiles) throws AppiumLabException {
    LOGGER.info("Entering in validateTestFiles(String testFiles) ");
    LOGGER.info("testFiles=" + testFiles);
    LOGGER.info("Leaving validateTestFiles(String testFiles) ");
    return validateAttributeWithComma(testFiles, ConfigElement.FEATURE);
  }

  private static void validateUdid(String udId) throws AppiumLabException {
    LOGGER.info("Entering in validateUdid(String udId) ");
    LOGGER.info("udId=" + udId);

    if (udId == null) {
      throw new AppiumLabException(
          "The attribute " + ConfigElement.UDID.name().toLowerCase() + " should not be empty!");
    }
    LOGGER.info("Leaving validateUdid(String udId) ");
  }

  protected static ParallelNodeDataModel validateParallelDataMap(Map<String, String> parallelMap)
      throws AppiumLabException {
    LOGGER.info("Entering in validateParallelDataMap(Map<String, String> parallelMap) ");
    LOGGER.info("parallelMap=" + parallelMap);

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
    LOGGER.info("Leaving validateParallelDataMap(Map<String, String> parallelMap) ");

    return parallelNodeDataModel;
  }

  private static void validatePlatformType(String platFormType) throws AppiumLabException {
    LOGGER.info("Entering in validatePlatformType(String platFormType) ");
    LOGGER.info("platFormType=" + platFormType);

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
    LOGGER.info("Leaving validatePlatformType(String platFormType) ");
  }
}
