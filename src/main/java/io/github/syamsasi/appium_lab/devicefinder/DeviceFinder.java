package io.github.syamsasi.appium_lab.devicefinder;

import io.github.syamsasi.appium_lab.exception.AppiumLabException;
import io.github.syamsasi.appium_lab.model.DeviceModel;

import java.util.List;

/** Created by Syam Sasi on May, 2018 */
public interface DeviceFinder {

  List<DeviceModel> getAllRealDevices() throws AppiumLabException;

  List<DeviceModel> getAllVirtualDevices() throws AppiumLabException;

  List<DeviceModel> getAllDevices() throws AppiumLabException;

}
