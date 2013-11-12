package com.beetoo.callblocker;

//import java.lang.reflect.Method;

//import android.content.Context;
//import android.media.AudioManager;
import android.telephony.PhoneStateListener;
/*import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;*/

public class MyPhoneListener_Extra extends PhoneStateListener {
	/*private Context context;
	
	public MyPhoneListener_Extra(Context ctx){
		this.context = ctx;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		// TODO Auto-generated method stub
		if(state==TelephonyManager.CALL_STATE_RINGING){
			Log.d("msg", "incoming: "+incomingNumber);
			AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamMute(AudioManager.STREAM_RING, true);
			TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			try{
				Toast.makeText(context, incomingNumber+" calling", Toast.LENGTH_SHORT).show();
				Class clazz = Class.forName(telephonyManager.getClass().getName());
				Method method = clazz.getDeclaredMethod("getITelephony");
				method.setAccessible(true);
				//ITelephony telephonyService = (ITelephony)method.invoke(telephonyManager);
				//telephonyService.silenceRinger();
				//telephonyService.endCall();
			}catch(Exception e){
				Log.d("msg","exception: "+e.getMessage());
			}
			audioManager.setStreamMute(AudioManager.STREAM_RING, false);
		}
		super.onCallStateChanged(state, incomingNumber);
	}*/
}
