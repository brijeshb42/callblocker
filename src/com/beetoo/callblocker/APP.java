package com.beetoo.callblocker;

public class APP {
	static final String SETTINGS_PREFS 	= "SETTINGS";
	static final String SAY_NAME 	= "SAY_NAME";
	static final String SAY_SMS 	= "SAY_SMS";
	static final String DO_SHAKE	= "DO_SHAKE";
	static final String STATE_PREFS = "LAST_STATE";
	static final String MODE 		= "mode";
	static final String UNKNOWN 	= "Unknown";
	static final String PDUS 		= "pdus";
	static final String CLOSE		= "close";
	static final String DB_NAME 	= "BlockedNumbers";
	static final String TABLE_CONTACTS = "contacts";
	static final String KEY_ID 		= "id";
	static final String KEY_NAME 	= "name";
	static final String KEY_PH_NO 	= "phone_number";
	static final String PREFS 			= "LAST_STATE";
	
	static final int DEFAULT 		= -1;
	static final int CANCEL 		= -1;
	static final int CHECKED 		= 1;
	static final int TYPE_SMS       = 10;
	static final int TYPE_CALL      = 11;
	static final int TYPE_SHAKE   	= 12;
	static final int RES_OK 		= 1;
	static final int RES_ERROR 		= -1;
	static final int RES_CANCEL 	= 0;
	static final int CONTACT_PICKER_ACTIVITY = 1111;
	static final int LOG_PICKER_ACTIVITY 	 = 1112;
	static final int DB_VERSION 	= 2;
	static final int VIBRATE_LENGTH = 500;
}
