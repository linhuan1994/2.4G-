/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.zokin.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import org.apache.http.util.EncodingUtils;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.FileNotFoundException; 
import android.app.Activity;
import android.content.SharedPreferences;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import java.io.FileInputStream;


public class Application extends android.app.Application {

	
	public boolean Switch_To_Setting_Activity = false;
	
	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;
	
	public boolean BEEP_EN = true;
	public boolean KEY_LX = false;
	
	public String Devices = new String();
	public String sBaurates = new String();
	public int nBaudrates = 0;
	
	private String CnfName = "peizhi";
	
	public int CurrentFragment = 0;
	public int FragmentBeforHome = 0;
	public boolean IamHome = false;
	
	public int Count_En = 1;
	public int LowPower = 1;

	public boolean EnoughPower = true;
	/**********************************************************************/
	/** getSerialPort                                                    **/
	/** 串口初始化                                                                                                               **/
	/**  打开串口ttyMT1,波特率115200                                      **/
	/**********************************************************************/
	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		//System.out.println("########################");
			if (mSerialPort == null) {
				
				 SharedPreferences pre = getSharedPreferences(CnfName, 0);
			     String path = pre.getString("DEVICE", "/dev/ttyMT1");
			   //TODO平板是0，手机是1，切记切记
			   //由于这个原因导致调试出错的请在下面加1
			   //count : 2 
			     
			     System.out.println("path = " + path);
			 
				int baudrate2 = 115200;
			
				mSerialPort = new SerialPort(new File(path), baudrate2, 0);
				
				

			
			}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	
	public void Save_Private(String filename , String data  ) {  
	       try {  
	           /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的， 
	            * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的 
	            *   public abstract FileOutputStream openFileOutput(String name, int mode) 
	            *   throws FileNotFoundException; 
	            * openFileOutput(String name, int mode); 
	            * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名 
	            *          该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt 
	            * 第二个参数，代表文件的操作模式 
	            *          MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖 
	            *          MODE_APPEND  私有   重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件 
	            *          MODE_WORLD_READABLE 公用  可读 
	            *          MODE_WORLD_WRITEABLE 公用 可读写 
	            *  */  
	           FileOutputStream outputStream = openFileOutput(filename,  
	                   Activity.MODE_PRIVATE);  
	           outputStream.write(data.getBytes());  
	           outputStream.flush();  
	           outputStream.close();  
	            
	       } catch (FileNotFoundException e) {  
	           e.printStackTrace();  
	       } catch (IOException e) {  
	           e.printStackTrace();  
	       }  
	 
	   }
	
	public void Save_Append(String filename , String data  ) {  
	       try {  
	           /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的， 
	            * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的 
	            *   public abstract FileOutputStream openFileOutput(String name, int mode) 
	            *   throws FileNotFoundException; 
	            * openFileOutput(String name, int mode); 
	            * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名 
	            *          该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt 
	            * 第二个参数，代表文件的操作模式 
	            *          MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖 
	            *          MODE_APPEND  私有   重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件 
	            *          MODE_WORLD_READABLE 公用  可读 
	            *          MODE_WORLD_WRITEABLE 公用 可读写 
	            *  */  
	           FileOutputStream outputStream = openFileOutput(filename,  
	                   Activity.MODE_APPEND);  
	           outputStream.write(data.getBytes());  
	           outputStream.flush();  
	           outputStream.close(); 
	           
	            
	       } catch (FileNotFoundException e) {  
	           e.printStackTrace();  
	       } catch (IOException e) {  
	           e.printStackTrace();  
	       }  
	 
	   }
	
  
	 public String ReadFileData(String fileName) {  
	        String res = "";  
	        try {  
	            FileInputStream fin = openFileInput(fileName);  
	            int length = fin.available();  
	            byte[] buffer = new byte[length];  
	            fin.read(buffer);  
	            res = EncodingUtils.getString(buffer, "UTF-8");  
	            fin.close();  
	        } catch (Exception e) {  
	            e.printStackTrace();
	        }  
	        return res;  
	    } 
	 
	 public boolean DeleteFile(String filename)
	 {
		 File file = new File("/data/data/" + getPackageName().toString()
					+ "/files", filename);
		 		System.out.println("/data/data/" + getPackageName().toString()
						+ "/files");
		 		System.out.println(filename);
				if (file.exists()) 
				{
				    file.delete();
				    return true;
				}
				else
				{
					return false;
				}

	 }
	 
	 
	 /**
	  * 获取版本号
	  * @return 当前应用的版本号
	  */
	 public String getVersion() {
	     try {
	         PackageManager manager = this.getPackageManager();
	         PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	         String version = info.versionName;
	         return version;
	     } catch (Exception e) {
	         e.printStackTrace();
	         return "error";
	     }
	 }
    
}
