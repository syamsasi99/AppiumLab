package io.github.syamsasi.appium_lab.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/** Created by Syam Sasi on May, 2018 */
public class ConfigFileReader {

  public static ConfigurationModel readConfigJson(File file) throws IOException, ParseException {

    JSONParser parser = new JSONParser();

    Object obj = parser.parse(new FileReader(file));

    JSONObject jsonObject = (JSONObject) obj;

    ConfigurationModel configurationModel = new ConfigurationModel();

    String mode = (String) jsonObject.get("mode");
    configurationModel.setMode(mode);

    String environment = (String) jsonObject.get("environment");
    configurationModel.setEnvironment(environment);

    if (mode.equalsIgnoreCase("distributed")) {
      Map<String, Map<String, Object>> distributedMap = parseDistributedData(jsonObject);
      configurationModel.setDistributedMap(distributedMap);
    } else if (mode.equalsIgnoreCase("parallel")) {
      Map<String, String> parallelMap = parseParallelData(jsonObject);
      configurationModel.setParallelMap(parallelMap);
    }

    return configurationModel;
  }

  private static Map<String, Map<String, Object>> parseDistributedData(JSONObject jsonObject)
      throws IOException {

    Map distributedMap = (Map<String, List<String>>) jsonObject.get("distributed");
    Map<String, Map<String, Object>> distributedMapTmp =
        new LinkedHashMap<String, Map<String, Object>>();
    List<String> allNodes = new ArrayList<String>(distributedMap.keySet());
    for (String node : allNodes) {
      JSONObject jsonList = (JSONObject) distributedMap.get(node);
      Map<String, Object> jsonMap = convertToJavaMap(jsonList);
      distributedMapTmp.put(node, jsonMap);
    }

    return distributedMapTmp;
  }

  private static Map<String, Object> convertToJavaMap(JSONObject jsonList) throws IOException {
    Map<String, Object> jsonMap = new HashMap<String, Object>();

    String jsonMapString = jsonList.toJSONString();
    ObjectMapper mapper = new ObjectMapper();
    jsonMap = mapper.readValue(jsonMapString, new TypeReference<Map<String, String>>() {});
    return jsonMap;
  }

  private static Map<String, String> parseParallelData(JSONObject jsonObject) {

    Map parallelMap = (Map<String, String>) jsonObject.get("parallel");
    return parallelMap;
  }
}
