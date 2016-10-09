package com.fys.password;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner; 
import android.widget.TextView;
import android.widget.Toast;

import com.example.fpapi.Lib.JavaFP;
import com.fys.handprint.Protocol.Defined;
import com.fys.password.R;

import java.io.File;


public class TestMain extends Activity {

	String TAG = "TestMain"; 
	FingerBusiness fingerBusiness; 
	
	private TextView password;
	private Button one, two, three, four, five, six, seven, eight, nine;
	private Button btok, back;
	private Button fingerinput;
	private Button Enroll,DeleteAll;
	
	ImageView Imageshow;
	UsbHelper theUsb;
	Context context;	
	fileHelp fileSave;	//文件读写单元
	JavaFP fpAPI;
	String myJpgPath;
	CharSequence message,result;
	short errorcount=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_main);
		fingerBusiness = new FingerBusiness(this, pHandler);
        password = (TextView) findViewById(R.id.password);
        //result = (TextView) findViewById(R.id.textview_result);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        eight = (Button) findViewById(R.id.eight);
        nine = (Button) findViewById(R.id.nine);
        
        btok = (Button) findViewById(R.id.btok);
        back = (Button) findViewById(R.id.back);
		
        fingerinput = (Button) findViewById(R.id.fingerinput);
        Enroll = (Button) findViewById(R.id.enroll);
        DeleteAll = (Button) findViewById(R.id.delete);
        
        one.setOnClickListener(mylistener);
        two.setOnClickListener(mylistener);
        three.setOnClickListener(mylistener);
        four.setOnClickListener(mylistener);
        five.setOnClickListener(mylistener);
        six.setOnClickListener(mylistener);
        seven.setOnClickListener(mylistener);
        eight.setOnClickListener(mylistener);
        nine.setOnClickListener(mylistener);
        
        btok.setOnClickListener(mylistener);
        back.setOnClickListener(mylistener);
        
        fingerinput.setOnClickListener(myfingerlistener);
        Enroll.setOnClickListener(myfingerlistener);
        DeleteAll.setOnClickListener(myfingerlistener);

        //Imageshow = (ImageView) findViewById(R.id.image);
		setEnable(false);
		setPassWordEnable(false);
		context = this;
		fileSave=new fileHelp(context);
		fpAPI=new JavaFP();
		Log.e(TAG, " fpAPI.Start："+ fpAPI.Start(100, 100));
		
	}
	void setEnable(boolean b) {
		fingerinput.setEnabled(b);
		Enroll.setEnabled(b);
		DeleteAll.setEnabled(b);

	}
	void setPassWordEnable(boolean b) {
		one.setEnabled(b);
		two.setEnabled(b);
		three.setEnabled(b);
		four.setEnabled(b);
		five.setEnabled(b);
		six.setEnabled(b);
		seven.setEnabled(b);
		eight.setEnabled(b);
		nine.setEnabled(b);
		btok.setEnabled(b);
		back.setEnabled(b);
	}		
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		//fingerBusiness.Close();
		super.onStop();
	}
	 @Override
	 protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
		fingerBusiness = new FingerBusiness(this, pHandler);
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.test_main, menu);
		return true;
	}
/*
 * 处理事件
 * 主要是处理USB通后，处理业务单元事件
 */
	Handler pHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				
				case Defined.USBINI : //USB
					setEnable(true);
					break;
				case Defined.GETIMAGE : //
					byte[] picByte = msg.getData().getByteArray(
							Defined.BundlerDATA);
					Imageshow.setImageBitmap(getFromByte(picByte)); //界面显示
					break;
				case Defined.GETIMAGERaw : //
					byte[] picByteRaw = msg.getData().getByteArray(
							Defined.BundlerDATA);
					Imageshow.setImageBitmap(getFromByteRaw(picByteRaw)); //界面显示
					break;
				case Defined.GETTemplate :	//
					byte[] templateBytes = msg.getData().getByteArray(
							Defined.BundlerDATA);
					int templateId=msg.arg1;
					DecimalFormat df = new DecimalFormat("0000");
					fileSave.WriteFile(df.format(templateId)+".dat", templateBytes);
					break;
				case Defined.SETTemplate :	//
					byte[] settemplateBytes = msg.getData().getByteArray(
							fingerBusiness.s);

					int templateId1=9;
					DecimalFormat df1 = new DecimalFormat("0000");
					fileSave.WriteFile(df1.format(templateId1)+".dat", settemplateBytes);
					break;
				case Defined.ShowMessage:	//
					//Toast toast;
					Bundle bundle=msg.getData();
					String info=bundle.getString("info","default");
					message = info;
					result = "";
					Result();
					//toast = Toast.makeText(context,info,Toast.LENGTH_SHORT);
					//toast.show();
					break;
				case Defined.VERIFYCORRECT:	//
		    		//myJpgPath = "/mnt/sdcard/Pictures/correct1.jpg";
		    		message = "タ絋 ID="+msg.arg2+",time="+msg.arg1+"ms";
		    		result = "Θ\nSuccess";
		    		Result();
		    		errorcount = 0;
					break;
				case Defined.VERIFYERROR:	//
		    		//myJpgPath = "/mnt/sdcard/Pictures/001.jpg";
		    		errorcount++;
		    		message = "岿粇"+errorcount+"Ω,time="+msg.arg1+"ms";
		    		result = "ア毖\nFail";
		    		Result();
		    		if(errorcount == 3)
		    		{
		    			setPassWordEnable(true);
		    			setEnable(false);
		    			errorcount = 0;
		    		}
					break;
				default :
					Log.e(TAG, "default");
					break;

			}
		}

	};
	// buffer: 258*202
	Bitmap getFromByte(byte[] buffer)
	{
		Bitmap pic = Bitmap.createBitmap(320, 240,
				Bitmap.Config.ARGB_8888);
		for (int i = 0; i < 258; i++) {
			for (int j = 0; j < 202; j++) {
				byte b = (byte) (buffer[(i * 202 + j)]);
//				int c1 = 0x0032A050; //高字节为亮度
				int c1=0xff000000;
				c1 = c1 ^ b << 24;
				pic.setPixel(i+31, j+19, c1);
			}
		}
		for(int i=0;i<258;i++)
		{
			for(int j=0;j<19;j++)
			{
				pic.setPixel(i+31, j, 0x7f000000);
				pic.setPixel(i+31, j+19+202, 0x7f000000);
			}
		}
		return pic;
	}
	
	Bitmap getFromByteRaw(byte[] buffer)
	{
		
		Bitmap pic = Bitmap.createBitmap(320, 240,
				Bitmap.Config.ARGB_8888);
		for (int i = 0; i < 160; i++) {
			for (int j = 0; j < 120; j++) {
				byte b = (byte) (buffer[(j * 160 + i)]);
//				int c1 = 0x0032A050; //高字节为亮度
				int c1=0xff000000;
				c1 = c1 ^ b << 24;
				pic.setPixel((i*2),(j*2), c1);
				pic.setPixel(((i*2)+1),(j*2), c1);
				pic.setPixel((i*2),((j*2)+1), c1);
				pic.setPixel(((i*2)+1),((j*2)+1), c1);
			}
		}

		return pic;
	}
	//关闭此Activity
	void Close()	
	{
		fingerBusiness.Close();
		finish();
	}

	void Result()	
	{
		TextView View1,View2;
		View1 = (TextView) findViewById(R.id.textview_message);
		View1.setText(message);	
		
		View2 = (TextView) findViewById(R.id.textview_result);
		View2.setText(result);
	}
	Handler uriOpenHandler=new Handler();
	Runnable uriOpenRunnable= new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 Uri uri = Uri.parse("http://www.baidu.com");  
				Intent  intent = new Intent(Intent.ACTION_VIEW, uri); 
				startActivity(intent);  //打开浏览器显示百度页面
				Close();
		}
	};
	{}
	class login_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.Login();	//调用登录业务
		}
	}
	class verify_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.VerifyIdentify();//调用验证业务
		}
	}

	class GetEnrollCount_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.FBGetEnrollCount();//调用验证业务
		}
	}

	class DeletAll_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.FBDeletAll();//调用验证业务
		}
	}
	class DeletID_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBDeletID();//调用验证业务
		}
	}
	class CheckEnrolled_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBCheckEnrolled();//调用验证业务
		}
	}	
	class GetImage_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.FBGetImage();//调用验证业务
		}
	}
	class GetRaw_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.FBGetRaw();//调用验证业务
		}
	}
	class SetTemplate_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBSetTemplate();//调用验证业务    
		}
	}

    class SpinnerSelectedListener implements OnItemSelectedListener{  
    	  
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,  
                long arg3) { 
    		switch(arg2)
    		{
    		case 0:
    			fingerBusiness.parameter = 9600;
    		break;
    		case 1:
    			fingerBusiness.parameter = 19200;
    		break;
    		case 2:
    			fingerBusiness.parameter = 38400;
    		break;
    		case 3:
    			fingerBusiness.parameter = 57600;
    		break;
    		case 4:
    			fingerBusiness.parameter = 115200;
    		break;
    		default:
    			fingerBusiness.parameter = 9600;
    		break;
    		}
        } 
  
        public void onNothingSelected(AdapterView<?> arg0) {  
        }  
    } 
	
	class GetTemplate_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBGetTemplate();//调用验证业务
		}
	}
	
	class VerifyTemplate_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBVerifyTemplate();//调用验证业务
		}
	}
	class IdentifyTemplate_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBIdentifyTemplate();//调用验证业务
		}
	}
	
	class DeviceInfo_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//fingerBusiness.idnumber = Integer.parseInt(et_ID.getText().toString());
			fingerBusiness.FBDeviceInfo();//调用验证业务
		}
	}
	class SetBaudrate_Listener implements Button.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			fingerBusiness.FBSetBaudrate();//调用验证业务
		}
	}
    private Button.OnClickListener mylistener = new Button.OnClickListener() {
    	@Override
    	public void onClick(View arg0) {
    	// TODO Auto-generated method stub
    	String string = password.getText().toString();
    	if(arg0.getId()!=R.id.btok || arg0.getId()!= R.id.back)
    	{
    		message = "";
    		result = "";
    		Result();
    	}
    	switch (arg0.getId()) 
    	{
	    	case R.id.one:
		    	password.setText(string + "1");
		    	break;
	    	case R.id.two:
		    	password.setText(string + "2");
		    	break;
	    	case R.id.three:
		    	password.setText(string + "3");
		    	break;
	    	case R.id.four:
		    	password.setText(string + "4");
		    	break;
	    	case R.id.five:
		    	password.setText(string + "5");
		    	break;
	    	case R.id.six:
		    	password.setText(string + "6");
		    	break;
	    	case R.id.seven:
		    	password.setText(string + "7");
		    	break;
	    	case R.id.eight:
		    	password.setText(string + "8");
		    	break;
	    	case R.id.nine:
		    	password.setText(string + "9");
		    	break;
	    	case R.id.btok:
		    	if (string.equals("123456")) 
		    	{
		    		//myJpgPath = "/mnt/sdcard/Pictures/correct1.jpg";
		    		message = "盞絏タ絋";
		    		result = "Θ\nSuccess";
		    		Result();
		    		setEnable(true);
		    		setPassWordEnable(false);
		    		errorcount = 0;

			    } 
		    	else 
		    	{
		    		//String myJpgPath = "com.example.image01:drawable/";
		    		//myJpgPath = "/mnt/sdcard/Pictures/001.jpg";
		    		errorcount++;
		    		message = "盞絏岿粇"+errorcount+"Ω";
		    		result = "ア毖\nFail";
		    		Result();
			    }
	    		password.setText("");	//clean screen and password
	    		string = "";
		    	break;
	    	case R.id.back:
		    		int length = string.trim().length();
		    		if (length == 0) 
		    		{
		    			break;
		    		} 
		    		else 
		    		{
				    	password.setText(string.substring(0,length-1));
				    	break;
		    		}
    
    		}
    
    	}
 
    	};
    private Button.OnClickListener myfingerlistener = new Button.OnClickListener() {
    	@Override
    	public void onClick(View arg0) {
    		message = "";
    		result = "";
    		Result();
    		switch (arg0.getId())
    		{
    			case R.id.enroll:
    				fingerBusiness.Login();
    				break;
    			case R.id.delete:
    				fingerBusiness.FBDeletAll();
    				break;
    			case R.id.fingerinput:
    				fingerBusiness.VerifyIdentify();
    				break;
    			default:
    				break;
    		}
    	}
    };
}
