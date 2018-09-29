package devutility.external.usb4java.app;

import java.nio.IntBuffer;

import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.external.usb4java.BaseTest;
import devutility.external.usb4java.UsbDeviceUtils;
import devutility.external.usb4java.callbacks.DeviceHandleCallback;
import devutility.external.usb4java.callbacks.FindUsbDeviceCallback;
import devutility.external.usb4java.callbacks.InitContextCallback;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4Java_ScannerTest extends BaseTest {
	private static final short vendorId = 0x2010;
	private static final short productId = (short) 0x7638;

	@Override
	public void run() {
		DeviceHandleCallback deviceHandleCallback = (usbDevice) -> {
			IntBuffer intBuffer = IntBuffer.allocate(8);
			int result = LibUsb.getConfiguration(usbDevice.getDeviceHandle(), intBuffer);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to get Configuration", result);
			}

			int config = intBuffer.get();
			System.out.println(config);
			result = LibUsb.setConfiguration(usbDevice.getDeviceHandle(), config);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to set Configuration", result);
			}
		};

		FindUsbDeviceCallback findUsbDeviceCallback = (usbDevice) -> {
			if (usbDevice.getDevice() == null) {
				System.out.println("Not found!");
				return;
			}

			System.out.println("Usb device found!");
			System.out.println(usbDevice.getDeviceDescriptor().toString());
			UsbDeviceUtils.deviceHandle(usbDevice, deviceHandleCallback);
		};

		InitContextCallback initContextCallback = (usbDevice) -> {
			UsbDeviceUtils.findDevice(usbDevice, vendorId, productId, findUsbDeviceCallback);
		};

		UsbDeviceUtils.init(initContextCallback);
	}

	public static void main(String[] args) {
		TestExecutor.run(DUE_Usb4Java_ScannerTest.class);
	}
}