package devutility.external.usb4java.callbacks;

import devutility.external.usb4java.models.UsbDevice;

public interface ClaimInterfaceCallback {
	void call(UsbDevice usbDevice);
}