package com.fys.handprint.Protocol;

public class Utils {


	//JAVA 数值-->字节  低位在前
	public static byte[] intToByteArray(int i) {   
		  byte[] result = new byte[4];   
		  result[3] = (byte)((i >> 24) & 0xFF);
		  result[2] = (byte)((i >> 16) & 0xFF);
		  result[1] = (byte)((i >> 8) & 0xFF); 
		  result[0] = (byte)(i & 0xFF);
		  return result;
		 }
	
	public static int bytesToInt(byte b1,byte b2,byte b3,byte b4)
	{
		return    b1 & 0xff|(b2 & 0xff) << 8 | (b3 & 0xff) << 16  | (b4 & 0xff) << 24;
	}
	public static short byteToShort(byte b1,byte b2)
	{
		return (short)( b1 & 0xff|(b2& 0xff) << 8 );
	}
	public static byte[] shortTobytes(short i)
	{
		byte[] rec=new byte[2];
		rec[0]=(byte) (i&0xff);
		rec[1]=(byte) ((i>>8)&0xff); 
		return rec;
	}
	public static short shortByteSum1(short i)
	{
		short rec= (short) (i&0xff + i>>8);
		return rec;
		
	}
	public static short intByteSum(int i)
	{
		short rec=0;
		rec+= (byte)(i & 0xFF)+(byte)((i >> 8) & 0xFF)+(byte)((i >> 16) & 0xFF)+(byte)((i >> 24) & 0xFF);
		return rec;
	}
	public static String byteToString(byte[] bytes) {
		String rec = "";
		for (int i = 0; i < bytes.length; i++) {
			rec += " " + String.format("%1$#2x", bytes[i]);
		}
		return rec;
	}
}
