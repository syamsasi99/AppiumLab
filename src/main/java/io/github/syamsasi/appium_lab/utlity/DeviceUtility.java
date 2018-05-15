package io.github.syamsasi.appium_lab.utlity;

import io.github.syamsasi.appium_lab.constants.AppiumLabConstants;
import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.runner.AppiumLabRunner;
import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;

import java.util.logging.Logger;

/** Created by Syam Sasi on May, 2018 */
public class DeviceUtility {
  private static final Logger LOGGER = Logger.getLogger(AppiumLabRunner.class.getName());



  public static String exeCommand(String cmd, String... args) throws AppiumLabException {
    LOGGER.info("Entering in exeCommand(String cmd, String... args)");
    LOGGER.info("cmd="+cmd);
    LOGGER.info("args="+args);
    ProcBuilder builder = new ProcBuilder(cmd);
    for (String argument : args) {
      builder = builder.withArg(argument);
    }
    builder = builder.withNoTimeout();
    try {
      ProcResult result = builder.run();
      String outputLog = result.getOutputString();
      LOGGER.info("outputLog="+outputLog);
      LOGGER.info("Leaving exeCommand(String cmd, String... args)");

      return outputLog;
    } catch (ExternalProcessFailureException e) {
      throw new AppiumLabException(e.getMessage());
    }
  }

  public static void restartAdbServer() throws AppiumLabException {
    LOGGER.info("Entering in restartAdbServer()");
    DeviceUtility.exeCommand(AppiumLabConstants.ADB, "kill-server");
    DeviceUtility.exeCommand(AppiumLabConstants.ADB, "start-server");
    LOGGER.info("Leaving restartAdbServer()");
  }
}
