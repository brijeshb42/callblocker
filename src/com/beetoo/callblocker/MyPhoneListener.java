package com.beetoo.callblocker;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class MyPhoneListener extends PhoneStateListener {
	private Context context;
	
	public MyPhoneListener(Context ctx){
		this.context = ctx;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		// TODO Auto-generated method stub
		if(state==TelephonyManager.CALL_STATE_RINGING){
			rejectCall(context);
			Log.d("msg", "num: "+incomingNumber);
		}else if(state==TelephonyManager.CALL_STATE_IDLE){
			AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamMute(AudioManager.STREAM_RING, false);
		}
		super.onCallStateChanged(state, incomingNumber);
	}

	private void rejectCall(Context ctx) {
		// TODO Auto-generated method stub
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		//audioManager.setStreamMute(AudioManager.STREAM_RING, true);
		try{
			Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			btnUp.putExtra(Intent.EXTRA_KEY_EVENT,new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			ctx.sendOrderedBroadcast(btnUp, "android.permission.CALL_PRIVILEGED");
		}catch(Exception e){
			Log.d("msg",e.getMessage());
		}
	}
}