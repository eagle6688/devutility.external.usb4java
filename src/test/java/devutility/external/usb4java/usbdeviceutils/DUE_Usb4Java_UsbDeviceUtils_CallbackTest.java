package devutility.external.usb4java.usbdeviceutils;

import devutility.external.usb4java.BaseTest;
import devutility.external.usb4java.UsbDeviceUtils;
import devutility.external.usb4java.callbacks.FindUsbDeviceCallback;
import devutility.external.usb4java.callbacks.InitContextCallback;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4Java_UsbDeviceUtils_CallbackTest extends BaseTest {
	private static final short vendorId = 0x046d;
	private static final short productId = (short) 0xc326;

	@Override
	public void run() {
		FindUsbDeviceCallback findUsbDeviceCallback = (usbDevice) -> {
			if (usbDevice == null) {
				System.out.println("Not found!");
				return;
			}

			System.out.println("Usb device found!");
			System.out.println(usbDevice.getDevice().toString());
			System.out.println(usbDevice.getDeviceDescriptor().toString());
		};

		InitContextCallback initContextCallback = (context) -> {
			UsbDeviceUtils.find(context, vendorId, productId, findUsbDeviceCallback);
		};

		UsbDeviceUtils.init(initContextCallback);
	}

	public static void main(String[] args) {
		TestExecutor.run(DUE_Usb4Java_UsbDeviceUtils_CallbackTest.class);
	}
}