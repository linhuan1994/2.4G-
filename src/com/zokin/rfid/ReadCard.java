/**
 * �ļ�����ReadCard
 * ��    �����ṩ����
 * ��    �ߣ�gxLin
 * ʱ    �䣺2016/7/28 10:08
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

	protected OutputStream mOutputStream;// TODO���ٹأ���
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
	// private int dataNum = 0;// ������

	/**
	 * @param���ܣ����캯��
	 * @param������String
	 */
	public ReadCard(InputStream input, OutputStream output) {
		mInputStream = input;
		mOutputStream = output;
		ReadCard.start();
	}

	/**
	 * 
	 * @return ����RFID��id
	 */
//	public LinkedList<String> getCardId() {
//		return this.tagIdList;
//	}
	public HashMap<String, Integer> getCardId() {
		return this.idMap;
	}
	
	/**
	 * @param��ʼ����ָ��
	 */
	public void startRead() {
		//tagIdList.removeAll(tagIdList);// �����������
		idMap.clear();// ���map
		isMyTurn = true;
		sendSerialPort(Common.Statr_Inv);
	}

	/**
	 * @paramֹͣ����ָ��
	 */
	public void stopRead() {
		sendSerialPort(Common.Stop_Inv);// Attention ���෢��������ֹ���ղ���
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
	 * ��ɶ���������,200ms���һ��//TODO
	 */
	Thread ReadCard = new Thread(new Runnable() {

		@Override
		public void run() {
			while (true)// FLAG���ñ�־������ʱ�ͷ�
			{
				if (isMyTurn) {
					byte[] BUFFER = new byte[8000];
					int size = 0;

					try {
						if (mInputStream == null) {
							LogInfo.LogW("�����߳�mInputStreamΪNULL");
							return;
						} else {
							size = mInputStream.read(BUFFER);// ��ȡ������
							if (Math.abs(inpt - outpt) + size >= 8000) {

								LogInfo.LogW("��������Խ��");
							} else if (size > 0) {
								for (int i = 0; i < size; i++) {
									RecBuff[inpt] = BUFFER[i]; // ������յ�����
									inpt = (inpt + 1) % BuffSize;
									// recnum++;
								}
								// ��ʼ����
								while (outpt != inpt) {// ���в�Ϊ�գ���ʾ������
									int CMD_Type = 0;
									int Data_Len = 0;

									if (Rec_state == WAIT_STX)// �ȴ�����ͷ״̬
									{
										if (RecBuff[outpt] == (byte) 0xFF) {
											// System.out.println("F1�յ�ͷ");
											Rec_state = WAIT_ETX;
											outpt = (outpt + 1) % BuffSize;
											;
											SizeOfPcMsgPack = 0;
										} else {
											outpt = (outpt + 1) % BuffSize;

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

											outpt = (outpt + 2) % BuffSize;

										} else {
											if (RecBuff[outpt] == (byte) 0xFE) // ֡β,��ʾ���������ݰ��Ѿ��������
											{
												// System.out.println("F1�յ�β");
												Rec_state = WAIT_STX;
												outpt = (outpt + 1) % BuffSize;

												if ((PcMsgPack[4] < 0)
														|| (PcMsgPack[4] > 64))// ���ݳ���ֵ����
												{
													SizeOfPcMsgPack = 0;
													LogInfo.LogW("���ݳ��Ȳ���");
												} else {
													if ((PcMsgPack[4] + 6) != SizeOfPcMsgPack)// ���ݳ��Ȳ���,�������������
													{
														SizeOfPcMsgPack = 0;
														LogInfo.LogW("���ݳ��Ȳ���,�������������");

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
				//������������1
               idMap.put(sTagid, idMap.get(sTagid)+1);
			}
		}
//		synchronized (tagIdList) {
//			tagIdList.add(sTagid);
//		}
	}

	/**********************************************************************/
	/** sendSerialPort **/
	/** ���ڷ����� **/
	/** ���ڷ����ֽ����� **/
	/**********************************************************************/
	protected void sendSerialPort(byte[] output) {
		try {
			mOutputStream.write(output);
		} catch (Exception e) {
		}
	}
}
