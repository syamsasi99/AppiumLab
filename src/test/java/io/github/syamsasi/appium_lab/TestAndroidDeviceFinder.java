package io.github.syamsasi.appium_lab;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
/** Created by Syam Sasi on May, 2018 */
public class TestAndroidDeviceFinder {

  @BeforeClass
  public static void setup() {}

  @Test
  public void testGetAllRealDevices() {
    assertThat("Frodo").isEqualTo("Frodo");
  }

  @AfterClass
  public static void tearDown() {}
}
