package devutility.external.usb4java.javax.listeners;

import javax.usb.UsbException;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;

import devutility.external.usb4java.javax.callbacks.ReceiveHandle;
import devutility.internal.lang.ExceptionUtils;

public class ReceiveListener implements Runnable {
	private boolean abort = false;
	private UsbPipe usbPipe = null;
	private ReceiveHandle receiveHandle = null;

	public ReceiveListener(UsbPipe usbPipe, ReceiveHandle receiveHandle) {
		this.usbPipe = usbPipe;
		this.receiveHandle = receiveHandle;
	}

	public ReceiveListener(UsbPipe usbPipe) {
		this(usbPipe, null);
	}

	public ReceiveListener() {
		this(null);
	}

	@Override
	public void run() {
		int transferredNumber = 0;
		byte[] buffer = new byte[UsbUtil.unsignedInt(usbPipe.getUsbEndpoint().getUsbEndpointDescriptor().wMaxPacketSize())];

		while (!abort) {
			try {
				transferredNumber = usbPipe.syncSubmit(buffer);

				if (receiveHandle != null) {
					receiveHandle.call(transferredNumber, buffer);
				}
			} catch (UsbException exception) {
				if (!abort) {
					System.out.format("Unable to submit data buffer to device: %s\n", ExceptionUtils.toString(exception));
					break;
				}
			}
		}
	}

	public void abort() {
		setAbort(true);
	}

	public boolean isAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
		usbPipe.abortAllSubmissions();
	}

	public ReceiveHandle getReceiveHandle() {
		return receiveHandle;
	}

	public void setReceiveHandle(ReceiveHandle receiveHandle) {
		this.receiveHandle = receiveHandle;
	}
}