package io.github.syamsasi.appium_lab.utlity;

import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;

/** Created by Syam Sasi on May, 2018 */
public class DeviceUtility {

  public static String exeCommand(String cmd, String... args) {
    ProcBuilder builder = new ProcBuilder(cmd);
    for (String argument : args) {
      builder = builder.withArg(argument);
    }
    builder = builder.withNoTimeout();
    ProcResult result = builder.run();

    String outputLog = result.getOutputString();
    return outputLog;
  }

  public static void restartAdbServer() {
    DeviceUtility.exeCommand(AppiumLabConstants.ADB, "kill-server");
    DeviceUtility.exeCommand(AppiumLabConstants.ADB, "start-server");
  }
}
