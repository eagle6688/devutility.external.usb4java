package devutility.external.usb4java.javax;

import javax.usb.UsbPipe;

import devutility.external.usb4java.BaseTest;
import devutility.external.usb4java.javax.callbacks.ReceiveHandle;
import devutility.external.usb4java.javax.listeners.ReceiveListener;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4java_JavaxTest extends BaseTest {
	public short idVendor = 0;
	public short idProduct = 0;
	public byte interfaceNumber = 0;
	public byte endpointAddress = 0;

	@Override
	public void run() {
		ReceiveHandle receiveHandle = (numbers, bytes) -> {
			System.out.format("%d bytes have been transfered.\n", numbers);
		};

		try {
			UsbPipe usbPipe = UsbDeviceUtils.linkDevice(idVendor, idProduct, interfaceNumber, endpointAddress);
			usbPipe.open();

			ReceiveListener listener = new ReceiveListener(usbPipe, receiveHandle);
			Thread thread = new Thread(listener);
			thread.start();
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DUE_Usb4java_JavaxTest test = new DUE_Usb4java_JavaxTest();
		test.idVendor = (short) 0x2010;
		test.idProduct = 0x7638;
		test.interfaceNumber = (byte) 0;
		test.endpointAddress = (byte) 0x83;
		TestExecutor.run(test);
	}
}