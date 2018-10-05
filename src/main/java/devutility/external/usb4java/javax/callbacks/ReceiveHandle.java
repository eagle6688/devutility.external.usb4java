package devutility.external.usb4java.javax.callbacks;

public interface ReceiveHandle {
	/**
	 * call
	 * @param Numbers of bytes have been transfered.
	 * @param buffer: Bytes buffer.
	 */
	void call(int numbers, byte[] buffer);
}