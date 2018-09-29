package devutility.external.usb4java.models;

import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;

public class UsbDevice {
	private Context context;
	private Device device;
	private DeviceDescriptor deviceDescriptor;
	private DeviceHandle deviceHandle;
	private ConfigDescriptor configDescriptor;
	private int interfaceNumber;
	private int alternateSetting;
	private byte endpointAddress;
	private short endpointMaxPacketSize;

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

	public ConfigDescriptor getConfigDescriptor() {
		return configDescriptor;
	}

	public void setConfigDescriptor(ConfigDescriptor configDescriptor) {
		this.configDescriptor = configDescriptor;
	}

	public int getInterfaceNumber() {
		return interfaceNumber;
	}

	public void setInterfaceNumber(int interfaceNumber) {
		this.interfaceNumber = interfaceNumber;
	}

	public int getAlternateSetting() {
		return alternateSetting;
	}

	public void setAlternateSetting(int alternateSetting) {
		this.alternateSetting = alternateSetting;
	}

	public byte getEndpointAddress() {
		return endpointAddress;
	}

	public void setEndpointAddress(byte endpointAddress) {
		this.endpointAddress = endpointAddress;
	}

	public short getEndpointMaxPacketSize() {
		return endpointMaxPacketSize;
	}

	public void setEndpointMaxPacketSize(short endpointMaxPacketSize) {
		this.endpointMaxPacketSize = endpointMaxPacketSize;
	}
}