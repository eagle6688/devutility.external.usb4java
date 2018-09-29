package devutility.external.usb4java.enums;

public enum UsbDeviceErrorCode {
	/**
	 * Unknow
	 */
	UNKNOWN(0),

	/**
	 * No Context
	 */
	NOCONTEXT(-101);

	private int value;

	UsbDeviceErrorCode(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public static UsbDeviceErrorCode parse(int value) {
		UsbDeviceErrorCode[] array = UsbDeviceErrorCode.values();

		for (int i = 0; i < array.length; i++) {
			if (value == array[i].value()) {
				return array[i];
			}
		}

		return UsbDeviceErrorCode.UNKNOWN;
	}
}