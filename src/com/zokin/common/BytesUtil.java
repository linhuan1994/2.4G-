package com.zokin.common;

public class BytesUtil {

	/**
	 * byte数组转换成int类型，高位在前
	 * 
	 * @param bb传入数组
	 * @return
	 * @date 2014年7月23日 下午8:04:07 lixianghuang
	 */
	public static int bytesToIntHighFirst(byte[] bb, int offset, int length) {
		int value = 0;
		switch (length) {
		case 1:
			value = (int) bb[offset];
			break;
		case 2:
			value = (int) ((bb[offset + 1] & 0xFF) | ((bb[offset] << 8) & 0xFF00));
			break;
		case 3:
			value = (int) ((bb[offset + 2] & 0xFF)
					| ((bb[offset + 1] << 8) & 0xFF00) | ((bb[offset] << 16) & 0xFF0000));
			break;
		case 4:
			value = (int) ((bb[offset + 3] & 0xFF)
					| ((bb[offset + 2] << 8) & 0xFF00)
					| ((bb[offset + 1] << 16) & 0xFF0000) | ((bb[offset] << 24) & 0xFF000000));
			break;
		default:
			break;
		}

		return value;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
	 * 
	 * @param bb
	 * @return
	 * @date 2014年7月23日 下午8:18:27 lixianghuang
	 */
	public static int bytesToIntLowFirst(byte[] bb, int offset, int length) {
		int value = 0;
		switch (length) {
		case 1:
			value = (int) bb[offset];
			break;
		case 2:
			value = (int) ((bb[offset] & 0xFF) | ((bb[offset + 1] << 8) & 0xFF00));
			break;
		case 3:
			value = (int) ((bb[offset] & 0xFF)
					| ((bb[offset + 1] << 8) & 0xFF00) | ((bb[offset + 2] << 16) & 0xFF0000));
			break;
		case 4:
			value = (int) ((bb[offset] & 0xFF)
					| ((bb[offset + 1] << 8) & 0xFF00)
					| ((bb[offset + 2] << 16) & 0xFF0000) | ((bb[offset + 3] << 24) & 0xFF000000));
			break;
		default:
			break;
		}

		return value;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
	 * 
	 * @param value要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytesLowFirst(int value) {
		byte[] bb = new byte[4];
		bb[3] = (byte) ((value >> 24) & 0xFF);
		bb[2] = (byte) ((value >> 16) & 0xFF);
		bb[1] = (byte) ((value >> 8) & 0xFF);
		bb[0] = (byte) (value & 0xFF);
		return bb;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
	 */
	public static byte[] intToBytesHighFirst(int value) {
		byte[] bb = new byte[4];
		bb[0] = (byte) ((value >> 24) & 0xFF);
		bb[1] = (byte) ((value >> 16) & 0xFF);
		bb[2] = (byte) ((value >> 8) & 0xFF);
		bb[3] = (byte) (value & 0xFF);
		return bb;
	}

	public static byte creatLRC(byte[] data) {
		byte mLRC = 0x00;
		for (int i = 0; i < data.length; i++) {
			mLRC = (byte) (mLRC ^ data[i]);
		}
		return mLRC;
	}

	/**
	 * 用于打印显示
	 * 
	 * @param data
	 * @return
	 * @date 2014年7月23日 下午4:16:06 lixianghuang
	 */
	public static String bytesToHexString(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		if (data == null || data.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < data.length; i++) {
			buffer[0] = Character.forDigit((data[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(data[i] & 0x0F, 16);
			stringBuilder.append(buffer);
			// stringBuilder.append(" ");
		}
		return stringBuilder.toString().toUpperCase().trim();
	}

	public static String bytesToHexStringReverse(byte[] data) {
		StringBuilder stringBuilder = new StringBuilder();
		if (data == null || data.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < data.length; i++) {
			buffer[0] = Character.forDigit(
					(data[data.length - 1 - i] >>> 4) & 0x0F, 16);
			buffer[1] = Character
					.forDigit(data[data.length - 1 - i] & 0x0F, 16);
			stringBuilder.append(buffer);
			// stringBuilder.append(" ");
		}
		return stringBuilder.toString().toUpperCase().trim();
	}

	public static String byteToHexString(byte data) {
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char[2];
		buffer[0] = Character.forDigit((data >>> 4) & 0x0F, 16);
		buffer[1] = Character.forDigit(data & 0x0F, 16);
		stringBuilder.append(buffer);
		stringBuilder.append(" ");
		return stringBuilder.toString().toUpperCase().trim();
	}

}
