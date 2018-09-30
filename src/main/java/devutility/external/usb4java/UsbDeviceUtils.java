package devutility.external.usb4java;

import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.external.usb4java.callbacks.ClaimInterfaceCallback;
import devutility.external.usb4java.callbacks.DeviceHandleUsage;
import devutility.external.usb4java.callbacks.FindUsbDeviceCallback;
import devutility.external.usb4java.callbacks.InitContextCallback;
import devutility.external.usb4java.callbacks.ConfigDescriptorUsage;
import devutility.external.usb4java.enums.UsbDeviceErrorCode;
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
			UsbDevice usbDevice = new UsbDevice();
			usbDevice.setContext(context);
			callback.call(usbDevice);
		} finally {
			LibUsb.exit(context);
		}
	}

	/**
	 * Find a usb device from device list.
	 * @param usbDevice: UsbDevice object.
	 * @param vendorId: Usb device vendorId from DeviceDescriptor.
	 * @param productId: Usb device productId from DeviceDescriptor.
	 * @param callback: FindUsbDeviceCallback object.
	 */
	public static void findDevice(UsbDevice usbDevice, short vendorId, short productId, FindUsbDeviceCallback callback) {
		DeviceList devices = new DeviceList();
		Context context = usbDevice.getContext();

		if (context == null) {
			throw new LibUsbException("Unable to get device list", UsbDeviceErrorCode.NOCONTEXT.value());
		}

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
					usbDevice.setDevice(device);
					usbDevice.setDeviceDescriptor(descriptor);
					callback.call(usbDevice);
					return;
				}
			}

			callback.call(usbDevice);
		} finally {
			LibUsb.freeDeviceList(devices, true);
		}
	}

	/**
	 * Use ConfigDescriptor
	 * @param usbDevice: UsbDevice object, must contain Device object.
	 * @param index: Config index.
	 * @param usage: ConfigDescriptorUsage object.
	 */
	public static void useConfigDescriptor(UsbDevice usbDevice, int index, ConfigDescriptorUsage usage) {
		ConfigDescriptor descriptor = new ConfigDescriptor();
		int result = LibUsb.getConfigDescriptor(usbDevice.getDevice(), (byte) index, descriptor);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to read config descriptor", result);
		}

		try {
			usbDevice.setConfigDescriptor(descriptor);
			usage.call(usbDevice);
		} finally {
			LibUsb.freeConfigDescriptor(descriptor);
		}
	}

	/**
	 * List all ConfigDescriptors belong to provided device.
	 * @param device: Device object.
	 * @param numConfigurations: bNumConfigurations in DeviceDescriptor.
	 * @return List<ConfigDescriptor>
	 */
	public static List<ConfigDescriptor> listConfigDescriptors(Device device, int numConfigurations) {
		List<ConfigDescriptor> list = new LinkedList<>();

		for (byte i = 0; i < numConfigurations; i += 1) {
			ConfigDescriptor descriptor = new ConfigDescriptor();
			int result = LibUsb.getConfigDescriptor(device, i, descriptor);

			if (result != LibUsb.SUCCESS) {
				LibUsbException exception = new LibUsbException("Unable to read config descriptor", result);
				System.out.format(ExceptionUtils.toString(exception));
				continue;
			}

			list.add(descriptor);
		}

		return list;
	}

	/**
	 * Create a DeviceHandle object by provided device and pass it to callback.
	 * @param usbDevice: UsbDevice object.
	 * @param usage: DeviceHandleUsage object.
	 */
	public static void deviceHandle(UsbDevice usbDevice, DeviceHandleUsage usage) {
		DeviceHandle handle = new DeviceHandle();
		int result = LibUsb.open(usbDevice.getDevice(), handle);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to open USB device", result);
		}

		try {
			usbDevice.setDeviceHandle(handle);
			usage.call(usbDevice);
		} finally {
			LibUsb.close(handle);
		}
	}

	/**
	 * Active provided device.
	 * @param handle: DeviceHandle object.
	 */
	public static void activeDevice(DeviceHandle handle) {
		IntBuffer intBuffer = IntBuffer.allocate(8);
		int result = LibUsb.getConfiguration(handle, intBuffer);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to get Configuration", result);
		}

		int config = intBuffer.get();
		result = LibUsb.setConfiguration(handle, config);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to set Configuration", result);
		}
	}

	/**
	 * Claim interface with provided DeviceHandle and interface number, after finish that execute the callback.
	 * @param usbDevice: UsbDevice object.
	 * @param callback: ClaimInterfaceCallback object.
	 */
	public static void claimInterface(UsbDevice usbDevice, ClaimInterfaceCallback callback) {
		DeviceHandle handle = usbDevice.getDeviceHandle();
		int interfaceNumber = usbDevice.getInterfaceNumber();
		int result = LibUsb.claimInterface(handle, interfaceNumber);

		if (result != LibUsb.SUCCESS) {
			throw new LibUsbException("Unable to claim interface", result);
		}

		try {
			callback.call(usbDevice);
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