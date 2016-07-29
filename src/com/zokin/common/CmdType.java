package com.zokin.common;

public class CmdType {
	//读取参数请求
	public static final byte READ_PARAMETER_REQ=(byte)0x01;
	//读取参数响应
	public static final byte READ_PARAMETER_RES=(byte)0x81;
	//设置参数请求
	public static final byte SET_PARAMETER_REQ=(byte)0x02;
	//设置参数响应
	public static final byte SET_PARAMETER_RES=(byte)0x82;
	//上传卡号请求
	public static final byte UPLOAD_CARD_REQ=(byte)0x03;
	//上传卡号响应
	public static final byte UPLOAD_CARD_RES=(byte)0x83;		

}
