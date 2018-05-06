package io.github.syamsasi.appium_lab.utlity;

import org.buildobjects.process.ProcBuilder;

public class DeviceUtility {

  public static String exeCommand(String cmd, String... args) {
    String outputLog = ProcBuilder.run(cmd, args);
    return outputLog;
  }

  public static void main(String[] args){
      exeCommand("adb","devices");
  }
}
