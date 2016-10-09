package com.fys.password;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import com.fys.handprint.Protocol.CommandPack;
import com.fys.handprint.Protocol.DataPacket;
import com.fys.handprint.Protocol.Defined;
import com.fys.handprint.Protocol.ResponsePacket;
import com.fys.handprint.Protocol.Utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
/*
 * ?è≠?á¥USB??îÁ±µ???
 * ?Ñ§ÊΩ∞USBÁ±µÊ?ÖË¢®??
 * ÊΩ∞ËÑ§USB?â¢??òË?îÂ?ÉË??
 * mass storage Ê∫êÂ??
 */
public class UsbHelper {

	// USB?â¢???
	final int WaitTime = 1000; // ??âÂ?Â•?ÊΩ?
	private UsbManager usbManager;
	private UsbDevice usbDevice;
	boolean isFindDevice;
	final int Vid = 1241;	//?â¢??òID
	final int Pid = 32776;	//?á¶Ê®°ID
	UsbEndpoint inEndpoint;// ÈªçÊ?ÖÊ?ÇË™π?ê∏
	UsbEndpoint outEndpoint;// Ëø°Ê?ÖÊ?ÇË™π?ê∏
	UsbDeviceConnection connection;
	byte[] m_abyTransferBuf = new byte[512];
	String TAG = "UsbHelper";
	Context context;// Â•ªÁ?üÊ??
	Handler pHandler;// Á±µÁú≠
	int sendMax, recMax;
	public boolean Isconneced=false;

	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private PendingIntent pendingIntent;

	public UsbHelper(Context c, Handler h) {
		context = c;
		pHandler = h;	
		usbManager = (UsbManager) context
				.getSystemService(Context.USB_SERVICE);
		isFindDevice = false;
		deviceHandler.postDelayed(deviceRunnable, 1000);
		pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
	}
/*
 * ?Ñ§Ê¢ëÊâ¢???
 */
	void FindDevice() {
		try {
			HashMap<String, UsbDevice> map = usbManager.getDeviceList();
			isFindDevice=false;
			for (UsbDevice device : map.values()) {
				if (Vid == device.getVendorId()
						&& Pid == device.getProductId()) {
					usbDevice = device;
					isFindDevice=true;
					break;
				}
			}
		
			if(!isFindDevice) return;	//Áæ∂Ë?ÑÁ?åÈ?ÖUSB?â¢???

			if (usbManager.hasPermission(usbDevice)) {
				isFindDevice = true;
				Message msg = new Message();
				msg.what = Defined.USBPermission;
				pHandler.sendMessage(msg);
				INIConnection();

			} else {
				usbManager.requestPermission(usbDevice, pendingIntent);

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	Handler deviceHandler = new Handler();
	//?Ñ§Ê¢ëÁ?åÈ?ÖUSB,?ó©10??ÉËÑ§Ê¢ëÁè®Ê£í„?õÁè®?úª??ÑÊ?ëÂ??;
	Runnable deviceRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!isFindDevice) {
				FindDevice();
				deviceHandler.postDelayed(deviceRunnable, 10000);
			}
		}

	};
	
	void Close() {
		UsbInterface intf = usbDevice.getInterface(0);
		 connection.releaseInterface(intf);
		connection.close();
	}
	void INIConnection() {
		// ?â¢??òÁ?êÂ?îÊ?ëÈ?éÁè®Ë∑∫Ë?âË´≥
		UsbInterface intf = usbDevice.getInterface(0);
		connection = usbManager.openDevice(usbDevice);
		// connection.controlTransfer(requestType, request, value, index,
		// buffer, length, timeout)
		isFindDevice = connection.claimInterface(intf, true);
		inEndpoint = intf.getEndpoint(0);
		outEndpoint = intf.getEndpoint(1);
		sendMax = outEndpoint.getMaxPacketSize();
		recMax = inEndpoint.getMaxPacketSize();
		Isconneced=true;

	}
	// cmd?Ñ©??úÈ?îÈé¢ para Áµ±Ê??
	// ÊÆøÈ?ô„Ñ©ResponsePacket
	// ??úÊ??Ñ©1.1ÔπúSCSI Ê•∑Â?? 1.2ÔπúÈ?úÈ?îÊ•∑??? 1.3 ÈªçÔ†µSCSI Ë¢®Ê?? 2.1ÔπúSCSIË´âÂΩ∂ 2.2ÔπúË?âÂΩ∂ ACK 2.3ÈªçÔ†µSCSI
	// Ë¢®Ê??
	public ResponsePacket SendCmd(short cmd, int para) {

		if (connection == null)
			return null;
		if(bulkSend(cmd, para))
		{
			return bulkResponse();
		}
		return null;
	}
	
	ResponsePacket bulkResponse()
	{
		byte[] responseBytes = new byte[Defined.responsePackLen];
		byte[] scsiStateBytes = new byte[13];
		byte[] scsiBytes = getSCSI(true, Defined.responsePackLen);
		// 2.1ÔπúSCSIË´âÂΩ∂
		if (connection.bulkTransfer(outEndpoint, scsiBytes,
				scsiBytes.length, WaitTime) != scsiBytes.length)
			return null;
//		Log.e(TAG, "2.1ÔπúSCSIË´âÂΩ∂"+Utils.byteToString(scsiBytes));
		// 2.2ÔπúË?âÂΩ∂ ACK
		if (connection.bulkTransfer(inEndpoint, responseBytes,
				responseBytes.length, WaitTime) != responseBytes.length)
			return null;
//		Log.e(TAG, "2.2ÔπúË?âÂΩ∂ ACK"+Utils.byteToString(responseBytes));
		// 2.3 ÈªçÔ†µSCSI Ë¢®Ê??
		if (connection.bulkTransfer(inEndpoint, scsiStateBytes,
				scsiStateBytes.length, WaitTime) != scsiStateBytes.length)
			return null;
//		Log.e(TAG, "2.3 ÈªçÔ†µSCSI Ë¢®Ê??"+Utils.byteToString(scsiStateBytes));
		return new ResponsePacket(responseBytes);
	}
	public DataPacket bulkData(int len)
	{
		byte[] responseBytes = new byte[len+6];
		byte[] scsiStateBytes = new byte[13];
		byte[] scsiBytes = getSCSI(true, len+6);
		byte[] tempBytes=new byte[recMax];
		int k=0;
		// 3.1ÔπúSCSIË´âÂΩ∂
		int out= connection.bulkTransfer(outEndpoint, scsiBytes,
				scsiBytes.length, WaitTime) ;
		if (out!= scsiBytes.length)
		{
			Log.e(TAG, "3.1ÔπúSCSIË´âÂΩ∂"+Utils.byteToString(scsiBytes));
			return null;
		}
		else
		{
			Log.e(TAG, "3.1ÔπúSCSIË´âÂΩ∂"+Utils.byteToString(scsiBytes));
		}
		// 3.2ÔπúË?âÂΩ∂ ACK
		int out1=0;
		k=0;
		do
		{
		out =connection.bulkTransfer(inEndpoint, tempBytes,
				tempBytes.length, WaitTime);
		
		if(out>0)
		{
		 System.arraycopy(tempBytes,0,responseBytes,k,out);
		 k+=out;
		 out1+=out;
		}
		} while(k<responseBytes.length);
		
		if (out1 != responseBytes.length)
		{
//			Log.e(TAG, "3.2ÔπúË?âÂΩ∂ ACK"+Utils.byteToString(responseBytes));
			return null;
		}
		else
		{
			Log.e(TAG, "??ÖÊ?ÇÈ?óÂ?Ö„Ñ©"+ responseBytes.length);
		}
		

		// 3.3 ÈªçÔ†µSCSI Ë¢®Ê??
		if (connection.bulkTransfer(inEndpoint, scsiStateBytes,
				scsiStateBytes.length, WaitTime) != scsiStateBytes.length)
		{
			Log.e(TAG, "3.3 ÈªçÔ†µSCSI Ë¢®Ê??"+Utils.byteToString(scsiStateBytes));
			return null;
		}
		
		return new DataPacket(responseBytes);
	}
	int bulkDataOut(int len,byte[] data)
	{
		//byte[] responseBytes = new byte[len+6];
		byte[] scsiStateBytes = new byte[13];
		byte[] scsiBytes = getSCSI(false, len+6);
		int out,out1;
		//byte[] tempBytes=new byte[recMax];

		// 3.1ÔπúSCSIË´âÂΩ∂
		out=connection.bulkTransfer(outEndpoint, scsiBytes,
				scsiBytes.length, WaitTime) ;
		
		//do
		//{
		out1=connection.bulkTransfer(outEndpoint, data,
				(len+6), WaitTime);
		//}while(out1!=(len+6));
		
		connection.bulkTransfer(inEndpoint, scsiStateBytes,
						scsiStateBytes.length, WaitTime);

		return out1;

	}
	boolean bulkSend(short cmd, int para)
	{
		boolean rec=true;
		CommandPack cmdp = new CommandPack(cmd, para);
		byte[] sendByte = cmdp.cmdBytes;
		byte[] scsiBytes = getSCSI(false, 12);
	
		byte[] scsiStateBytes = new byte[13];
		// 1.1ÔπúSCSI Ê•∑Â??		
		if (connection.bulkTransfer(outEndpoint, scsiBytes,
				scsiBytes.length, WaitTime)!= scsiBytes.length)
			return false;
//		Log.e(TAG, "1ÔπúSCSI Ê•∑Â??"+","+Utils. byteToString(scsiBytes));
		// 1.2ÔπúÈ?úÈ?îÊ•∑???		
	if (connection.bulkTransfer(outEndpoint, sendByte, sendByte.length,
			WaitTime) != sendByte.length)
			return false;
//	Log.e(TAG, "1.2ÔπúÈ?úÈ?îÊ•∑???"+"--"+Utils.byteToString(sendByte));
		// 1.3 ÈªçÔ†µSCSI Ë¢®Ê??	
		if (connection.bulkTransfer(inEndpoint, scsiStateBytes,
				scsiStateBytes.length, WaitTime) != scsiStateBytes.length)
			return false;
//		Log.e(TAG, "1.3 ÈªçÔ†µSCSI Ë¢®Ê??"+Utils.byteToString(scsiStateBytes));
		return rec;
	}
	

	// Ë´∑Áß∂??úÈ?îÊè≠?á¥

	byte[] getSCSI(boolean recFlag, int len) {
		ByteBuffer scsiBuffer = ByteBuffer.allocate(31);
		scsiBuffer.order(ByteOrder.LITTLE_ENDIAN);
		scsiBuffer.putInt(0, 0x43425355);// 0x43425355 Ê¢ìÂ?éÂ?àCBW??úÈ?îËº∏
		scsiBuffer.putInt(4, 0x89182b28);// 0x89182b28 CBWÊ¢ìÔ??
		scsiBuffer.putInt(8, len);
		if (recFlag) // 1=data-in from the device to the host
		{
			scsiBuffer.put(12, (byte) 0x80);
			scsiBuffer.putShort(15, (short) 0xffef);
		} else // 0=data-out from host to the device
		{
			scsiBuffer.put(12, (byte) 0x0);
			scsiBuffer.putShort(15, (short) 0xfeef);
		}
		scsiBuffer.put(13, (byte) 0); // LUN
		scsiBuffer.put(14, (byte) 10);// CBWCB??îË?ÑËô¥Ë∂ºË™π??óÂ??
		return scsiBuffer.array();

	}
	
}
