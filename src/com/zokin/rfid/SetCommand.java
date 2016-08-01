package com.zokin.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.zokin.common.BytesUtil;

import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android_serialport_api.SerialPort;

public class SetCommand {
	
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private int BuffSize = 16000;
	private byte[] RecBuff = new byte[BuffSize];
	private int inpt = 0;
	private int outpt = 0;
	
	final private int MSG_UPDATE_POWER = 0xFFFE;
	final private int MSG_UPDATE_SPEED = 0xFFFD;
	final private int MSG_UPDATE_FQ = 0xFFFC;
	final private int MSG_UPDATE_TO = 0xFFFB;
	final private int MSG_UPDATE_DECAY = 0xFFFA;
	final private int MSG_UPDATE_EQUTYPE = 0xFFF9;
	final private int MSG_UPDATE_ADDRESS = 0xFFF8;
	final private int MSG_SHOW_Tisk = 0xFFB0;
	
	private int Rec_state= 100;
	private int WAIT_STX = 100;
	private int WAIT_ETX = 200;

	private int PackMaxSize = 64;
	private byte[] PcMsgPack = new byte[PackMaxSize];
	private byte[] PcMsgData = new byte[PackMaxSize];

	private int SizeOfPcMsgPack = 0;
	private int SizeOfPcMsgData = 0;
	
	final private short TAG_POWER = 0x0001;
	final private short TAG_SPEED = 0x0002;
	final private short TAG_FQ = 0x0003;
	final private short TAG_TO = 0x0014;
	final private short TAG_DECAY = 0x0004;
	final private short TAG_EQU = 0x003E;
	final private short TAG_ADDRESS = 0x003F;

	final private int REQ_STATUS_SUCCESS = 0;
	final private int REQ_STATUS_CMD_ERRORLEN = -1;
	final private int REQ_STATUS_CMD_NOSPO = -2;
	final private int REQ_STATUS_CMD_ERRORDATA = -3;
	final private int REQ_STATUS_CMD_MODULEERROR = -4;
	
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
	// private byte[] Set_Address = new byte[]{(byte)0xFF ,0x00 ,0x00 , 0x02 ,
	// 0x00 , 0x07 , 0x00 , (byte)0x3F , 0x00, 0x03 , 0x00 , 0x00 , 0x00 , 0x00
	// , (byte)0xFE};

	// private byte[] Statr_Inv = new byte[]{(byte)0xFF , 0x00 , 0x00 , 0x02 ,
	// 0x00 , 0x05 , 0x00 , 0x20 , 0x00 , 0x01 , 0x01 , 0x27 , (byte)0xFE};
	private byte[] Stop_Inv = new byte[] { (byte) 0xFF, 0x00, 0x00, 0x02, 0x00,
			0x05, 0x00, 0x20, 0x00, 0x01, 0x00, 0x26, (byte) 0xFE };
	
	private int nPower_sel = 0;
	private int nSpeed_sel = 0;
	private int nFQ_sel = 0;
	private int nTO_sel = 0;
	private int nDecay_sel = 0;
	private int nEquType_sel = 0;
	private String sAddress1 = "00";
	private String sAddress2 = "00";
	private String sAddress3 = "00";

	private boolean imset = false;
	
	private Spinner Spn_Power;
	private Button Btn_Power_Read;
	private Button Btn_Power_Set;
	private long recnum2 = 0;
	private long duilnum2 = 0;
	private long buffnum2 = 0;
	
		
	public SetCommand()
	{
		
	}
	/*
	 * 完成取、解析、并发送到handler，1000ms完成一次//TODO
	 */
	Thread CommandThread = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true)// FLAG设置标志，结束时释放
			{
				byte[] BUFFER = new byte[8000];
				int size = 0;
				outpt=0;
				inpt=0;
				try {
					if (mInputStream == null) {
						Log.w("gxLin","读卡线程mInputStream为NULL");
						return;
					}
					size = mInputStream.read(BUFFER);
					if (size >= 8000) {
						Log.w("gxLin","接收数据越界>8000");
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
								}
								else {
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
													Read_Req_Duil(Data, Data_Len);
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
					Thread.sleep(200);
				} catch (Exception e) {

				}
			}
		}
	});

	
	void Set_Rqe_Duil(byte[] data, int datalen) {
		/* 检查Status */
		if (imset == false) {
			return;
		} else {
			imset = false;
			System.out.print("返回结果:" + data[0]);
			switch (data[0]) {
			case REQ_STATUS_SUCCESS:
				Message msg1 = new Message();
				msg1 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.setsuccess));
				myHandler.sendMessage(msg1);
				break;
			case REQ_STATUS_CMD_ERRORLEN:
				// MsgToShow = "设置失败，参数长度错误";
				Message msg2 = new Message();
				
				msg2 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.setfaildatalen));
				myHandler.sendMessage(msg2);
				break;
			case REQ_STATUS_CMD_NOSPO:
				// MsgToShow = "设置失败，命令不支持";
				Message msg3 = new Message();
				msg3 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.setfailnosupp));
				myHandler.sendMessage(msg3);
				break;
			case REQ_STATUS_CMD_ERRORDATA:
				// /MsgToShow = "设置失败，参数值错误";
				Message msg4 = new Message();
				msg4 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.setfaildata));
				myHandler.sendMessage(msg4);
				break;
			case REQ_STATUS_CMD_MODULEERROR:
				// /MsgToShow = "设置失败，模块出错";
				Message msg5 = new Message();
				msg5 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.setfailmodule));
				myHandler.sendMessage(msg5);
				break;
			default:
				break;
			}
		}
	}

	// 123124
	void Read_Req_Duil(byte[] data, int datalen) {
		/* 判断错误 */
		int Status = data[0];

		/* 首先获取Tag */
		short Tag = 0;
		if (Status != 0) {
			/* 检查Status */
			switch (data[0]) {
			case REQ_STATUS_CMD_ERRORLEN:
				// MsgToShow = "设置失败，参数长度错误";
				Message msg2 = new Message();
				msg2 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.readfaildatalen));
				myHandler.sendMessage(msg2);
				break;
			case REQ_STATUS_CMD_NOSPO:
				// MsgToShow = "设置失败，命令不支持";
				Message msg3 = new Message();
				msg3 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.readfailnosupp));
				myHandler.sendMessage(msg3);
				break;
			case REQ_STATUS_CMD_ERRORDATA:
				// /MsgToShow = "设置失败，参数值错误";
				Message msg4 = new Message();
				msg4 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.readfaildata));
				myHandler.sendMessage(msg4);
				break;
			case REQ_STATUS_CMD_MODULEERROR:
				// /MsgToShow = "设置失败，模块出错";
				Message msg5 = new Message();
				msg5 = myHandler.obtainMessage(MSG_SHOW_Tisk,
						getString(R.string.readfailmodule));
				myHandler.sendMessage(msg5);
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
			switch (Tag) {
			case TAG_POWER:
				/* 判断状态 */
				Read_Power_Req(datalen, data);
				break;
			case TAG_SPEED:
				Read_Speed_Req(datalen, data);
				break;
			case TAG_FQ:
				Read_FQ_Req(datalen, data);
				break;
			case TAG_TO:
				Read_TO_Req(datalen, data);
				break;
			case TAG_DECAY:
				Read_Decay_Req(datalen, data);
				break;
			case TAG_EQU:
				Read_EquType_Req(datalen, data);
				break;
			case TAG_ADDRESS:
				Read_Address_Req(datalen, data);
				break;
			default:
				break;
			}
			
		}

	}
	void Read_Address_Req(int datalen, byte[] data) {
		byte[] test = new byte[1];
		test[0] = data[5];
		sAddress1 = BytesUtil.bytesToHexString(test);
		test[0] = data[6];
		sAddress2 = BytesUtil.bytesToHexString(test);
		test[0] = data[7];
		sAddress3 = BytesUtil.bytesToHexString(test);

		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_ADDRESS);
		myHandler.sendMessage(msg);
	}

	void Read_Decay_Req(int datalen, byte[] data) {
		System.out.println("读到的衰减为：" + data[5]);
		int power = (int) data[5];
		System.out.println("@" + power);
		nDecay_sel = power;
		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_DECAY);
		myHandler.sendMessage(msg);
	}

	void Read_EquType_Req(int datalen, byte[] data) {
		System.out.println("读到的设备类型为：" + data[5]);
		int power = (int) data[5];
		System.out.println("@" + power);
		nEquType_sel = power;
		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_EQUTYPE);
		myHandler.sendMessage(msg);
	}

	void Read_Power_Req(int datalen, byte[] data) {
		System.out.println("读到的功率为：" + data[5]);
		int power = (int) data[5];
		System.out.println("@" + power);
		nPower_sel = power;
		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_POWER);
		myHandler.sendMessage(msg);
	}

	void Read_Speed_Req(int datalen, byte[] data) {
		System.out.println("读到的速率为：" + data[5]);
		int speed = (int) data[5];
		System.out.println("@" + speed);
		nSpeed_sel = speed;
		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_SPEED);
		myHandler.sendMessage(msg);
	}

	void Read_FQ_Req(int datalen, byte[] data) {
		System.out.println("读到的频率为：" + data[5]);
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

		nFQ_sel = fq;
		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_FQ);
		myHandler.sendMessage(msg);
	}

	void Read_TO_Req(int datalen, byte[] data) {
		System.out.println("读到的时间间隔：" + data[5]);
		int to = (int) data[5];
		System.out.println("@" + to);
		to = to - 1;
		nTO_sel = to;
		Message msg = new Message();
		msg = myHandler.obtainMessage(MSG_UPDATE_TO);
		myHandler.sendMessage(msg);
	}
	
	
	/**********************************************************************/
	/** sendSerialPort **/
	/** 串口发数据 **/
	/** 串口发送字节数据 **/
	/**********************************************************************/
	protected void sendSerialPort(byte[] output) {
		try {
			mOutputStream.write(output);
			Log.i("gxLin","发送串口数据");
		} catch (Exception e) {
		}
	}
}
