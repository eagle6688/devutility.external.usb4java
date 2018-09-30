package devutility.external.usb4java.app.services;

import java.nio.ByteBuffer;

import org.usb4java.LibUsb;

import devutility.external.usb4java.models.UsbDevice;

public class ControlTransferService {
	public static void test(UsbDevice usbDevice, short wValue, short wIndex) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(usbDevice.getEndpointMaxPacketSize());
		int transferedCount = LibUsb.controlTransfer(usbDevice.getDeviceHandle(), LibUsb.ENDPOINT_IN, LibUsb.REQUEST_GET_DESCRIPTOR, wValue, wIndex, byteBuffer, 2000L);

		if (transferedCount > 0) {
			System.out.format("ControlTransferService.test: %d bytes has been transfered!\n", transferedCount);

			if (byteBuffer.hasArray()) {
				System.out.println(new String(byteBuffer.array()));
			}

			return;
		}

		System.out.println("ControlTransferService.test: Nothing transfered through controlTransfer!");
	}

	public static void cases(UsbDevice usbDevice) {
		test(usbDevice, (short) 0x0100, (short) 0x0000);
		test(usbDevice, (short) 0x0200, (short) 0x0000);
		test(usbDevice, (short) 0x0300, (short) 0x0409);
	}
}