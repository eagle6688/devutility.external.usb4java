package devutility.external.usb4java;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.external.usb4java.callbacks.ClaimInterfaceCallback;
import devutility.external.usb4java.callbacks.DeviceHandleCallback;
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
	public static void findDevice(Context context, short vendorId, short productId, FindUsbDeviceCallback callback) {
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
	 * Create a DeviceHandle object by provided device and pass it to callback.
	 * @param device: Device object.
	 * @param callback: DeviceHandleCallback object.
	 */
	public static void deviceHandle(Device device, DeviceHandleCallback callback) {
		DeviceHandle handle = new DeviceHandle();
		int result = LibUsb.open(device, handle);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to open USB device", result);
		}

		try {
			callback.call(handle);
		} finally {
			LibUsb.close(handle);
		}
	}

	/**
	 * Claim interface with provided DeviceHandle and interface number, after finish that execute the callback.
	 * @param handle: DeviceHandle handle.
	 * @param interfaceNumber: Interface number.
	 * @param callback: ClaimInterfaceCallback object.
	 */
	public static void claimInterface(DeviceHandle handle, int interfaceNumber, ClaimInterfaceCallback callback) {
		int result = LibUsb.claimInterface(handle, interfaceNumber);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to claim interface", result);
		}

		try {
			callback.call();
		} finally {
			result = LibUsb.releaseInterface(handle, interfaceNumber);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to release interface", result);
			}
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