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

	private byte[] Read_Power = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01,
			0x00, 0x02, 0x00, 0x01, 0x02, (byte) 0xFE };
	private byte[] Read_Speed = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01,
			0x00, 0x02, 0x00, 0x02, 0x01, (byte) 0xFE };
	private byte[] Read_FQ = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01, 0x00,
			0x02, 0x00, 0x03, 0x00, (byte) 0xFE };
	private byte[] Read_TO = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01, 0x00,
			0x02, 0x00, (byte) 0x14, 0x17, (byte) 0xFE };
	private byte[] Read_SJ = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01, 0x00,
			0x02, 0x00, 0x04, 0x07, (byte) 0xFE };
	private byte[] Read_EQU = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01, 0x00,
			0x02, 0x00, (byte) 0x3E, (byte) 0x3D, (byte) 0xFE };
	private byte[] Read_Address = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x01,
			0x00, 0x02, 0x00, (byte) 0x3F, (byte) 0x3C, (byte) 0xFE };

	private byte[] Set_Power = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02,
			0x00, 0x05, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, (byte) 0xFE };
	private byte[] Set_Speed = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02,
			0x00, 0x05, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, (byte) 0xFE };
	private byte[] Set_FQ = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02, 0x00,
			0x05, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, (byte) 0xFE };
	private byte[] Set_TO = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02, 0x00,
			0x05, 0x00, (byte) 0x14, 0x00, 0x01, 0x00, 0x00, (byte) 0xFE };
	private byte[] Set_SJ = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02, 0x00,
			0x05, 0x00, 0x04, 0x00, 0x01, 0x00, 0x00, (byte) 0xFE };
	private byte[] Set_EQU = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02, 0x00,
			0x05, 0x00, (byte) 0x3E, 0x00, 0x01, 0x00, 0x00, (byte) 0xFE };
	private byte[] Set_Address = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02,
			0x00, 0x07, 0x00, (byte) 0x3F, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xFE };


	private String sAddress1 = "00";
	private String sAddress2 = "00";
	private String sAddress3 = "00";

	private boolean imset = false;

	private int SetStatus = 0;// 设置的状态
	private int ReadStatus = 0;// 读取的状态
	private String ReadAddress = "";

	public SetCommand() {

	}

	/**
	 * 得到设置状态
	 */

	public int SetStatus(String str) {

		if (str.equals("Set_Power")) {
			sendSerialPort(Common.Set_Power);
		} else if (str.equals("Set_Speed")) {
			sendSerialPort(Common.Set_Speed);
		} else if (str.equals("Set_FQ")) {
			sendSerialPort(Common.Set_FQ);
		} else if (str.equals("Set_TO")) {
			sendSerialPort(Common.Set_TO);
		} else if (str.equals("Set_SJ")) {
			sendSerialPort(Common.Set_SJ);
		} else if (str.equals("Set_EQU")) {
			sendSerialPort(Common.Set_EQU);
		} else if (str.equals("Set_Address")) {
			sendSerialPort(Set_Address);
		}

		/* 开启线程TODO */

		return this.SetStatus;
	}

	/**
	 * 得到读取数据
	 * 
	 * @return
	 */
	public int ReadStatus(String str) {

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

		/* 开启线程TODO */

		return this.ReadStatus;
	}

	/*
	 * 完成取、解析//TODO
	 */
	Thread CommandThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true)// FLAG设置标志，结束时释放
			{
				byte[] BUFFER = new byte[8000];
				int size = 0;
				outpt = 0;
				inpt = 0;
				try {
					if (mInputStream == null) {

						LogInfo.LogW("读卡线程mInputStream为NULL");
						return;
					}
					size = mInputStream.read(BUFFER);
					if (size >= 8000) {
						LogInfo.LogW("接收数据越界>8000");
					} else if (size > 0) {
						for (int i = 0; i < size; i++) {
							RecBuff[inpt] = BUFFER[i]; // 保存接收的数据
							inpt = (inpt + 1) % BuffSize;
							// recnum++;
						}
						// 开始解析
						while (outpt < size) {
							int CMD_Type = 0;
							int Data_Len = 0;

							if (Rec_state == WAIT_STX)// 等待数据头状态
							{
								if (RecBuff[outpt] == (byte) 0xFF) {
									// System.out.println("F1收到头");
									Rec_state = WAIT_ETX;
									outpt++;
									SizeOfPcMsgPack = 0;
								} else {
									outpt++;
								}
							} else if (Rec_state == WAIT_ETX)// 等尾状态
							{
								if (RecBuff[outpt] == (byte) 0xFD)// 判断是否转义字符
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
										// 收到错误的转义字符，说明这个指令是错误的，都扔了，重新开始接受下一个指令
										Rec_state = WAIT_STX;
										SizeOfPcMsgPack = 0;
										break;
									}
									outpt += 2;
								} else {
									if (RecBuff[outpt] == (byte) 0xFE) // 帧尾,表示完整的数据包已经接收完成
									{
										// System.out.println("F1收到尾");
										Rec_state = WAIT_STX;
										outpt++;
										if ((PcMsgPack[4] < 0)
												|| (PcMsgPack[4] > 64))// 数据长度值不对
										{
											SizeOfPcMsgPack = 0;
											System.out.println("F2长度值不对，扔了1");
										} else {
											if ((PcMsgPack[4] + 6) != SizeOfPcMsgPack)// 数据长度不对,这个包可以扔了
											{
												SizeOfPcMsgPack = 0;
												System.out.println("F2长度不对，扔了");

											} else// 长度对了
											{
												// System.out.println("长度对了");
												Data_Len = PcMsgPack[4];
												CMD_Type = PcMsgPack[2];
												byte[] Data = new byte[Data_Len];
												for (int a = 0; a < Data_Len; a++)// 把Data取出来
												{
													Data[a] = PcMsgPack[a + 5];
												}
												// String s1 =
												// BytesUtil.bytesToHexString(Data);
												// System.out.println("数据"+s1);
												switch (CMD_Type) {
												case (byte) 0x03:
													// 收到卡号
													break;
												case (byte) 0x81:
													System.out.println("收到读响应");
													Read_Req_Duil(Data,
															Data_Len);
													break;
												case (byte) 0x82:
													System.out.println("收到写响应");
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

				try {
					Thread.sleep(100);
				} catch (Exception e) {

				}
			}
		}
	});

	/**
	 * 设置的状态
	 * 
	 * @param data
	 * @param datalen
	 */
	private void Set_Rqe_Duil(byte[] data, int datalen) {
		/* 检查Status */
		if (imset == false) {
			return;
		} else {
			imset = false;
			LogInfo.LogI("返回结果:" + data[0]);

			switch (data[0]) {
			case Common.REQ_STATUS_SUCCESS:

				SetStatus = Common.REQ_STATUS_SUCCESS;// 状态为读取成功
				break;
			case Common.REQ_STATUS_CMD_ERRORLEN:
				// MsgToShow = "设置失败，参数长度错误";
				SetStatus = Common.REQ_STATUS_CMD_ERRORLEN;

				break;
			case Common.REQ_STATUS_CMD_NOSPO:
				// MsgToShow = "设置失败，命令不支持";
				SetStatus = Common.REQ_STATUS_CMD_NOSPO;
				break;
			case Common.REQ_STATUS_CMD_ERRORDATA:
				// /MsgToShow = "设置失败，参数值错误";
				SetStatus = Common.REQ_STATUS_CMD_ERRORDATA;
				break;
			case Common.REQ_STATUS_CMD_MODULEERROR:
				// /MsgToShow = "设置失败，模块出错";
				SetStatus = Common.REQ_STATUS_CMD_MODULEERROR;
				break;
			default:
				break;
			}
		}
	}

	// 123124
	private void Read_Req_Duil(byte[] data, int datalen) {
		/* 判断错误 */
		int Status = data[0];

		/* 首先获取Tag */
		short Tag = 0;
		if (Status != 0) {
			/* 检查Status */
			switch (data[0]) {
			case Common.REQ_STATUS_CMD_ERRORLEN:
				// MsgToShow = "设置失败，参数长度错误";
				ReadStatus = Common.REQ_STATUS_CMD_ERRORLEN;
				break;
			case Common.REQ_STATUS_CMD_NOSPO:
				// MsgToShow = "设置失败，命令不支持";
				ReadStatus = Common.REQ_STATUS_CMD_NOSPO;
				break;
			case Common.REQ_STATUS_CMD_ERRORDATA:
				// /MsgToShow = "设置失败，参数值错误";
				ReadStatus = Common.REQ_STATUS_CMD_ERRORDATA;
				break;
			case Common.REQ_STATUS_CMD_MODULEERROR:
				// /MsgToShow = "设置失败，模块出错";
				ReadStatus = Common.REQ_STATUS_CMD_MODULEERROR;
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

		ReadAddress = sAddress1 + " " + sAddress2 + " " + sAddress3;

		LogInfo.LogI("读取到地址：" + ReadAddress);
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

		ReadStatus = fq;
		LogInfo.LogI("读到的频率为：" + ReadStatus);
	}

	/* 判断状态 */
	private void readStatus(short Tag, byte[] data) {
		switch (Tag) {
		case Common.TAG_POWER:
			/* 判断状态 */
			ReadStatus = (int) data[5];
			LogInfo.LogI("读到的功率为：" + ReadStatus);
			break;
		case Common.TAG_SPEED:
			ReadStatus = (int) data[5];
			LogInfo.LogI("读到的速率为：" + ReadStatus);
			break;
		case Common.TAG_FQ:
			Read_FQ_Req(data);
			break;
		case Common.TAG_TO:

			ReadStatus = (int) data[5] - 1;
			LogInfo.LogI("读到的时间间隔：" + ReadStatus);
			break;
		case Common.TAG_DECAY:
			ReadStatus = (int) data[5];
			LogInfo.LogI("读到的衰减为：" + ReadStatus);

			break;
		case Common.TAG_EQU:
			ReadStatus = (int) data[5];
			LogInfo.LogI("读到的设备类型为：" + ReadStatus);
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
	/** 串口发数据 **/
	/** 串口发送字节数据 **/
	/**********************************************************************/
	protected void sendSerialPort(byte[] output) {
		try {
			mOutputStream.write(output);
			LogInfo.LogI("发送串口数据");
		} catch (Exception e) {
		}
	}
}
