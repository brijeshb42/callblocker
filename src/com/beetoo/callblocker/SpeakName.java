package com.beetoo.callblocker;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

public class SpeakName extends Activity implements OnInitListener,OnUtteranceCompletedListener{
	
	private static final String CLOSE = "close";
	private TextToSpeech tts = null;
	private String name;
	
	private BroadcastReceiver stopSpeech;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent startingIntent = this.getIntent();
		name = startingIntent.getStringExtra("NAME");
		tts = new TextToSpeech(this,this);
		stopSpeech = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				unregisterReceiver(stopSpeech);
				finish();
			}
			
		};
		registerReceiver(stopSpeech, new IntentFilter(CLOSE));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(tts!=null)
			tts.shutdown();
		finish();
	}

	/*@Override
	public void onUtteranceCompleted(String arg0) {
		// TODO Auto-generated method stub
		
	}*/

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if(status==TextToSpeech.SUCCESS){
			int result = tts.setLanguage(Locale.US);
			if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
				Log.d("msg","not supported");
				finish();
			}
			else{
				if(Build.VERSION.SDK_INT>=15){
					tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
						
						@Override
						public void onStart(String utteranceId) {
							// TODO Auto-generated method stub
							//tts.speak(name+" calling", TextToSpeech.QUEUE_FLUSH, null);
						}
						
						@Override
						public void onError(String utteranceId) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onDone(String utteranceId) {
							// TODO Auto-generated method stub
							tts.speak(name+" calling", TextToSpeech.QUEUE_FLUSH, null);
						}
					});
				}else{
					tts.setOnUtteranceCompletedListener(this);
				}
				tts.speak(name+" calling", TextToSpeech.QUEUE_FLUSH, null);
			}
		}else{
			finish();
		}
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		// TODO Auto-generated method stub
		//tts.shutdown();
		//finish();
		tts.speak(name+" calling", TextToSpeech.QUEUE_FLUSH, null);
	}

}
