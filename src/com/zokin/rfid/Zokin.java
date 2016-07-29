/**
 * �ļ�����Zokin
 * ��    �����ṩ�������������
 * ��    �ߣ�gxLin
 * ʱ    �䣺2016/7/28 10:08
 */
package com.zokin.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.zokin.common.Application;

import android.app.Activity;
import android.util.Log;
import android_serialport_api.SerialPort;
public class Zokin {

	public ReadCard readCard;
	public SetCommand setCommand;

	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	protected InputStream mInputStream;

	public Zokin(Activity activity) {

		mApplication = (Application) activity.getApplication();
		openSerialPort();
		readCard = new ReadCard(mInputStream, mOutputStream);
		setCommand = new SetCommand();
	}

	public void endZokin() {
		closeSerialPort();
	}

	protected void openSerialPort() {
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
		} catch (SecurityException e) {
		} catch (IOException e) {
		} catch (InvalidParameterException e) {
		}
	}

	protected void closeSerialPort() {
		try {
			mInputStream.close();
			mOutputStream.close();
			mApplication.closeSerialPort();
			Log.i("gxLin", "���ڹر�");
		} catch (Exception e) {
		}
		mSerialPort = null;
	}

}
