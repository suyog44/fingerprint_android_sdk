package  com.example.fpapi.Lib;

public class JavaFP {

	public final int 	OK=0;
	public final int FAIL=-1;
	public final int	ENROLL_NOT_YET=-7;
static
{
	System.loadLibrary("FPAPI");	
}


	/*
	Width, Height : [Input] ]Set the Image Size
	return OK if no error
	*/
	public  native int Start(int Width,int Height);
	/*
	return OK if no error
	*/
	public  native int End();
	/*
	lpImage : [Input] ] the Image Buffer of Width * Height
	return OK if the image quality is OK
	*/
	public  native int Quality(byte[] lpImage);
	/*
	lpImage : [Input] ] the Image Buffer of Width * Height
	return OK if no finger is on the reader
	*/
	public  native int Remove(byte[] lpImage);
	/*
	rRawTemplate : [Output] ] the Template Buffer of 512 Bytes
	return OK if no error
	*/
	public  native int Template(byte[] rRawTemplate);
	/*
	RawTemplate : [Input] ] the Template Buffer by FP_Template
	rEnrlTemplate : [Output] ] the Template Buffer by FP_Enroll
	return -7 if not yet OK
	return 65~68 if OK
	*/
	public  native int Enroll(byte[] RawTemplate, byte[] rEnrlTemplate);
	/*
	return OK if no error
	*/
	public  native int EndEnroll();
	/*
	rRawTemplate : [Input] ] the Template Buffer by FP_Template
	EnrlTemplate : [Input] ] the Template Buffer by FP_Enroll
	threshold : [Input] 0~100, default : 50
	rScore :  [Output] 0~100, the matching score

	return OK if no error
	*/

	public  native int Verify(byte[] FP_Template,byte[] FP_Enroll,byte threshold,byte rScore);

}
