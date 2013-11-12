package com.beetoo.callblocker;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.view.KeyEvent;
import android.widget.Toast;

public class SpeakNameService extends Service implements OnInitListener,OnUtteranceCompletedListener,SensorEventListener{
	private TextToSpeech tts = null;
	private String text;
	private int type;
	private SensorManager sensorManager;
	private Sensor accl;
	private float xAccl,yAccl,zAccl,xPrevAccl,yPrevAccl,zPrevAccl;
	private boolean firstUpdate;
	private boolean shakeInitiated;
	private float shakeThreshold;
	private boolean shakeSupported;
	
	private BroadcastReceiver stopSpeech;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		text = intent.getStringExtra("TEXT");
		type = intent.getIntExtra("TYPE", -1);
		if(type==-1)
			stopSelf();
		tts = new TextToSpeech(this,this);
		int shakeSetting = getSettings(APP.DO_SHAKE);
		if(shakeSetting==1 && type==APP.TYPE_CALL){
			sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
			accl = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		if(accl!=null){
			shakeSupported = true;
			firstUpdate = true;
			shakeInitiated = false;
			shakeThreshold = 9.0f;
			sensorManager.registerListener(this, accl, SensorManager.SENSOR_DELAY_UI);
		}else
			shakeSupported = false;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		stopSpeech = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				unregisterReceiver(stopSpeech);
				stopSelf();
			}
			
		};
		registerReceiver(stopSpeech, new IntentFilter(APP.CLOSE));
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		// TODO Auto-generated method stub
		if(utteranceId.equals(APP.CLOSE)){
			if(type==APP.TYPE_SMS){
				stopSelf();
				return;
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, APP.CLOSE);
			tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if(status==TextToSpeech.SUCCESS){
			int result = tts.setLanguage(Locale.getDefault());
			if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
				//Log.d("msg","not supported");
				stopSelf();
			}
			else{
				if(Build.VERSION.SDK_INT>=15){
					tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
						
						@Override
						public void onStart(String utteranceId) {
							// TODO Auto-generated method stub
							//Log.d("utter-start",utteranceId);
						}
						
						@Override
						public void onError(String utteranceId) {
							// TODO Auto-generated method stub
							//Log.d("utter-error",utteranceId);
						}
						
						@Override
						public void onDone(String utteranceId) {
							// TODO Auto-generated method stub
							if(utteranceId.equals(APP.CLOSE)){
								if(type==APP.TYPE_SMS){
									stopSelf();
									return;
								}
								try {
									Thread.sleep(800);
								} catch (InterruptedException e) {
									Toast.makeText(getApplicationContext(), "Thread exception." , Toast.LENGTH_SHORT).show();
								}
								HashMap<String, String> params = new HashMap<String, String>();
								params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, APP.CLOSE);
								tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
								//Log.d("utter-done",utteranceId);
							}
						}
					});
				}else{
					tts.setOnUtteranceCompletedListener(this);
				}
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, APP.CLOSE);
				tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
			}
		}else{
			stopSelf();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(stopSpeech);
		tts.shutdown();
		if(shakeSupported)
			sensorManager.unregisterListener(this);
		super.onDestroy();
		//Log.d("finish", "finishing");
	}
	
	private int getSettings(String type){
		SharedPreferences settings = getSharedPreferences(APP.SETTINGS_PREFS, 0);
		int say_name = settings.getInt(type, APP.DEFAULT);
		if(say_name==APP.DEFAULT || say_name==0)
			return 0;
		else
			return say_name;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void onSensorChanged(SensorEvent se) {
		// TODO Auto-generated method stub
		updateAccelParameters(se.values[0], se.values[1], se.values[2]);
        if ((!shakeInitiated) && isAccelerationChanged()) {
        	shakeInitiated = true; 
        } else if ((shakeInitiated) && isAccelerationChanged()) {
        	executeShakeAction();
        	if(shakeSupported)
    			sensorManager.unregisterListener(this);
        } else if ((shakeInitiated) && (!isAccelerationChanged())) {
        	shakeInitiated = false;
        }
	}

	private void updateAccelParameters(float xNewAccel, float yNewAccel,float zNewAccel) {
		// TODO Auto-generated method stub
		if(firstUpdate){
			xPrevAccl = xNewAccel;
			yPrevAccl = yNewAccel;
			zPrevAccl = zNewAccel;
			firstUpdate = false;
		}else{
			xPrevAccl = xAccl;
			yPrevAccl = yAccl;
			zPrevAccl = zAccl;
		}
		xAccl = xNewAccel;
		yAccl = yNewAccel;
		zAccl = zNewAccel;
	}

	private boolean isAccelerationChanged() {
		// TODO Auto-generated method stub
		float dx = Math.abs(xPrevAccl-xAccl);
		float dy = Math.abs(yPrevAccl-yAccl);
		float dz = Math.abs(zPrevAccl-zAccl);
		return ((dx>shakeThreshold && dy>shakeThreshold)||(dx>shakeThreshold && dz>shakeThreshold)||(dy>shakeThreshold && dz>shakeThreshold));
	}

	@SuppressLint("NewApi")
	private void executeShakeAction() {
		// TODO Auto-generated method stub
		Intent answer = new Intent(Intent.ACTION_MEDIA_BUTTON);
		answer.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_HEADSETHOOK));
		sendOrderedBroadcast(answer, "android.permission.CALL_PRIVILEGED");
	}

}
