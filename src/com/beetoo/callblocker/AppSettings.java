package com.beetoo.callblocker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;


public class AppSettings extends Activity {
	
	private static final String PREFS 		= "SETTINGS";
	private static final String SAY_NAME 	= "SAY_NAME";
	private static final String SAY_SMS 	= "SAY_SMS";
	private static final String DO_SHAKE	= "DO_SHAKE";
	private static final int CANCEL 		= -1;
	private static final int CHECKED 		= 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		setListeners();
		refreshView();
	}

	private void refreshView() {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		int say_name = settings.getInt(SAY_NAME, CANCEL);
		int say_sms = settings.getInt(SAY_SMS, CANCEL);
		int do_shake = settings.getInt(DO_SHAKE, CANCEL);
		CheckBox sayName = (CheckBox)findViewById(R.id.say_name);
		CheckBox saySms = (CheckBox)findViewById(R.id.say_sms);
		CheckBox doShake = (CheckBox)findViewById(R.id.do_shake);
		if(say_name==CANCEL || say_name==0)
			sayName.setChecked(false);
		else if(say_name==CHECKED)
			sayName.setChecked(true);
		if(say_sms==CANCEL || say_sms==0)
			saySms.setChecked(false);
		else if(say_sms==CHECKED)
			saySms.setChecked(true);
		if(do_shake==CANCEL || do_shake==0)
			doShake.setChecked(false);
		else if(do_shake==CHECKED)
			doShake.setChecked(true);
	}

	private void setListeners() {
		// TODO Auto-generated method stub
		Button save_btn = (Button)findViewById(R.id.save_button);
		save_btn.setOnClickListener(saveSettings);
	}
	
	private OnClickListener saveSettings = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int setting1,setting2,setting3;
			CheckBox say = (CheckBox)findViewById(R.id.say_name);
			CheckBox sms = (CheckBox)findViewById(R.id.say_sms);
			CheckBox shake = (CheckBox)findViewById(R.id.do_shake);
			setting1 = say.isChecked()?CHECKED:0;
			setting2 = sms.isChecked()?CHECKED:0;
			setting3 = shake.isChecked()?CHECKED:0;
			SharedPreferences settings = getSharedPreferences(PREFS, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(SAY_NAME, setting1);
			editor.putInt(SAY_SMS, setting2);
			editor.putInt(DO_SHAKE, setting3);
			editor.commit();
			finish();
		}
	};
	
}
