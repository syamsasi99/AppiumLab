package io.github.syamsasi.appium_lab.config;

import io.github.syamsasi.appium_lab.model.ConfigurationModel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigFileReader {

  public static ConfigurationModel readConfigJson(File file)
      throws IOException, ParseException {

    JSONParser parser = new JSONParser();

    Object obj = parser.parse(new FileReader(file));

    JSONObject jsonObject = (JSONObject) obj;
    System.out.println(jsonObject);

    ConfigurationModel configurationModel = new ConfigurationModel();

    String mode = (String) jsonObject.get("mode");
    System.out.println("mode=" + mode);
    configurationModel.setMode(mode);

    String environment = (String) jsonObject.get("environment");
    System.out.println("environment=" + environment);
    configurationModel.setEnvironment(environment);

    if (mode.equalsIgnoreCase("distributed")) {
      Map<String, List<String>> distributedMap = parseDistributedData(jsonObject);
      configurationModel.setDistributedMap(distributedMap);
    } else if (mode.equalsIgnoreCase("parallel")) {
      Map<String, String> parallelMap = parseParallelData(jsonObject);
      configurationModel.setParallelMap(parallelMap);
    }

    System.out.println("configurationModel="+configurationModel);

    return configurationModel;
  }

  public static void main(String[] args) throws IOException, ParseException {

    String filePath = System.getProperty("user.dir") + "/config.json";
    readConfigJson(new File(filePath));
  }

  private static Map<String, List<String>> parseDistributedData(JSONObject jsonObject) {

    Map distributedMap = (Map<String, List<String>>) jsonObject.get("distributed");
    distributedMap.forEach((k, v) -> System.out.println("Key: " + k + ": Value: " + v));
    return distributedMap;
  }

  private static Map<String, String> parseParallelData(JSONObject jsonObject) {

    Map parallelMap = (Map<String, String>) jsonObject.get("parallel");
    parallelMap.forEach((k, v) -> System.out.println("Key: " + k + ": Value: " + v));
    return parallelMap;
  }
}
