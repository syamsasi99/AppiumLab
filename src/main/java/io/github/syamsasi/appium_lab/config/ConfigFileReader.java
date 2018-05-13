package io.github.syamsasi.appium_lab.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.syamsasi.appium_lab.constants.ConfigElement;
import io.github.syamsasi.appium_lab.constants.RunType;
import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.model.ConfigurationModel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Created by Syam Sasi on May, 2018 */
public class ConfigFileReader {

  private static final Logger LOGGER = Logger.getLogger(ConfigFileReader.class.getName());

  /**
   * Reading the config json file and return the ConfigurationModel
   * @param file -> The config json file
   * @return ConfigurationModel
   * @throws IOException
   * @throws ParseException
   */
  public static ConfigurationModel readConfigJson(File file) throws AppiumLabException {
    LOGGER.info("Entering readConfigJson()");
    LOGGER.info("Reading the config file , " + file);
    JSONParser parser = new JSONParser();
    Object obj = null;
    try {
      obj = parser.parse(new FileReader(file));
    } catch (IOException e) {
      throw new AppiumLabException(e.getMessage());
    } catch (ParseException e) {
      throw new AppiumLabException(e.getMessage());
    }
    JSONObject jsonObject = (JSONObject) obj;
    ConfigurationModel configurationModel = new ConfigurationModel();
    String mode = (String) jsonObject.get(ConfigElement.MODE.name().toLowerCase());
    LOGGER.info("Running mode= " + mode);
    configurationModel.setMode(mode);

    String environment = (String) jsonObject.get(ConfigElement.ENVIRONMENT.name().toLowerCase());
    LOGGER.info("Running environment= " + environment);
    configurationModel.setEnvironment(environment);

    if (mode.equalsIgnoreCase(RunType.DISTRIBUTED.name())) {
      Map<String, Map<String, Object>> distributedMap = null;
      try {
        distributedMap = parseDistributedData(jsonObject);
      } catch (IOException e) {
        throw new AppiumLabException(e.getMessage());
      }
      LOGGER.info("DistributedMap= " + distributedMap);
      configurationModel.setDistributedMap(distributedMap);
    } else if (mode.equalsIgnoreCase(RunType.PARALLEL.name())) {
      Map<String, String> parallelMap = parseParallelData(jsonObject);
      LOGGER.info("ParallelMap= " + parallelMap);
      configurationModel.setParallelMap(parallelMap);
    }
    LOGGER.info("configurationModel=" + configurationModel);

    LOGGER.info("Exiting readConfigJson()");
    return configurationModel;
  }

  private static Map<String, Map<String, Object>> parseDistributedData(JSONObject jsonObject)
          throws IOException, AppiumLabException {
    LOGGER.info("Entering parseDistributedData()");

    Map distributedMap =
        (Map<String, List<String>>) jsonObject.get(RunType.DISTRIBUTED.name().toLowerCase());
    LOGGER.info("distributedMap=" + distributedMap);
    Map<String, Map<String, Object>> distributedMapTmp =
        new LinkedHashMap<String, Map<String, Object>>();
    List<String> allNodes = new ArrayList<String>(distributedMap.keySet());
    for (String node : allNodes) {
      JSONObject jsonList = (JSONObject) distributedMap.get(node);
      Map<String, Object> jsonMap = null;
      try {
        jsonMap = convertToJavaMap(jsonList);
      } catch (AppiumLabException e) {
       throw new AppiumLabException(e.getMessage());
      }
      distributedMapTmp.put(node, jsonMap);
    }
    LOGGER.info("distributedMapTmp=" + distributedMapTmp);
    LOGGER.info("Exiting parseDistributedData()");

    return distributedMapTmp;
  }

  private static Map<String, Object> convertToJavaMap(JSONObject jsonList) throws AppiumLabException {
    LOGGER.info("Entering convertToJavaMap()");
    LOGGER.info("jsonList=" + jsonList);

    Map<String, Object> jsonMap = new HashMap<String, Object>();
    String jsonMapString = jsonList.toJSONString();
    LOGGER.info("jsonMapString=" + jsonMapString);
    ObjectMapper mapper = new ObjectMapper();
    try {
      jsonMap = mapper.readValue(jsonMapString, new TypeReference<Map<String, String>>() {});
    } catch (IOException e) {
      throw new AppiumLabException(e.getMessage());
    }
    LOGGER.info("jsonMap=" + jsonMap);
    LOGGER.info("Exiting convertToJavaMap()");
    return jsonMap;
  }

  private static Map<String, String> parseParallelData(JSONObject jsonObject) {
    LOGGER.info("Entering parseParallelData()");
    LOGGER.info("jsonObject=" + jsonObject);
    Map parallelMap = (Map<String, String>) jsonObject.get(RunType.PARALLEL.name().toLowerCase());
    LOGGER.info("parallelMap=" + parallelMap);
    LOGGER.info("Exiting parseParallelData()");
    return parallelMap;
  }
}
