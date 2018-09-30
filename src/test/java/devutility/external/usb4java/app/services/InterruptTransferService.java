package devutility.external.usb4java.app.services;

import java.nio.ByteBuffer;

import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

import devutility.external.usb4java.models.UsbDevice;

public class InterruptTransferService {
	public static void test(UsbDevice usbDevice) {
		TransferCallback receiveData = new TransferCallback() {
			@Override
			public void processTransfer(Transfer transfer) {
				System.out.format("InterruptTransferService.test: %d bytes received.", transfer.actualLength());
			}
		};

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(usbDevice.getEndpointMaxPacketSize());
		Transfer transfer = LibUsb.allocTransfer(0);
		LibUsb.fillInterruptTransfer(transfer, usbDevice.getDeviceHandle(), usbDevice.getEndpointAddress(), byteBuffer, receiveData, null, 3000L);
		int result = LibUsb.submitTransfer(transfer);

		if (result != LibUsb.SUCCESS) {
			System.out.println(result);
			throw new LibUsbException("Unable to submit transfer", result);
		}
	}
}