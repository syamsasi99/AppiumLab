package io.github.syamsasi.appium_lab;

import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.runner.AppiumLabRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Created by Syam Sasi on May, 2018 */
public class TestAppiumLabRunner {

  @BeforeClass
  public static void setup() {}

  @Test
  public void testGetAllBuildAttributesFromConfigFile() throws AppiumLabException {

    String configFilePath = System.getProperty("user.dir") + "/config.json";
    Map<String, Object> buildMap = null;
    buildMap = AppiumLabRunner.getAllBuildAttributesFromConfigFile(new File(configFilePath));
  }

  @AfterClass
  public static void tearDown() {}
}
