package devutility.external.usb4java;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.external.usb4java.callbacks.FindUsbDeviceCallback;
import devutility.external.usb4java.callbacks.InitContextCallback;
import devutility.external.usb4java.models.UsbDevice;
import devutility.internal.lang.ExceptionUtils;

public class UsbDeviceUtils {
	/**
	 * Init the context, can be used as an entry.
	 * @param callback: InitContextCallback object.
	 */
	public static void init(InitContextCallback callback) {
		Context context = new Context();
		int result = LibUsb.init(context);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to initialize libusb", result);
		}

		try {
			callback.call(context);
		} finally {
			LibUsb.exit(context);
		}
	}

	/**
	 * Find a usb device from device list.
	 * @param context: Context object.
	 * @param vendorId: Usb device vendorId from DeviceDescriptor.
	 * @param productId: Usb device productId from DeviceDescriptor.
	 * @param callback: FindUsbDeviceCallback object.
	 */
	public static void find(Context context, short vendorId, short productId, FindUsbDeviceCallback callback) {
		DeviceList devices = new DeviceList();
		int result = LibUsb.getDeviceList(context, devices);

		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		try {
			for (Device device : devices) {
				DeviceDescriptor descriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, descriptor);

				if (result != LibUsb.SUCCESS) {
					LibUsbException exception = new LibUsbException("Unable to read device descriptor", result);
					System.out.println(ExceptionUtils.toString(exception));
					continue;
				}

				if (descriptor.idProduct() == productId && descriptor.idVendor() == vendorId) {
					UsbDevice usbDevice = new UsbDevice();
					usbDevice.setDevice(device);
					usbDevice.setDeviceDescriptor(descriptor);
					callback.call(usbDevice);
					return;
				}
			}

			callback.call(null);
		} finally {
			LibUsb.freeDeviceList(devices, true);
		}
	}

	/**
	 * Find a usb device from device list.
	 * @param context: Context object.
	 * @param vendorId: Usb device vendorId from DeviceDescriptor.
	 * @param productId: Usb device productId from DeviceDescriptor.
	 * @return UsbDevice
	 */
	public static UsbDevice find(Context context, short vendorId, short productId) {
		DeviceList devices = new DeviceList();
		int result = LibUsb.getDeviceList(context, devices);

		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		try {
			for (Device device : devices) {
				DeviceDescriptor descriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, descriptor);

				if (result != LibUsb.SUCCESS) {
					LibUsbException exception = new LibUsbException("Unable to read device descriptor", result);
					System.out.println(ExceptionUtils.toString(exception));
					continue;
				}

				if (descriptor.idProduct() == productId && descriptor.idVendor() == vendorId) {
					UsbDevice usbDevice = new UsbDevice();
					usbDevice.setDevice(device);
					usbDevice.setDeviceDescriptor(descriptor);
					return usbDevice;
				}
			}
		} finally {
			LibUsb.freeDeviceList(devices, true);
		}

		return null;
	}

	/**
	 * Find a usb device from device list.
	 * @param vendorId: Usb device vendorId from DeviceDescriptor.
	 * @param productId: Usb device productId from DeviceDescriptor.
	 * @return UsbDevice
	 */
	public static UsbDevice find(short vendorId, short productId) {
		Context context = new Context();
		int result = LibUsb.init(context);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to initialize libusb", result);
		}

		try {
			return find(context, vendorId, productId);
		} finally {
			LibUsb.exit(context);
		}
	}
}