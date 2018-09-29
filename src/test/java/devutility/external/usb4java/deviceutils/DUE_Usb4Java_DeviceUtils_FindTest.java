package devutility.external.usb4java.deviceutils;

import devutility.external.usb4java.BaseTest;
import devutility.external.usb4java.DeviceUtils;
import devutility.external.usb4java.models.UsbDevice;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4Java_DeviceUtils_FindTest extends BaseTest {
	private static final short vendorId = 0x046d;
	private static final short productId = (short) 0xc326;

	@Override
	public void run() {
		UsbDevice usbDevice = DeviceUtils.find(vendorId, productId);

		if (usbDevice == null) {
			System.out.println("Not found!");
			return;
		}

		System.out.println("Usb device found!");
	}

	public static void main(String[] args) {
		TestExecutor.run(DUE_Usb4Java_DeviceUtils_FindTest.class);
	}
}