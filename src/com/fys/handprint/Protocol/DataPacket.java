package com.fys.handprint.Protocol;
/*
 * Data Packet
 */
public class DataPacket {
	byte Code1=0x5A;
	byte Code2=(byte) 0xA5;
	short DeviceID=1;
	public byte[] Data;
	short Check_Sum;
	public DataPacket(byte[] bytes)
	{
		setBytes(bytes);
	}
	public void setBytes(byte[] bytes)
	{
		Data=new byte[bytes.length-6];
		for(int i=0;i<Data.length;i++)
		{
			Data[i]=bytes[i+4];
		}
		Check_Sum=Utils.byteToShort(bytes[bytes.length-1], bytes[bytes.length-2]);
	}
	public boolean getCheck()
	{
		if(Data==null) return false;
		short rec=0;
		rec+=Code1+Code2;
		for(int i=0;i<Data.length;i++)
		{
			rec+=Data[i];
		}
		return Check_Sum==rec;
	}
}
