package io.github.syamsasi.appium_lab.devicefinder;

import io.github.syamsasi.appium_lab.model.DeviceModel;

import java.util.List;

/** Created by Syam Sasi on May, 2018 */
public interface DeviceFinder {

  List<DeviceModel> getAllRealDevices() throws Exception;

  List<DeviceModel> getAllVirtualDevices() throws Exception;

  List<DeviceModel> getAllDevices() throws Exception;
}
