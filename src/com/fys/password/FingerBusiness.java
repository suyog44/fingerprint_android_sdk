package com.fys.password;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.fys.handprint.Protocol.DataPacket;
import com.fys.handprint.Protocol.Defined;
import com.fys.handprint.Protocol.ResponsePacket;
import com.fys.handprint.Protocol.Utils;


public class FingerBusiness {

	final String TAG = "FingerBusiness";
	UsbHelper theUsb;
	Handler pHandler;
	public int idnumber;
	public int parameter;
	public int temp;
	public Uri fburi;
    char[] x = new char[498];
	String s="";

	public FingerBusiness(Context c, Handler h) {
		theUsb = new UsbHelper(c, usbHandler);
		pHandler = h;
	}
	public void Close() {
		theUsb.Close();
	}
	public void VerifyIdentify() {
		Verify verRunnable = new Verify();
		new Thread(verRunnable).start();
	}
	public void Login() {
		Enroll enrollRunnable = new Enroll();
		new Thread(enrollRunnable).start();
	}
	public void FBGetEnrollCount() {
		GetCount GetRunnable = new GetCount();
		new Thread(GetRunnable).start();
	}
	public void FBDeletAll() {
		DeletIDAll DeletRunnable = new DeletIDAll();
		new Thread(DeletRunnable).start();
	}
	public void FBDeletID() {
		DeletID DeletIDRunnable = new DeletID();
		new Thread(DeletIDRunnable).start();
	}
	public void FBCheckEnrolled() {
		CheckID CheckIDRunnable = new CheckID();
		new Thread(CheckIDRunnable).start();
	}
	public void FBGetImage() {
		GetImage GetImageRunnable = new GetImage();
		new Thread(GetImageRunnable).start();
	}
	public void FBGetRaw() {
		GetRaw GetRawRunnable = new GetRaw();
		new Thread(GetRawRunnable).start();
	}
	public void FBSetTemplate() {
		SetTemp SetTempRunnable = new SetTemp();
		new Thread(SetTempRunnable).start();
	}
	public void FBGetTemplate() {
		GetTemp GetTempRunnable = new GetTemp();
		new Thread(GetTempRunnable).start();
	}
	public void FBVerifyTemplate() {
		VerifyTemp VerifyTempRunnable = new VerifyTemp();
		new Thread(VerifyTempRunnable).start();
	}
	public void FBIdentifyTemplate() {
		IdentifyTemp IdentifyTempRunnable = new IdentifyTemp();
		new Thread(IdentifyTempRunnable).start();
	}
	public void FBDeviceInfo() {
		Devinfo DevinfoRunnable = new Devinfo();
		new Thread(DevinfoRunnable).start();
	}
	public void FBSetBaudrate() {
		SetBaudrate BaudrateRunnable = new SetBaudrate();
		new Thread(BaudrateRunnable).start();
	}
	boolean EnrollStart(int id) {
		ResponsePacket Response = theUsb.SendCmd(Defined.CMD_ENROLL_START,
				id);
		return (Response.Response ==  Defined.ACK_OK);
	}
	ResponsePacket Enroll(short cmd,int para)
	{
		if(CaptureFinger())
		{
			return theUsb.SendCmd(cmd,para);
		}
		else 
		{
			return null;
		}
	}
	int CheckEnrolledNotUsed(ResponsePacket Response) {
		for (int i = 1; i < 2000; i++) {
			ResponsePacket rs  = theUsb.SendCmd(
					Defined.CMD_CHECK_ENROLLED, i);
			
			Response.Response=rs.Response;
			Response.Parameter=rs.Parameter;
			if (Response.Response ==Defined.NACK_INFO && Response.Parameter== Defined.NACK_IS_NOT_USED ) {
				Log.e(TAG, "ID id not used"+i);
				return i;
			}
			
		}
		return -1;
	}
	
	int CheckEnrollCount() {
	
		ResponsePacket rs  = theUsb.SendCmd(
				Defined.CMD_ENROLL_COUNT, 0);
		
		//Response.Response=rs.Response;
		//Response.Parameter=rs.Parameter;
		if (rs.Response ==Defined.ACK_OK) 
		{
			return rs.Parameter;
		}
			
		
		return -1;
	}

	boolean DeletID_All() {

		ResponsePacket rs;
		//if(ID==0xFE)//delet all id
			{
			rs  = theUsb.SendCmd(Defined.CMD_DELETE_ALL, 0);
			}
		//else	//delet id
			//{
			//rs  = theUsb.SendCmd(Defined.CMD_DELETE, ID);
			//}
		if (rs.Response ==Defined.ACK_OK) 
		{
			return true;
		}
			
		return false;
	}

	boolean DeletID() {

		ResponsePacket rs;
		rs  = theUsb.SendCmd(Defined.CMD_DELETE, idnumber);
		if (rs.Response ==Defined.ACK_OK) 
		{
			return true;
		}	
		return false;
	}	

	boolean CheckID() {

		ResponsePacket rs;
		rs  = theUsb.SendCmd(Defined.CMD_CHECK_ENROLLED, idnumber);
		if (rs.Response ==Defined.ACK_OK) 
		{
			return true;
		}	
		return false;
	}
	void LED_ONOFF(int para) {

		theUsb.SendCmd(Defined.CMD_CMOS_LED, para);
	}
	
	void IsPressFinger()
	{
		short ispressed=1;
		
		//BackMessage("Please take off finger");
		//theUsb.SendCmd(Defined.CMD_CMOS_LED, 1);//led turn on
		do
		{
			try {
				Thread.sleep(250); // 延时
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ResponsePacket IsPressFingerResponse = theUsb.SendCmd(
					Defined.CMD_IS_PRESS_FINGER, 0);

			if (IsPressFingerResponse.Response ==  Defined.ACK_OK
					&& IsPressFingerResponse.Parameter != 0) 
			{

				ispressed = 0;
				BackMessage("Input finger");
				//theUsb.SendCmd(Defined.CMD_CMOS_LED, 0);//led turn off
				//break;	
			}
				
		}while(ispressed==1);	
	}
	
	boolean CaptureFinger() {
		boolean rec = false;
		boolean waitPressFinger = true;
		long waitTime = 10000;
		long StartTime;
		long endTime;
		StartTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();

		// 1---- 打开灯
		//ResponsePacket ledResponse = theUsb
				//.SendCmd(Defined.CMD_CMOS_LED, 1);
		//if (ledResponse == null)
			//return false; // 打开灯有问题

		do {
			try {
				Thread.sleep(250); // 延时
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			endTime = System.currentTimeMillis();
			// 判断超时
			if ((endTime - StartTime) > waitTime) {
				waitPressFinger = false;
				break;
			}
			//ResponsePacket IsPressFingerResponse = theUsb.SendCmd(
					//Defined.CMD_IS_PRESS_FINGER, 0);

			//if (IsPressFingerResponse == null)
				//continue;
			//if (IsPressFingerResponse.Response ==  Defined.ACK_OK
					//&& IsPressFingerResponse.Parameter == 0) {
				//break;
			//}
			ResponsePacket CaptureFingerResponse = theUsb.SendCmd(
					Defined.CMD_CAPTURE, 1);
			//BackMessage("Response="+CaptureFingerResponse.Response);
			if (CaptureFingerResponse == null)
			{
				continue;
			}
			if (CaptureFingerResponse.Response == Defined.ACK_OK) 
			{
				rec = true;
				break;
			}
		} while (waitPressFinger);
		Log.e(TAG, "waitPressFinger:"+waitPressFinger);
		//if (waitPressFinger) {
			// 2---- 获得指纹
			//ResponsePacket CaptureFingerResponse = theUsb.SendCmd(
					//Defined.CMD_CAPTURE, 1);
			//if (CaptureFingerResponse.Response == Defined.ACK_OK) {
				//rec = true;
			//}
		
		// 3 ---关灯
		//theUsb.SendCmd(Defined.CMD_CMOS_LED, 0);
	//}
		//theUsb.SendCmd(Defined.CMD_CMOS_LED, 0);
		return rec;
	}
	
	class Enroll implements Runnable {

		@Override
		public void run() {

			/*
			 * 6.3 Enrollment An enrollment flowchart is as below. 
			 * 0. CheckEnrolled get ID 
			 * 1. EnrollStart with a (not used) ID 
			 * 2. CaptureFinger 
			 * 3. Enroll1 
			 * 4. Wait to take off the finger  using IsPressFinger 
			 * 5. CaptureFinger 
			 * 6. Enroll2 
			 * 7. Wait to  take off the finger using IsPressFinger 
			 * 8. CaptureFinger 
			 * 9.  Enroll3
			 */

			// TODO Auto-generated method stub

			int ID = -1;
			// 0---CheckEnrolled
			ResponsePacket temp_response = new ResponsePacket(null);
			
			ID = CheckEnrolledNotUsed(temp_response);
			
			if(temp_response.Parameter==Defined.NACK_DB_IS_FULL)
			{
				BackMessage("Data base is full.");
				return;
			}
			LED_ONOFF(1);
			
			if(EnrollStart(ID))
			{
				ResponsePacket response;
				 response=Enroll(Defined.CMD_ENROLL1,0);
				if(response==null)
				{
					BackMessage("No response in enroll 1");
					LED_ONOFF(0);
					return;
				}
				
				if(response.Response==Defined.ACK_OK)
				{
					BackMessage("Enroll 1/3 time is ok!\nPlease take off finger.");
				}
				else
				{
					if(response.Parameter<4096)
						{
						BackMessage("ID="+response.Parameter+" is duplicated");
						}
					else
						{
						BackMessage(ID+" Enroll fail");
						}
					LED_ONOFF(0);
					return;	
				}
				IsPressFinger();
				response=Enroll(Defined.CMD_ENROLL2,0);
				if(response==null)
				{
					BackMessage("No response in enroll 2");
					LED_ONOFF(0);
					return;
				}
				if(response.Response==Defined.ACK_OK)
				{
					BackMessage("Enroll 2/3 time is ok!\nPlease take off finger.");
				}
				else
				{
					if(response.Parameter<4096)
						{
						BackMessage("ID="+response.Parameter+" is duplicated");
						}
					else
						{
						BackMessage(ID+" Enroll fail");
						}
					LED_ONOFF(0);
					return;	
				}
				IsPressFinger();
				response=Enroll(Defined.CMD_ENROLL3,0);
				if(response==null)
				{
					BackMessage("No response in enroll 3");
					LED_ONOFF(0);
					return;
				}
				if(response.Response==Defined.ACK_OK)
				{
					BackMessage("ID="+ID+" Enroll 3/3 time is ok!");
				}
				else
				{
					if(response.Parameter<4096)
					{
						BackMessage("ID="+response.Parameter+" is duplicated");
					}
					else
					{
						BackMessage(ID+" Enroll fail");
					}
					LED_ONOFF(0);
					return;	
				}
				
			}
			LED_ONOFF(0);
			
			/*
			if(EnrollStart(ID))
			{
				ResponsePacket response;
				for(int i=1;i<=10;i++)
				{
					response=Enroll(Defined.CMD_ENROLL1,(i-1));
					if(response==null)
					{
						BackMessage("No response in enroll="+i);
						LED_ONOFF(0);
						return;
					}
					
					if(response.Response==Defined.ACK_OK)
					{
						
						if(i==10)
						{
							BackMessage("ID="+ID+" enroll succeed");
						}
						else
						{
							BackMessage("Enroll="+i+"/10 time is ok!\nPlease take off finger.");	
						}
					}
					else
					{
						if(response.Parameter<4096)
						{
							BackMessage("ID="+response.Parameter+" is duplicated");
						}
						else
						{
							BackMessage("ID="+ID+" enroll fail");
						}
						LED_ONOFF(0);
						return;	
					}
					if(i<=9)
					{
						IsPressFinger();
					}
				}
				LED_ONOFF(0);	
			}
			*/
		}
	}
	
	void SaveTemplate(int id)
	{
		ResponsePacket GetTemplateResponse = theUsb.SendCmd(
				Defined.CMD_MAKE_TEMPLATE, id);
		if(GetTemplateResponse.Response==Defined.ACK_OK)
		{
			DataPacket data=theUsb.bulkData(498);
			Message msg = new Message();
			msg.what = Defined.GETTemplate;
			msg.arg1=id;
			Bundle bundle = new Bundle();
			bundle.putByteArray(Defined.BundlerDATA, data.Data);
			msg.setData(bundle);
			pHandler.sendMessage(msg);
		}
		else
		{
			BackMessage("Save template fail.");
		}
	}
	void GetTemplate(int id)
	{
		ResponsePacket GetTemplateResponse = theUsb.SendCmd(
				Defined.CMD_GET_TEMPLATE, id);
		if(GetTemplateResponse.Response==Defined.ACK_OK)
		{
			BackMessage("ID="+id+"GetTemplate is ok!!");
			DataPacket data=theUsb.bulkData(498);
			Message msg = new Message();
			msg.what = Defined.GETTemplate;
			msg.arg1=id;
			Bundle bundle = new Bundle();
			bundle.putByteArray(Defined.BundlerDATA, data.Data);
			msg.setData(bundle);
			pHandler.sendMessage(msg);
		}
		else
		{
			BackMessage("ID="+id+"is not used");
			Message msg1 = new Message();
			msg1.arg1=id;
			pHandler.sendMessage(msg1);	
		}
	}
	void VerifyTemplate(int id)
	{
		
		byte[] settemplateBytes = new byte[504];

		settemplateBytes = GetReadFile();
	
		
		ResponsePacket Response = theUsb.SendCmd(Defined.CMD_VERIFY_TEMPLATE, idnumber);
		if(Response.Response==Defined.ACK_OK)
		{
			//BackMessage("Verify template cmd ok");	
		}
		else if (Response.Response ==Defined.NACK_INFO && Response.Parameter== Defined.NACK_IS_NOT_USED )  
		{
			BackMessage("ID="+idnumber+" is not used.");	
			return;
		}
		

		theUsb.bulkDataOut(498,settemplateBytes);
		ResponsePacket bulkDataOutresponse = theUsb.bulkResponse();
		if(bulkDataOutresponse.Response == Defined.ACK_OK)
		{
			BackMessage("Verify is correct.");	
		}
		else
		{
			BackMessage("Verify is fail.");
		}
	}
	void IdentifyTemplate()
	{
		
		byte[] settemplateBytes = new byte[504];

		settemplateBytes = GetReadFile();
	
		
		ResponsePacket Response = theUsb.SendCmd(Defined.CMD_IDENTIFY_TEMPLATE, 0);
		if(Response.Response==Defined.ACK_OK)
		{
			//BackMessage("Identify template cmd ok");	
		}
		else if (Response.Response ==Defined.NACK_INFO && Response.Parameter== Defined.NACK_DB_IS_EMPTY )  
		{
			BackMessage("Data base is empty.");	
			return;
		}

		theUsb.bulkDataOut(498,settemplateBytes);
		ResponsePacket bulkDataOutresponse = theUsb.bulkResponse();

		if(bulkDataOutresponse.Response == Defined.ACK_OK)
		{
			BackMessage("ID="+bulkDataOutresponse.Parameter+" is correct.");	
		}
		else
		{
			BackMessage("Identify is fail.");
		}
	}
	void info()
	{
		byte[] rec = null;
		byte[] temp1 = new byte[4];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[16];
		StringBuffer FirmwareVersion = new StringBuffer();
		StringBuffer IsoAreaMaxSize = new StringBuffer();
		StringBuffer DeviceSN = new StringBuffer();
		ResponsePacket InfoResponse = theUsb.SendCmd(
				Defined.CMD_OPEN, 1);
		if (InfoResponse.Response == Defined.ACK_OK) {
			DataPacket InfoData = theUsb.bulkData(24); 
			rec = InfoData.Data;
			for(int i=0;i<4;i++)
			{
				temp1[3-i] = rec[i];
			}
		    for (byte b:temp1)
		    {
		    	FirmwareVersion.append(String.format("%02x", b));
		    }
		    
			for(int i=4;i<8;i++)
			{
				temp2[7-i] = rec[i];
			}
		    for (byte b:temp2)
		    {
		    	IsoAreaMaxSize.append(String.format("%02x", b));
		    }

			for(int i=0;i<16;i++)
			{
				temp3[i] = rec[i+8];
			}
		    for (byte b:temp3)
		    {
		    	DeviceSN.append(String.format("%02X", b));
		    }
		   
		    BackMessage("FirmwareVersion:"+FirmwareVersion
		    		+"\nIsoAreaMaxSize:"+IsoAreaMaxSize
		    		+"\nDeviceSN:"+DeviceSN
		    		+"\nAppSN:14.0625.03.01");
		    //BackMessage("IsoAreaMaxSize:"+IsoAreaMaxSize);
		    //BackMessage("DeviceSN:"+DeviceSN);
		    //BackMessage("AppSN:14.0604.02.01");//20140604//01version//01cut
		}
		
	}
	void SetBaudrate()
	{
		ResponsePacket SetBaudrateResponse = theUsb.SendCmd(
				Defined.CMD_CHANGE_BAUDRATE, parameter);
		if(SetBaudrateResponse.Response==Defined.ACK_OK)
		{
			BackMessage("Set Baudrate="+parameter+" is ok!!");
		}
		else
		{
			BackMessage("Set Baudrate="+parameter+" is fail!!");
		}
	}
	void BackMessage(String strMessage)
	{
		Message msg=new Message();
		msg.what=Defined.ShowMessage;
		Bundle bundle=new Bundle();
		bundle.putString("info", strMessage);
		msg.setData(bundle);
		pHandler.sendMessage(msg);
	}
	byte[] GetImage() {
		byte[] rec = null;
		ResponsePacket GetImageResponse = theUsb.SendCmd(
				Defined.CMD_GET_IMAGE, 0);
		if (GetImageResponse.Response == Defined.ACK_OK) {
			// Data  258x202  image  (52116  bytes)
			DataPacket GetImageData = theUsb.bulkData(52116); 
			rec = GetImageData.Data;
		}
		return rec;
	}
	byte[] GetRawImage() {
		byte[] rec = null;

		ResponsePacket GetImageResponse = theUsb.SendCmd(
				Defined.CMD_GET_RAWIMAGE, 0);
		if (GetImageResponse.Response == Defined.ACK_OK) {
			// Data  160x120  image  (19200  bytes)
			DataPacket GetImageData = theUsb.bulkData(19200); 
			rec = GetImageData.Data;
		}
		return rec;
	}
	byte[] GetReadFile() {
		byte[] rec = null;
		byte[] readbyte = new byte[498];
		byte[] settemplateBytes = new byte[504];
		short checksum=0;
		
        try {
        File stockInputFile = new File("/mnt/sdcard/finger/0001.dat");
        FileInputStream fis = new FileInputStream(stockInputFile);
        int count;
        int k=0;
        while ((count = fis.read()) != -1) {
        	readbyte[k] = (byte)count;
        	k++;
        }
        fis.close();
	
       } 
        catch (FileNotFoundException e) {
        	BackMessage("file is not found!");
     } catch (IOException e) {
            System.err.println("FileStreamsReadnWrite: " + e);
            BackMessage("file is not found XXXXDDDDD!");
     }
		for(int i=0;i<498;i++)
		{
			settemplateBytes[i+4] = readbyte[i];
		}
		
		settemplateBytes[0] = (byte)0x5A;
		settemplateBytes[1] = (byte)0xA5;
		settemplateBytes[2] = (byte)0x01;
		settemplateBytes[3] = (byte)0x00;
		//caculate checksum
		for(int i=0;i<504;i++)
		{
			checksum += (short)(settemplateBytes[i]&0xFF);
		}
		settemplateBytes[502] = (byte)(checksum&0xFF);
		settemplateBytes[503] = (byte)((checksum>>8)&0xFF);        
		return settemplateBytes;
	}
	int VerifyCheck() {
		int rec = -1;
		ResponsePacket VerifyReaponse = theUsb.SendCmd(
				Defined.CMD_IDENTIFY, 0);
		if (VerifyReaponse.Response ==  Defined.ACK_OK) {
			rec = VerifyReaponse.Parameter;
		}
		return rec;
	}
	class Verify implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int TimeCount =10;
			long StartTime;
			long endTime;
			LED_ONOFF(1);
			if (CaptureFinger()) {
				
				Message msg = new Message();
				//msg.what = Defined.GETIMAGE;
				Bundle bundle = new Bundle();
				bundle.putByteArray(Defined.BundlerDATA, GetImage());
				msg.setData(bundle);
				pHandler.sendMessage(msg);
				StartTime = System.currentTimeMillis();
				int id= VerifyCheck();
				if(id<0)
				{
					//BackMessage("There is no data base!");
					endTime = System.currentTimeMillis();
					Message msg2 = new Message();
					msg2.what = Defined.VERIFYERROR;
					msg2.arg1=(int) (endTime-StartTime);
					pHandler.sendMessage(msg2);
				}
				else
				{
					endTime = System.currentTimeMillis();
					//BackMessage("ID="+id+" identify succeed!");
					Message msg1 = new Message();
					msg1.what = Defined.VERIFYCORRECT;
					msg1.arg1=(int) (endTime-StartTime);
					msg1.arg2 = id;
					pHandler.sendMessage(msg1);
				}
				
			}
			else
				{
					BackMessage("Time out="+TimeCount+"second");
					Message msg1 = new Message();
					//msg1.what = Defined.VERIFY;
					msg1.arg1=TimeCount;
					pHandler.sendMessage(msg1);
				}
			LED_ONOFF(0);
		}

	}

	class GetCount implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
		int EnrollCount = -1;
		// 0---CheckEnrolled
		EnrollCount=CheckEnrollCount();
		
		BackMessage("Enroll Count="+EnrollCount);
		Message msg1 = new Message();
		msg1.arg1=EnrollCount;
		pHandler.sendMessage(msg1);
		}

	}

	class DeletIDAll implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
		if(DeletID_All())
			{
				BackMessage("Delet All ID is Sucessful");
			}
		else
			{
				BackMessage("Delet All ID is Fail");

			}
			Message msg1 = new Message();
			pHandler.sendMessage(msg1);	
		}

	}
	class DeletID implements Runnable {

		@Override
		public void run() {
		BackMessage("Delet ID="+idnumber);
		Message msg1 = new Message();
		pHandler.sendMessage(msg1);	
		if(DeletID())
			{
				BackMessage("Delet ID="+idnumber+" is Sucessful");
			}
		else
			{
				BackMessage("Delet ID="+idnumber+" is Fail");

			}
			// TODO Auto-generated method stub
			Message msg2 = new Message();
			msg1.arg1=idnumber;
			pHandler.sendMessage(msg2);	
		}
	}
	class CheckID implements Runnable {

		@Override
		public void run() {
		BackMessage("Check ID="+idnumber);
		Message msg1 = new Message();
		pHandler.sendMessage(msg1);	
		if(CheckID())
			{
				BackMessage("ID="+idnumber+" is be used");
			}
		else
			{
				BackMessage("ID="+idnumber+" is not be used");

			}
			// TODO Auto-generated method stub
			Message msg2 = new Message();
			msg1.arg1=idnumber;
			pHandler.sendMessage(msg2);	
		}
	}	
	class GetImage implements Runnable {

		@Override
		public void run() {
		int TimeCount=10;
		LED_ONOFF(1);
			if (CaptureFinger()) 
			{
				Message msg = new Message();
				msg.what = Defined.GETIMAGE;
				Bundle bundle = new Bundle();
				bundle.putByteArray(Defined.BundlerDATA, GetImage());
				msg.setData(bundle);
				pHandler.sendMessage(msg);		
			}
			else
			{
				BackMessage("Time out="+TimeCount+"second");
				Message msg1 = new Message();
				//msg1.what = Defined.VERIFY;
				msg1.arg1=TimeCount;
				pHandler.sendMessage(msg1);		
			}
		LED_ONOFF(0);
		}
	}
	class GetRaw implements Runnable {

		@Override
		public void run() {
			//ResponsePacket ledResponse = theUsb
					//.SendCmd(Defined.CMD_CMOS_LED, 1);
			LED_ONOFF(1);
			//if (ledResponse == null)
				//return; 

			Message msg = new Message();
			msg.what = Defined.GETIMAGERaw;
			Bundle bundle = new Bundle();
			bundle.putByteArray(Defined.BundlerDATA, GetRawImage());
			msg.setData(bundle);
			pHandler.sendMessage(msg);	
			LED_ONOFF(0);
			//theUsb.SendCmd(Defined.CMD_CMOS_LED, 0);

		}
	}
	class SetTemp implements Runnable {

		@Override
		public void run() {
			
			byte[] settemplateBytes = new byte[504];

			settemplateBytes = GetReadFile();
			
			ResponsePacket Response = theUsb.SendCmd(Defined.CMD_ADD_TEMPLATE, idnumber);
			if(Response.Response==Defined.ACK_OK)
			{
				//BackMessage("Set template cmd ok");	
			}
			else
			{
				//BackMessage("Set template cmd fail");	
				return;
			}
			
			int len=theUsb.bulkDataOut(498,settemplateBytes);
			ResponsePacket bulkDataOutresponse = theUsb.bulkResponse();
			if(bulkDataOutresponse.Response == Defined.ACK_OK)
			{
				BackMessage("Set template is correct.");	
			}
			else
			{
				BackMessage("Set template is fail.");
			}
			
		}
	}
	class GetTemp implements Runnable {

		@Override
		public void run() {
			    
		GetTemplate(idnumber);	
		}
	}
	class VerifyTemp implements Runnable {

		@Override
		public void run() {
			    
		VerifyTemplate(idnumber);	
		}
	}
	class IdentifyTemp implements Runnable {

		@Override
		public void run() {
			    
		IdentifyTemplate();	
		}
	}
	class Devinfo implements Runnable {

		@Override
		public void run() {
			    
		info();	
		}
	}
	class SetBaudrate implements Runnable {

		@Override
		public void run() {
			    
			SetBaudrate();	
		}
	}
	class OPEN implements Runnable {
		short cmd = Defined.CMD_OPEN;
		int para = 1;
		
		byte[] temp = null;
		byte[] temp1 = new byte[4];
		byte[] temp2 = new byte[4];
		byte[] temp3 = new byte[16];
		StringBuffer FirmwareVersion = new StringBuffer();
		StringBuffer IsoAreaMaxSize = new StringBuffer();
		StringBuffer DeviceSN = new StringBuffer();

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ResponsePacket rec = theUsb.SendCmd(cmd, para);
			//DataPacket data = theUsb.bulkData(24);
			DataPacket InfoData = theUsb.bulkData(24); 
			
			temp = InfoData.Data;
			for(int i=0;i<4;i++)
			{
				temp1[3-i] = temp[i];
			}
		    for (byte b:temp1)
		    {
		    	FirmwareVersion.append(String.format("%02x", b));
		    }
		    
			for(int i=4;i<8;i++)
			{
				temp2[7-i] = temp[i];
			}
		    for (byte b:temp2)
		    {
		    	IsoAreaMaxSize.append(String.format("%02x", b));
		    }

			for(int i=0;i<16;i++)
			{
				temp3[i] = temp[i+8];
			}
		    for (byte b:temp3)
		    {
		    	DeviceSN.append(String.format("%02X", b));
		    }
		   
		    BackMessage("FirmwareVersion:"+FirmwareVersion
		    		+"\nIsoAreaMaxSize:"+IsoAreaMaxSize
		    		+"\nDeviceSN:"+DeviceSN
		    		+"\nAppSN:14.0731.03.03");
			
			Log.e(TAG, "OPEN USB" + Utils.byteToString(InfoData.Data));
			if (rec == null)
				return;
			if (rec.Response == 0x30) {
				Message msg = new Message();
				msg.what = Defined.USBINI;
				pHandler.sendMessage(msg);
			}
			
		}

	}
	Handler usbHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e(TAG, "USB handler start" + msg.what);
			switch (msg.what) {
				case Defined.USBPermission :
					OPEN openDevice = new OPEN();
					new Thread(openDevice).start();
					break;
				case Defined.USBINI :
					pHandler.sendMessage(msg);
					break;
				default :
					Log.e(TAG, "Default event happen");
					break;
			}
		}
	};
}
