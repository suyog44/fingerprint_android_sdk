package com.fys.handprint.Protocol;


/*
 * 常量定义
 */
public class Defined {

	public static final int USBPermission=0;
	public static final int USBnoPermission=1;
	public static final int USBINI=2;
	public static final int received=3;
	public static final int GETIMAGE=4;
	public static final int GETTemplate=5;
	public static final int ShowMessage =6;
	public static final int GETIMAGERaw =7;
	public static final int SETTemplate =8;
	public static final int NOUSB=9;
	public static final String BundlerDATA="DATA";
	public static final String FilePath="Finger";
	public static final int VERIFY=10;
	public static final int VERIFYCORRECT=20;
	public static final int VERIFYERROR=21;
	
	public static final int NACK_FINGER_IS_NOT_PRESSED=0x1012;
	
	public static final int cmdPackLen=12;
	public static final int responsePackLen=12;
	
	public static final short ACK_OK = 48;
	  public static final short CMD_ADD_TEMPLATE = 113;
	  public static final short CMD_ADJUST_SENSOR = 16;
	  public static final short CMD_CAPTURE = 96;
	  public static final short CMD_CHANGE_BAUDRATE = 4;
	  public static final short CMD_CHECK_ENROLLED = 33;
	  public static final short CMD_CLOSE = 2;
	  public static final short CMD_CMOS_INIT = 17;
	  public static final short CMD_CMOS_LED = 18;
	  public static final short CMD_DELETE = 64;
	  public static final short CMD_DELETE_ALL = 65;
	  public static final short CMD_ENROLL1 = 35;
	  public static final short CMD_ENROLL2 = 36;
	  public static final short CMD_ENROLL3 = 37;
	  public static final short CMD_ENROLL_COUNT = 32;
	  public static final short CMD_ENROLL_START = 34;
	  public static final short CMD_GET_CMOS_REG = 21;
	  public static final short CMD_GET_DATABASE_END = 115;
	  public static final short CMD_GET_DATABASE_START = 114;
	  public static final short CMD_GET_EEPROM = 19;
	  public static final short CMD_GET_IMAGE = 98;
	  public static final short CMD_GET_RAWIMAGE = 99;
	  public static final short CMD_GET_TEMPLATE = 112;
	  public static final short CMD_IAP_MODE = 5;
	  public static final short CMD_IDENTIFY = 81;
	  public static final short CMD_IDENTIFY_TEMPLATE = 83;
	  public static final short CMD_IS_PRESS_FINGER = 38;
	  public static final short CMD_MAKE_TEMPLATE = 97;
	  public static final short CMD_NONE = 0;
	  public static final short CMD_OPEN = 1;
	  public static final short CMD_SET_CMOS_REG = 22;
	  public static final short CMD_SET_EEPROM = 20;
	  public static final short CMD_UPGRADE_FIRMWARE = 128;
	  public static final short CMD_UPGRADE_ISO_IMAGE = 129;
	  public static final short CMD_USB_shortERNAL_CHECK = 3;
	  public static final short CMD_VERIFY = 80;
	  public static final short CMD_VERIFY_TEMPLATE = 82;
	  public static final int EEPROM_SIZE = 16;
	  public static final int FP_MAX_USERS = 2000;
	  public static final int FP_TEMPLATE_SIZE = 498;
	  public static final int NACK_BAD_FINGER = 4108;
	  public static final int NACK_CAPTURE_CANCELED = 4112;
	  public static final int NACK_COMM_ERR = 4102;
	  public static final int NACK_DB_IS_EMPTY = 4106;
	  public static final int NACK_DB_IS_FULL = 4105;
	  public static final int NACK_DEV_ERR = 4111;
	  public static final int NACK_ENROLL_FAILED = 4109;	
	  public static final int NACK_IDENTIFY_FAILED = 4104;
	  public static final int NACK_INFO = 49;
	  public static final int NACK_INVALID_BAUDRATE = 4098;
	  public static final int NACK_INVALID_PARAM = 4113;
	  public static final int NACK_INVALID_POS = 4099;
	  public static final int NACK_IS_ALREADY_USED = 4101;
	  public static final int NACK_IS_NOT_SUPPORTED = 4110;
	  public static final int NACK_IS_NOT_USED = 4100;
	  public static final int NACK_NONE = 4096;
	  public static final int NACK_TIMEOUT = 4097;
	  public static final int NACK_TURN_ERR = 4107;
	  public static final int NACK_VERIFY_FAILED = 4103;
	  public static final int OEM_COMM_ERR = -2001;
	  public static final int OEM_NONE = -2000;
}

