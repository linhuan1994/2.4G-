/**
 * �ļ�����LogInfo
 * ��    ������ӡ��ʾ��Ϣ
 * ��    �ߣ�gxLin
 * ʱ    �䣺2016/7/28 10:08
 */

package com.zokin.common;

import android.util.Log;

public class LogInfo {

	final private static String name = "gxLin_Test";

	/**
	 * ��������LogD(String str) 
	 * @param���ܣ���ʾ������Ϣ 
	 * @param������String 
	 * ����ֵ����
	 */
	public static void LogD(String str) {
		Log.d(name, str);
	}

	/**
	 * ��������LogI(String str) 
	 * @param���ܣ���ʾ��Ϣ 
	 * @param������String 
	 * ����ֵ����
	 */
	

	public static void LogI(String str) {
		Log.i(name, str);
	}

	/**
	 * ��������LogE(String str) 
	 * @param���ܣ���ʾ������Ϣ 
	 * @param������String 
	 * ����ֵ����
	 */
	public static void LogE(String str) {
		Log.e(name, str);
	}

	/**
	 * ��������LogW(String str) 
	 * @param���ܣ���ʾ������Ϣ 
	 * @param������String 
	 * ����ֵ����
	 */
	public static void LogW(String str) {
		Log.w(name, str);
	}

}
