package com.beetoo.callblocker;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RingerReceiver extends BroadcastReceiver {
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		//Log.d("intent-action", intent.getAction());
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			if(ringerMode!=AudioManager.RINGER_MODE_NORMAL)
				return;
			Bundle extra = intent.getExtras();
			if(extra!=null && extra.containsKey(APP.PDUS)){
				int say_sms = getSettings(APP.SAY_SMS);
				if(say_sms==0)
					return;
				Object[] pdus = (Object[]) extra.get(APP.PDUS);
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[0]);
			    String senderNumber = sms.getOriginatingAddress();
			    String name = getContactName(senderNumber);
			    if(name==null)
			    	name = "Unknown";
			    String text = "Message from "+name+" : "+sms.getMessageBody();
			    speak(text,APP.TYPE_SMS);
			    Log.d("sms",text);
			}
			return;
		}
		else if(!intent.getAction().equals("android.intent.action.PHONE_STATE"))
			return;
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		int say_name = getSettings(APP.SAY_NAME);
		if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
			saveLastState(ringerMode);
			String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			DatabaseHandler db = new DatabaseHandler(context);
			String name = getContactName(number);
			List<String> list = db.getAllNumbers();
			if(list!=null && list.contains(number)){
				if(ringerMode==AudioManager.RINGER_MODE_NORMAL)
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}else if(name!=null){
				//Log.d("msg", "name: "+name);
				if(ringerMode!=AudioManager.RINGER_MODE_NORMAL || say_name==0)
					return;
				speak(name+" calling",APP.TYPE_CALL);
			}else{
				if(ringerMode!=AudioManager.RINGER_MODE_NORMAL || say_name==0)
					return;
				speak(APP.UNKNOWN+" calling",APP.TYPE_CALL);
			}
		}else{
			//Log.d("msg", "idle");
			restoreLastState();
			Intent i = new Intent(context,SpeakNameService.class);
			context.stopService(i);
		}
	}
	
	private String getContactName(String number) {
		// TODO Auto-generated method stub
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		String[] projection;
		projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
		Cursor contactLookup = context.getContentResolver().query(uri, projection, null,null,null);
		String name = null;
		if(contactLookup.moveToFirst())
			name = contactLookup.getString(0);
		contactLookup.close();
		return name;
	}

	private void saveLastState(int mode){
		SharedPreferences settings = context.getSharedPreferences(APP.PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(APP.MODE, mode);
		editor.commit();
	}
	
	private void restoreLastState(){
		AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(manager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL)
			return;
		SharedPreferences settings = context.getSharedPreferences(APP.PREFS, 0);
		int lastMode = settings.getInt(APP.MODE, APP.DEFAULT);
		if(lastMode==APP.DEFAULT)
			return;
		manager.setRingerMode(lastMode);
	}
	
	private int getSettings(String type){
		SharedPreferences settings = context.getSharedPreferences(APP.SETTINGS_PREFS, 0);
		int say_name = settings.getInt(type, APP.DEFAULT);
		if(say_name==APP.DEFAULT || say_name==0)
			return 0;
		else
			return say_name;
	}
	
	private void speak(String text,int type){
		Intent speakActivity = new Intent();
		speakActivity.setClass(context, SpeakNameService.class);
		speakActivity.putExtra("TEXT", text);
		speakActivity.putExtra("TYPE", type);
		context.startService(speakActivity);
	}
	
}
