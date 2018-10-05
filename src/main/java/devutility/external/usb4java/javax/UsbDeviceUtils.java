package devutility.external.usb4java.javax;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;

public class UsbDeviceUtils {
	/**
	 * Link a device and claim provided interface.
	 * @param idVendor: Device vendorId.
	 * @param idProduct: Device productId.
	 * @param interfaceNumber: Interface number.
	 * @param endpointAddress: Endpoint address number need to be connected.
	 * @return UsbPipe
	 * @throws SecurityException
	 * @throws UsbException
	 */
	public static UsbPipe linkDevice(short idVendor, short idProduct, byte interfaceNumber, byte endpointAddress) throws SecurityException, UsbException {
		UsbDevice device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub(), idVendor, idProduct);

		if (device == null) {
			String message = String.format("UsbDevice with vendorId %x productId %x not found!", idVendor, idProduct);
			throw new UsbException(message);
		}

		UsbConfiguration configuration = device.getActiveUsbConfiguration();

		if (configuration == null) {
			throw new UsbException("No active configuration!");
		}

		UsbInterface iface = configuration.getUsbInterface(interfaceNumber);

		if (iface == null) {
			String message = String.format("Cannot find interface %x!", interfaceNumber);
			throw new UsbException(message);
		}

		iface.claim();
		UsbEndpoint endpoint = iface.getUsbEndpoint(endpointAddress);

		if (endpoint == null) {
			String message = String.format("Cannot find endpoint %d!", endpointAddress);
			throw new UsbException(message);
		}

		return endpoint.getUsbPipe();
	}

	/**
	 * Find device.
	 * @param hub: UsbHub object.
	 * @param vendorId: Device vendorId.
	 * @param productId: Device productId.
	 * @return UsbDevice
	 */
	public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
		@SuppressWarnings("unchecked")
		List<UsbDevice> usbDevices = (List<UsbDevice>) hub.getAttachedUsbDevices();

		for (UsbDevice device : usbDevices) {
			UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

			if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
				return device;
			}

			if (device.isUsbHub()) {
				device = findDevice((UsbHub) device, vendorId, productId);

				if (device != null) {
					return device;
				}
			}
		}

		return null;
	}
}