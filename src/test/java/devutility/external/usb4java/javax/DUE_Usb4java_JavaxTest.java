package devutility.external.usb4java.javax;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbEndpoint;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;

import devutility.external.usb4java.BaseTest;
import devutility.internal.test.TestExecutor;

public class DUE_Usb4java_JavaxTest extends BaseTest {
	public short idVendor = 0;
	public short idProduct = 0;

	@Override
	public void run() {
		try {
			UsbPipe sendUsbPipe = useUsb();

			if (sendUsbPipe != null) {
				byte[] buff = new byte[8];

				for (int i = 0; i < 9; i++) {
					buff[i] = (byte) i;
					sendMassge(sendUsbPipe, buff);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UsbPipe useUsb() throws Exception {
		UsbInterface iface = linkDevice();

		if (iface == null) {
			return null;
		}

		UsbEndpoint receivedUsbEndpoint = (UsbEndpoint) iface.getUsbEndpoints().get(0);
		UsbEndpoint sendUsbEndpoint = (UsbEndpoint) iface.getUsbEndpoints().get(0);
		UsbPipe sendUsbPipe = sendUsbEndpoint.getUsbPipe();
		sendUsbPipe.open();

		final UsbPipe receivedUsbPipe = receivedUsbEndpoint.getUsbPipe();

		new Thread(new Runnable() {
			public void run() {
				try {
					receivedMassge(receivedUsbPipe);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return sendUsbPipe;
	}

	public UsbInterface linkDevice() throws Exception {
		UsbDevice device = null;

		if (device == null) {
			device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub());
		}

		if (device == null) {
			System.out.println("设备未找到！");
			return null;
		}

		UsbConfiguration configuration = device.getActiveUsbConfiguration();
		UsbInterface iface = null;

		if (configuration.getUsbInterfaces().size() > 0) {
			iface = (UsbInterface) configuration.getUsbInterfaces().get(0);
		} else {
			return null;
		}

		iface.claim();
		return iface;
	}

	public void receivedMassge(UsbPipe usbPipe) throws Exception {
		byte[] b = new byte[8];
		int length = 0;
		while (true) {
			length = usbPipe.syncSubmit(b);

			System.out.println("接收长度：" + length);
			for (int i = 0; i < length; i++) {
				System.out.print(Byte.toUnsignedInt(b[i]) + " ");
			}
		}
	}

	public static void sendMassge(UsbPipe usbPipe, byte[] buff) throws Exception {
		usbPipe.syncSubmit(buff);
	}

	public UsbDevice findDevice(UsbHub hub) {
		UsbDevice device = null;

		@SuppressWarnings("rawtypes")
		List list = (List) hub.getAttachedUsbDevices();

		for (int i = 0; i < list.size(); i++) {
			device = (UsbDevice) list.get(i);
			UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
			if (desc.idVendor() == idVendor && desc.idProduct() == idProduct) {
				return device;
			}
			if (device.isUsbHub()) {
				device = findDevice((UsbHub) device);
				if (device != null)
					return device;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		DUE_Usb4java_JavaxTest test = new DUE_Usb4java_JavaxTest();
		test.idVendor = 0x2010;
		test.idProduct = 0x7638;
		TestExecutor.run(test);
	}
}