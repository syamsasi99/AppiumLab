package io.github.syamsasi.appium_lab.devicefinder;

import io.github.syamsasi.appium_lab.model.DeviceModel;

import java.util.List;

public abstract class BaseDeviceFinder implements DeviceFinder {

  public abstract List<DeviceModel> getAllRealDevices() throws Exception;

  public abstract List<DeviceModel> getAllVirtualDevices () throws Exception;
}
