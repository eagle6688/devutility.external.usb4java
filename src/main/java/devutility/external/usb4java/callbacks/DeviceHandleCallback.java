package devutility.external.usb4java.callbacks;

import org.usb4java.DeviceHandle;

public interface DeviceHandleCallback {
	void call(DeviceHandle handle);
}