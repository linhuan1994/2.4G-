package com.zokin.common;

public class BytesUtil {

	/**
	 * byte����ת����int���ͣ���λ��ǰ
	 * 
	 * @param bb��������
	 * @return
	 * @date 2014��7��23�� ����8:04:07 lixianghuang
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
	 * byte������ȡint��ֵ��������������(��λ��ǰ����λ�ں�)��˳��
	 * 
	 * @param bb
	 * @return
	 * @date 2014��7��23�� ����8:18:27 lixianghuang
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
	 * ��int��ֵת��Ϊռ�ĸ��ֽڵ�byte���飬������������(��λ��ǰ����λ�ں�)��˳��
	 * 
	 * @param valueҪת����intֵ
	 * @return byte����
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
	 * ��int��ֵת��Ϊռ�ĸ��ֽڵ�byte���飬������������(��λ��ǰ����λ�ں�)��˳��
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
	 * ���ڴ�ӡ��ʾ
	 * 
	 * @param data
	 * @return
	 * @date 2014��7��23�� ����4:16:06 lixianghuang
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
