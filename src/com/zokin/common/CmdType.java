package com.zokin.common;

public class CmdType {
	//��ȡ��������
	public static final byte READ_PARAMETER_REQ=(byte)0x01;
	//��ȡ������Ӧ
	public static final byte READ_PARAMETER_RES=(byte)0x81;
	//���ò�������
	public static final byte SET_PARAMETER_REQ=(byte)0x02;
	//���ò�����Ӧ
	public static final byte SET_PARAMETER_RES=(byte)0x82;
	//�ϴ���������
	public static final byte UPLOAD_CARD_REQ=(byte)0x03;
	//�ϴ�������Ӧ
	public static final byte UPLOAD_CARD_RES=(byte)0x83;		

}
