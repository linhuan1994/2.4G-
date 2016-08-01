/**
 * 文件名：Zokin
 * 描    述：提供读卡与参数设置
 * 作    者：gxLin
 * 时    间：2016/7/28 10:08
 */
package com.zokin.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.zokin.common.Application;
import com.zokin.common.LogInfo;

import android.app.Activity;
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
		setCommand = new SetCommand(mInputStream, mOutputStream);
		
	}

	public void endZokin() {
		closeSerialPort();
	}

	protected void openSerialPort() {
		try {
			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			LogInfo.LogI("zokin串口打开");
			
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
			LogInfo.LogI("zokin串口关闭");
		} catch (Exception e) {
		}
		mSerialPort = null;
	}

}
