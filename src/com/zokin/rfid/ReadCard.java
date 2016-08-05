/**
 * 文件名：ReadCard
 * 描    述：提供卡号
 * 作    者：gxLin
 * 时    间：2016/7/28 10:08
 */
package com.zokin.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.zokin.common.BytesUtil;
import com.zokin.common.Common;
import com.zokin.common.LogInfo;

public class ReadCard {

	protected OutputStream mOutputStream;// TODO打开再关？？
	protected InputStream mInputStream;

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
	//private LinkedList<String> tagIdList = new LinkedList<String>();
	private HashMap<String, Integer> idMap = new HashMap<String, Integer>();

	private boolean isMyTurn = false;

	// private HashSet<String> dataSet = new HashSet<String>();
	// private int dataNum = 0;// 卡数量

	/**
	 * @param功能：构造函数
	 * @param参数：String
	 */
	public ReadCard(InputStream input, OutputStream output) {
		mInputStream = input;
		mOutputStream = output;
		ReadCard.start();
	}

	/**
	 * 
	 * @return 返回RFID的id
	 */
//	public LinkedList<String> getCardId() {
//		return this.tagIdList;
//	}
	public HashMap<String, Integer> getCardId() {
		return this.idMap;
	}
	
	/**
	 * @param开始读卡指令
	 */
	public void startRead() {
		//tagIdList.removeAll(tagIdList);// 清空链表数据
		idMap.clear();// 清空map
		isMyTurn = true;
		sendSerialPort(Common.Statr_Inv);
	}

	/**
	 * @param停止读卡指令
	 */
	public void stopRead() {
		sendSerialPort(Common.Stop_Inv);// Attention ：多发几个，防止接收不到
		sendSerialPort(Common.Stop_Inv);
		sendSerialPort(Common.Stop_Inv);
		isMyTurn = false;
	}

	public boolean cleanCardId() {
		
        this.idMap.clear();
		//this.tagIdList.removeAll(tagIdList);
		return true;
	}

	/*
	 * 完成读卡、解析,200ms完成一次//TODO
	 */
	Thread ReadCard = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true)// FLAG设置标志，结束时释放
			{
				if (isMyTurn) {
					byte[] BUFFER = new byte[8000];
					int size = 0;

					try {
						if (mInputStream == null) {
							LogInfo.LogW("读卡线程mInputStream为NULL");
							return;
						} else {
							size = mInputStream.read(BUFFER);// 读取缓存区
							if (Math.abs(inpt - outpt) + size >= 8000) {

								LogInfo.LogW("接收数据越界");
							} else if (size > 0) {
								for (int i = 0; i < size; i++) {
									RecBuff[inpt] = BUFFER[i]; // 保存接收的数据
									inpt = (inpt + 1) % BuffSize;
									// recnum++;
								}
								// 开始解析
								while (outpt != inpt) {// 队列不为空，表示有数据
									int CMD_Type = 0;
									int Data_Len = 0;

									if (Rec_state == WAIT_STX)// 等待数据头状态
									{
										if (RecBuff[outpt] == (byte) 0xFF) {
											// System.out.println("F1收到头");
											Rec_state = WAIT_ETX;
											outpt = (outpt + 1) % BuffSize;
											;
											SizeOfPcMsgPack = 0;
										} else {
											outpt = (outpt + 1) % BuffSize;

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

											outpt = (outpt + 2) % BuffSize;

										} else {
											if (RecBuff[outpt] == (byte) 0xFE) // 帧尾,表示完整的数据包已经接收完成
											{
												// System.out.println("F1收到尾");
												Rec_state = WAIT_STX;
												outpt = (outpt + 1) % BuffSize;

												if ((PcMsgPack[4] < 0)
														|| (PcMsgPack[4] > 64))// 数据长度值不对
												{
													SizeOfPcMsgPack = 0;
													LogInfo.LogW("数据长度不对");
												} else {
													if ((PcMsgPack[4] + 6) != SizeOfPcMsgPack)// 数据长度不对,这个包可以扔了
													{
														SizeOfPcMsgPack = 0;
														LogInfo.LogW("数据长度不对,这个包可以扔了");

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
															Pack3_duil(
																	Data_Len,
																	Data);
															break;
														case (byte) 0x81:

															break;
														case (byte) 0x82:

															break;
														case (byte) 0x83:

															break;
														case (byte) 0x84:

															break;
														case (byte) 0x05:
															// Pack5_duil(Data_Len
															// ,
															// Data);
														default:
															break;
														}
													}
												}
											} else {
												PcMsgPack[SizeOfPcMsgPack++] = RecBuff[outpt];
												outpt = (outpt + 1) % BuffSize;
											}
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
				try {
					Thread.sleep(100);
				} catch (Exception e) {

				}
			}
		}
	});

	private void Pack3_duil(int Data_Len, byte[] data) {// TODO
		int a;
		byte[] bTagid = new byte[Data_Len];
		for (a = 0; a < Data_Len; a++) {
			bTagid[a] = data[Data_Len - 1 - a];
		}
		String sTagid = new String();
		sTagid = BytesUtil.bytesToHexString(bTagid);
		// System.out.println(tagIdList.size());
		synchronized (idMap) {
			
			if (!idMap.containsKey(sTagid)) {
				idMap.put(sTagid, 1);
			} else {
				//存在则数量加1
               idMap.put(sTagid, idMap.get(sTagid)+1);
			}
		}
//		synchronized (tagIdList) {
//			tagIdList.add(sTagid);
//		}
	}

	/**********************************************************************/
	/** sendSerialPort **/
	/** 串口发数据 **/
	/** 串口发送字节数据 **/
	/**********************************************************************/
	protected void sendSerialPort(byte[] output) {
		try {
			mOutputStream.write(output);
		} catch (Exception e) {
		}
	}
}
