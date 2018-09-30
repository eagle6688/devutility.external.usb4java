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
import devutility.external.usb4java.app.services.ControlTransferService;
import devutility.external.usb4java.app.services.InterruptTransferService;
import devutility.external.usb4java.callbacks.ClaimInterfaceCallback;
import devutility.external.usb4java.callbacks.ConfigDescriptorUsage;
import devutility.external.usb4java.callbacks.DeviceHandleUsage;
import devutility.external.usb4java.callbacks.FindUsbDeviceCallback;
import devutility.external.usb4java.callbacks.InitContextCallback;
import devutility.external.usb4java.models.UsbDevice;
import devutility.internal.lang.ExceptionUtils;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4Java_KeyboardTest extends BaseTest {
	private static final short vendorId = 0x046d;
	private static final short productId = (short) 0xc326;

	@Override
	public void run() {
		ClaimInterfaceCallback claimInterfaceCallback = (usbDevice) -> {
			System.out.format("EndpointAddress: %x\n", usbDevice.getEndpointAddress());
			ControlTransferService.cases(usbDevice);
			InterruptTransferService.test(usbDevice);
		};

		DeviceHandleUsage deviceHandleUsage = (usbDevice) -> {
			UsbDeviceUtils.activeDevice(usbDevice.getDeviceHandle());
			UsbDeviceUtils.claimInterface(usbDevice, claimInterfaceCallback);
		};

		ConfigDescriptorUsage configDescriptorUsage = (usbDevice) -> {
			ConfigDescriptor configDescriptor = usbDevice.getConfigDescriptor();
			InterfaceDescriptor interfaceDescriptor = configDescriptor.iface()[0].altsetting()[0];
			usbDevice.setInterfaceNumber(interfaceDescriptor.bInterfaceNumber());

			System.out.format("endpoint count: %d\n", interfaceDescriptor.endpoint().length);

			EndpointDescriptor endpointDescriptor = interfaceDescriptor.endpoint()[0];
			usbDevice.setEndpointAddress(endpointDescriptor.bEndpointAddress());
			usbDevice.setEndpointMaxPacketSize(endpointDescriptor.wMaxPacketSize());

			UsbDeviceUtils.deviceHandle(usbDevice, deviceHandleUsage);
		};

		FindUsbDeviceCallback findUsbDeviceCallback = (usbDevice) -> {
			if (usbDevice.getDevice() == null) {
				System.out.print("Device not found, vendorId: ");
				System.out.print(vendorId);
				System.out.print(", productId: ");
				System.out.println(productId);
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

	void interruptTransferTest(UsbDevice usbDevice) {
		while (true) {
			IntBuffer transferedCountBuffer = IntBuffer.allocate(8);
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(usbDevice.getEndpointMaxPacketSize());
			int result = LibUsb.interruptTransfer(usbDevice.getDeviceHandle(), usbDevice.getEndpointAddress(), byteBuffer, transferedCountBuffer, 5000);

			if (result != LibUsb.SUCCESS) {
				System.out.println(result);
				LibUsbException exception = new LibUsbException("Unable to interrupt transfer", result);
				System.out.println(ExceptionUtils.toString(exception));
				continue;
			}

			if (byteBuffer.hasArray()) {
				System.out.format("Read from device length:  %d, value : %s\n", transferedCountBuffer.get(), new String(byteBuffer.array()));
			}
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(DUE_Usb4Java_KeyboardTest.class);
	}
}