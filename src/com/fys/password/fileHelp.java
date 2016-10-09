package com.fys.password;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.fys.handprint.Protocol.Defined;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
/*
 * 对SD卡的文件读写业务
 * 	1、在SD卡建立 Defined.FilePath目录
 * 	2、在指定目录下写文件
 */
public class fileHelp {

	String TAG="fileHelp";
	Context context;
	String path;
	public fileHelp(Context c)
	{
		context=c;
		String SDPATH=Environment.getExternalStorageDirectory().getAbsolutePath();
		path=SDPATH+"/"+Defined.FilePath+"/";
		isExist(path);
		Log.e(TAG, path);
	}
	/**
	* 
	* @param path 文件夹路径
	*/
	public  void isExist(String path) {
	File file = new File(path);
	//判断文件夹是否存在,如果不存在则创建文件夹
	if (!file.exists()) {
	file.mkdir();
	}
	}
	/**
	* 
	* @param path 文件夹路径
	* @file file 文件
	*/
	public void WriteFile(String name,byte[] buffer) 
	{
		File file = new File(path, name);
		try
		{
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(buffer);
		outStream.close();
		}
		catch(Exception er)
		{
			Log.e(TAG, "写入文件失败！"+er.toString());
		}
	}
	
}
