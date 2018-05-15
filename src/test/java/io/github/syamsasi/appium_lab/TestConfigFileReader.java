package io.github.syamsasi.appium_lab;

import io.github.syamsasi.appium_lab.config.ConfigFileReader;
import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.runner.AppiumLabRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/** Created by Syam Sasi on May, 2018 */
public class TestConfigFileReader {

  @BeforeClass
  public static void setup() {}

  @Test
  public void testRunningMode() throws AppiumLabException {

    String configFilePath = System.getProperty("user.dir") + "/config.json";
    Map<String, Object> buildMap = null;
    ConfigFileReader.readConfigJson(new File(configFilePath));
  }

  @AfterClass
  public static void tearDown() {}
}
