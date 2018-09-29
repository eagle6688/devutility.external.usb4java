package devutility.external.usb4java.models;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;

public class UsbDevice {
	private Context context;
	private Device device;
	private DeviceDescriptor deviceDescriptor;
	private DeviceHandle deviceHandle;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public DeviceDescriptor getDeviceDescriptor() {
		return deviceDescriptor;
	}

	public void setDeviceDescriptor(DeviceDescriptor deviceDescriptor) {
		this.deviceDescriptor = deviceDescriptor;
	}

	public DeviceHandle getDeviceHandle() {
		return deviceHandle;
	}

	public void setDeviceHandle(DeviceHandle deviceHandle) {
		this.deviceHandle = deviceHandle;
	}
}