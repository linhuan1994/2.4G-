/**
 * 文件名：LogInfo
 * 描    述：打印提示信息
 * 作    者：gxLin
 * 时    间：2016/7/28 10:08
 */

package com.zokin.common;

import android.util.Log;

public class LogInfo {

	final private static String name = "gxLin_Test";

	/**
	 * 方法名：LogD(String str) 
	 * @param功能：提示调试信息 
	 * @param参数：String 
	 * 返回值：无
	 */
	public static void LogD(String str) {
		Log.d(name, str);
	}

	/**
	 * 方法名：LogI(String str) 
	 * @param功能：提示信息 
	 * @param参数：String 
	 * 返回值：无
	 */
	

	public static void LogI(String str) {
		Log.i(name, str);
	}

	/**
	 * 方法名：LogE(String str) 
	 * @param功能：提示错误信息 
	 * @param参数：String 
	 * 返回值：无
	 */
	public static void LogE(String str) {
		Log.e(name, str);
	}

	/**
	 * 方法名：LogW(String str) 
	 * @param功能：提示警告信息 
	 * @param参数：String 
	 * 返回值：无
	 */
	public static void LogW(String str) {
		Log.w(name, str);
	}

}
