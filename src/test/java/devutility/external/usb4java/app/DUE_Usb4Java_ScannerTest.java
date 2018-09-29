package devutility.external.usb4java.app;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.usb4java.ConfigDescriptor;
import org.usb4java.EndpointDescriptor;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.external.usb4java.BaseTest;
import devutility.external.usb4java.UsbDeviceUtils;
import devutility.external.usb4java.callbacks.ClaimInterfaceCallback;
import devutility.external.usb4java.callbacks.ConfigDescriptorUsage;
import devutility.external.usb4java.callbacks.DeviceHandleUsage;
import devutility.external.usb4java.callbacks.FindUsbDeviceCallback;
import devutility.external.usb4java.callbacks.InitContextCallback;
import devutility.internal.lang.ExceptionUtils;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4Java_ScannerTest extends BaseTest {
	private static final short vendorId = 0x2010;
	private static final short productId = (short) 0x7638;

	@Override
	public void run() {
		ClaimInterfaceCallback claimInterfaceCallback = (usbDevice) -> {
			boolean quit = false;
			System.out.format("%x", usbDevice.getEndpointAddress());

			while (!quit) {
				IntBuffer transferedCount = IntBuffer.allocate(8);
				ByteBuffer byteBuffer = ByteBuffer.allocateDirect(usbDevice.getEndpointMaxPacketSize());
				int result = LibUsb.interruptTransfer(usbDevice.getDeviceHandle(), (byte) 0x83, byteBuffer, transferedCount, 5000);

				if (result != LibUsb.SUCCESS) {
					System.out.println(result);
					LibUsbException exception = new LibUsbException("Unable to interrupt transfer", result);
					System.out.println(ExceptionUtils.toString(exception));
					continue;
				}

				System.out.println(transferedCount.get());
			}
		};

		DeviceHandleUsage deviceHandleUsage = (usbDevice) -> {
			UsbDeviceUtils.activeDevice(usbDevice.getDeviceHandle());
			UsbDeviceUtils.claimInterface(usbDevice, claimInterfaceCallback);
		};

		ConfigDescriptorUsage configDescriptorUsage = (usbDevice) -> {
			ConfigDescriptor configDescriptor = usbDevice.getConfigDescriptor();
			InterfaceDescriptor interfaceDescriptor = configDescriptor.iface()[0].altsetting()[0];
			usbDevice.setInterfaceNumber(interfaceDescriptor.bInterfaceNumber());

			EndpointDescriptor endpointDescriptor = interfaceDescriptor.endpoint()[0];
			usbDevice.setEndpointAddress(endpointDescriptor.bEndpointAddress());
			usbDevice.setEndpointMaxPacketSize(endpointDescriptor.wMaxPacketSize());

			UsbDeviceUtils.deviceHandle(usbDevice, deviceHandleUsage);
		};

		FindUsbDeviceCallback findUsbDeviceCallback = (usbDevice) -> {
			if (usbDevice.getDevice() == null) {
				System.out.println("Not found!");
				return;
			}

			System.out.println("Usb device found!");
			System.out.println(usbDevice.getDeviceDescriptor().toString());
			UsbDeviceUtils.useConfigDescriptor(usbDevice, 0, configDescriptorUsage);
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