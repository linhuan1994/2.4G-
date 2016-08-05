package com.zokin.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.zokin.common.BytesUtil;
import com.zokin.common.Common;
import com.zokin.common.LogInfo;

public class SetCommand {

	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private int BuffSize = 16000;
	private byte[] RecBuff = new byte[BuffSize];
	private int inpt = 0;
	private int outpt = 0;

	private int Rec_state = 100;
	private int WAIT_STX = 100;
	private int WAIT_ETX = 200;

	private int PackMaxSize = 64;
	private byte[] PcMsgPack = new byte[PackMaxSize];
	private int SizeOfPcMsgPack = 0;

	private String sAddress1 = "00";
	private String sAddress2 = "00";
	private String sAddress3 = "00";

	private int SetStatus = 0;// ���õ�״̬
	private int ReadStatus = 0;// ��ȡ��״̬
	private String ReadAddress = "";

	public SetCommand(InputStream input, OutputStream output) {
		mInputStream = input;
		mOutputStream = output;
	}

	/**
	 * �õ�������Ӧ
	 */

	public int SetStatus(String str, int choose) {

		if (str.equals("Set_Power")) {
			sendSerialPort(setData(str,Common.Set_Power, choose));
		} else if (str.equals("Set_Speed")) {
			sendSerialPort(setData(str,Common.Set_Speed, choose));
		} else if (str.equals("Set_FQ")) {
			sendSerialPort(setData(str,Common.Set_FQ, choose));
		} else if (str.equals("Set_TO")) {
			sendSerialPort(setData(str,Common.Set_TO, choose));
		} else if (str.equals("Set_SJ")) {
			sendSerialPort(setData(str,Common.Set_SJ, choose));
		} else if (str.equals("Set_EQU")) {
			//sendSerialPort(Common.Set_EQU);
		} else if (str.equals("Set_Address")) {
			//sendSerialPort(Common.Set_Address);
		}

		/* �����߳�TODO */
		new Thread(CommandThread).start();

		try {
			// �ȴ���Ӧ
			Thread.sleep(50);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return this.SetStatus;
	}

	/**
	 * ���ò���
	 */
	private byte[] setData(String str, byte[] set, int choose) {

		if (str.equals("Set_FQ")) {
			switch (choose) {
			case 0:
				set[10] = (byte) 97;
				break;
			case 1:
				set[10] = (byte) 94;
				break;
			case 2:
				set[10] = (byte) 91;
				break;
			case 3:
				set[10] = (byte) 88;
				break;
			case 4:
				set[10] = (byte) 85;
				break;
			case 5:
				set[10] = (byte) 82;
				break;
			case 6:
				set[10] = (byte) 79;
				break;
			case 7:
				set[10] = (byte) 76;
				break;
			default:
				break;
			}
		} else {
			set[10] = (byte) choose;
		}

		int aa = 0;
		byte[] bb = new byte[10];
		byte LRC;
		for (aa = 0; aa < 10; aa++) {
			bb[aa] = set[1 + aa];
		}
		LRC = BytesUtil.creatLRC(bb);
		set[11] = LRC;
		return set;
	}

	/**
	 * �õ���ȡ����
	 * 
	 * @return Object
	 */
	public Object ReadStatus(String str) {

		if (str.equals("Read_Power")) {
			sendSerialPort(Common.Read_Power);
		} else if (str.equals("Read_Speed")) {
			sendSerialPort(Common.Read_Speed);
		} else if (str.equals("Read_FQ")) {
			sendSerialPort(Common.Read_FQ);
		} else if (str.equals("Read_TO")) {
			sendSerialPort(Common.Read_TO);
		} else if (str.equals("Read_SJ")) {
			sendSerialPort(Common.Read_SJ);
		} else if (str.equals("Read_EQU")) {
			sendSerialPort(Common.Read_EQU);
		} else if (str.equals("Read_Address")) {
			sendSerialPort(Common.Read_Address);
		}

		/* �����߳�TODO */
		new Thread(CommandThread).start();

		try {
			// �ȴ���Ӧ
			Thread.sleep(50);
		} catch (Exception e) {
			// TODO: handle exception
		}
		// ���ڵ�ַ���������ò�����ͬ������Ҫ�ֿ�
		if (str.equals("Read_Address"))
			return this.ReadAddress;
		else {
			return this.ReadStatus;
		}

	}

	/*
	 * ���ȡ������//TODO
	 */
	Thread CommandThread = new Thread(new Runnable() {

		@Override
		public void run() {

			byte[] BUFFER = new byte[8000];
			int size = 0;
			// ��ʼ��
			outpt = 0;
			inpt = 0;
			Rec_state = 100;
			WAIT_STX = 100;
			WAIT_ETX = 200;
			try {
				if (mInputStream == null) {

					LogInfo.LogW("��������mInputStreamΪNULL");
					return;
				}
				size = mInputStream.read(BUFFER);
				if (size >= 8000) {
					LogInfo.LogW("�������ý�������Խ��>8000");
				} else if (size > 0) {
					for (int i = 0; i < size; i++) {
						RecBuff[inpt] = BUFFER[i]; // ������յ�����
						inpt = (inpt + 1) % BuffSize;
						// recnum++;
					}
					// ��ʼ����
					while (outpt < size) {
						int CMD_Type = 0;
						int Data_Len = 0;

						if (Rec_state == WAIT_STX)// �ȴ�����ͷ״̬
						{
							if (RecBuff[outpt] == (byte) 0xFF) {
								// System.out.println("F1�յ�ͷ");
								Rec_state = WAIT_ETX;
								outpt++;
								SizeOfPcMsgPack = 0;
							} else {
								outpt++;
							}
						} else if (Rec_state == WAIT_ETX)// ��β״̬
						{
							if (RecBuff[outpt] == (byte) 0xFD)// �ж��Ƿ�ת���ַ�
							{
								byte zy = RecBuff[outpt + 1];
								switch (zy) {
								case 0x00:
									PcMsgPack[SizeOfPcMsgPack++] = (byte) 0xFF;
									break;
								case 0x01:
									PcMsgPack[SizeOfPcMsgPack++] = (byte) 0xFE;
									break;
								case 0x02:
									PcMsgPack[SizeOfPcMsgPack++] = (byte) 0xFD;
									break;
								default:
									// �յ������ת���ַ���˵�����ָ���Ǵ���ģ������ˣ����¿�ʼ������һ��ָ��
									Rec_state = WAIT_STX;
									SizeOfPcMsgPack = 0;
									break;
								}
								outpt += 2;
							} else {
								if (RecBuff[outpt] == (byte) 0xFE) // ֡β,��ʾ���������ݰ��Ѿ��������
								{
									// System.out.println("F1�յ�β");
									Rec_state = WAIT_STX;
									outpt++;
									if ((PcMsgPack[4] < 0)
											|| (PcMsgPack[4] > 64))// ���ݳ���ֵ����
									{
										SizeOfPcMsgPack = 0;
										LogInfo.LogW("����ֵ���ԣ��ӵ�1");

									} else {
										if ((PcMsgPack[4] + 6) != SizeOfPcMsgPack)// ���ݳ��Ȳ���,�������������
										{
											SizeOfPcMsgPack = 0;
											LogInfo.LogW("����ֵ���ԣ��ӵ�2");
										} else// ���ȶ���
										{
											// System.out.println("���ȶ���");
											Data_Len = PcMsgPack[4];
											CMD_Type = PcMsgPack[2];
											byte[] Data = new byte[Data_Len];
											for (int a = 0; a < Data_Len; a++)// ��Dataȡ����
											{
												Data[a] = PcMsgPack[a + 5];
											}
											// String s1 =
											// BytesUtil.bytesToHexString(Data);
											// System.out.println("����"+s1);
											switch (CMD_Type) {
											case (byte) 0x03:
												// �յ�����
												break;
											case (byte) 0x81:
												LogInfo.LogE("�յ�����Ӧ");
												Read_Req_Duil(Data, Data_Len);
												break;
											case (byte) 0x82:
												LogInfo.LogE("�յ�д��Ӧ");
												Set_Rqe_Duil(Data, Data_Len);
												break;
											case (byte) 0x83:

												break;
											case (byte) 0x84:

												break;
											case (byte) 0x05:
												// Pack5_duil(Data_Len ,
												// Data);
											default:
												break;
											}
										}
									}
								} else {
									PcMsgPack[SizeOfPcMsgPack++] = RecBuff[outpt];
									outpt++;
								}
							}

						}

					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	});

	/**
	 * ���õ�״̬
	 * 
	 * @param data
	 * @param datalen
	 */
	private void Set_Rqe_Duil(byte[] data, int datalen) {
		/* ���Status */
		LogInfo.LogI("���ؽ��:" + data[0]);

		switch (data[0]) {
		case Common.REQ_STATUS_SUCCESS:

			SetStatus = Common.REQ_STATUS_SUCCESS;// ״̬Ϊ��ȡ�ɹ�
			break;
		case Common.REQ_STATUS_CMD_ERRORLEN:
			// MsgToShow = "����ʧ�ܣ��������ȴ���";
			SetStatus = Common.REQ_STATUS_CMD_ERRORLEN;

			break;
		case Common.REQ_STATUS_CMD_NOSPO:
			// MsgToShow = "����ʧ�ܣ����֧��";
			SetStatus = Common.REQ_STATUS_CMD_NOSPO;
			break;
		case Common.REQ_STATUS_CMD_ERRORDATA:
			// /MsgToShow = "����ʧ�ܣ�����ֵ����";
			SetStatus = Common.REQ_STATUS_CMD_ERRORDATA;
			break;
		case Common.REQ_STATUS_CMD_MODULEERROR:
			// /MsgToShow = "����ʧ�ܣ�ģ�����";
			SetStatus = Common.REQ_STATUS_CMD_MODULEERROR;
			break;
		default:
			break;
		}

	}

	private void Read_Req_Duil(byte[] data, int datalen) {
		/* �жϴ��� */
		int Status = data[0];

		/* ���Ȼ�ȡTag */
		short Tag = 0;
		if (Status != 0) {
			/* ���Status */
			switch (data[0]) {
			case Common.REQ_STATUS_CMD_ERRORLEN:
				// MsgToShow = "����ʧ�ܣ��������ȴ���";
				this.ReadStatus = Common.REQ_STATUS_CMD_ERRORLEN;
				break;
			case Common.REQ_STATUS_CMD_NOSPO:
				// MsgToShow = "����ʧ�ܣ����֧��";
				this.ReadStatus = Common.REQ_STATUS_CMD_NOSPO;
				break;
			case Common.REQ_STATUS_CMD_ERRORDATA:
				// /MsgToShow = "����ʧ�ܣ�����ֵ����";
				this.ReadStatus = Common.REQ_STATUS_CMD_ERRORDATA;
				break;
			case Common.REQ_STATUS_CMD_MODULEERROR:
				// /MsgToShow = "����ʧ�ܣ�ģ�����";
				this.ReadStatus = Common.REQ_STATUS_CMD_MODULEERROR;
				break;
			default:
				break;
			}
		} else {
			if (datalen < 4) {
				return;
			}
			if (data[2] < 0) {
				Tag = (short) ((short) (data[1] << 8) + (short) (data[2] + 256));
			} else {
				Tag = (short) ((short) (data[1] << 8) + (short) (data[2]));
			}

			readStatus(Tag, data);

		}

	}

	private void Read_Address_Req(byte[] data) {
		byte[] test = new byte[1];
		test[0] = data[5];
		sAddress1 = BytesUtil.bytesToHexString(test);
		test[0] = data[6];
		sAddress2 = BytesUtil.bytesToHexString(test);
		test[0] = data[7];
		sAddress3 = BytesUtil.bytesToHexString(test);

		this.ReadAddress = sAddress1 + " " + sAddress2 + " " + sAddress3;

		LogInfo.LogI("��ȡ����ַ��" + ReadAddress);
	}

	private void Read_FQ_Req(byte[] data) {
		int fq = 0;
		switch (data[5]) {
		case (byte) 0x61:
			fq = 0;
			break;
		case (byte) 0x5E:
			fq = 1;
			break;
		case (byte) 0x5B:
			fq = 2;
			break;
		case (byte) 0x58:
			fq = 3;
			break;
		case (byte) 0x55:
			fq = 4;
			break;
		case (byte) 0x52:
			fq = 5;
			break;
		case (byte) 0x4F:
			fq = 6;
			break;
		case (byte) 0x4C:
			fq = 7;
			break;
		default:
			break;
		}

		this.ReadStatus = fq;
		LogInfo.LogI("������Ƶ��Ϊ��" + ReadStatus);
	}

	/* �ж�״̬ */
	private void readStatus(short Tag, byte[] data) {
		switch (Tag) {
		case Common.TAG_POWER:
			/* �ж�״̬ */
			this.ReadStatus = (int) data[5];
			LogInfo.LogI("�����Ĺ���Ϊ��" + ReadStatus);
			break;
		case Common.TAG_SPEED:
			this.ReadStatus = (int) data[5];
			LogInfo.LogI("����������Ϊ��" + ReadStatus);
			break;
		case Common.TAG_FQ:
			Read_FQ_Req(data);
			break;
		case Common.TAG_TO:

			this.ReadStatus = (int) data[5];
			LogInfo.LogI("������ʱ������" + ReadStatus);
			break;
		case Common.TAG_DECAY:
			this.ReadStatus = (int) data[5];
			LogInfo.LogI("������˥��Ϊ��" + ReadStatus);

			break;
		case Common.TAG_EQU:
			this.ReadStatus = (int) data[5];
			LogInfo.LogI("�������豸����Ϊ��" + ReadStatus);
			break;
		case Common.TAG_ADDRESS:
			Read_Address_Req(data);
			break;
		default:
			break;
		}

	}

	/**********************************************************************/
	/** sendSerialPort **/
	/** ���ڷ����� **/
	/** ���ڷ����ֽ����� **/
	/**********************************************************************/
	protected void sendSerialPort(byte[] output) {
		try {
			mOutputStream.write(output);
			try {
				// 50ms�ȴ���Ӧ
				Thread.sleep(20);
			} catch (Exception e) {

			}
			LogInfo.LogI("���ʹ�������");
		} catch (Exception e) {
		}
	}
}
