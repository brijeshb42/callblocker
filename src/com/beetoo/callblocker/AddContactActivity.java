package com.beetoo.callblocker;

import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		setListeners();
	}

	private void setListeners() {
		// TODO Auto-generated method stub
		Button btn = (Button)findViewById(R.id.add_contact_btn);
		btn.setOnClickListener(saveContact);
		Button from_contact_btn = (Button)findViewById(R.id.from_contacts);
		from_contact_btn.setOnClickListener(from_contact);
		Button from_log_btn = (Button)findViewById(R.id.from_log);
		from_log_btn.setOnClickListener(from_log);
		Button cancelBtn = (Button)findViewById(R.id.cancel_contact);
		cancelBtn.setOnClickListener(goBack);
	}
	
	private View.OnClickListener saveContact = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			EditText nameEdit = (EditText)findViewById(R.id.contact_name);
			EditText numberEdit = (EditText)findViewById(R.id.contact_number);
			String name = nameEdit.getText().toString().trim();
			String number = numberEdit.getText().toString().trim();
			if(name.equals("") || number.equals("")){
				Toast.makeText(getApplicationContext(), "Empty field(s)", Toast.LENGTH_SHORT).show();
			}else{
				number = number.replace(" ", "").replace("-", "");
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
				Contact contact = new Contact(name,number);
				long id = db.addNumber(contact);
				if(id==APP.RES_ERROR){
					setResult(APP.RES_ERROR);
					finish();
				}
				//Log.d("msg", "last-id "+String.valueOf(id));
				Intent intent=new Intent();
			    intent.putExtra("lastID", id);
				setResult(APP.RES_OK,intent);
				finish();
			}
		}
	};
	
	private View.OnClickListener goBack = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setResult(APP.RES_CANCEL);
			finish();
		}
	};
	
	private View.OnClickListener from_contact = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,Contacts.CONTENT_URI);
			startActivityForResult(contactPickerIntent, APP.CONTACT_PICKER_ACTIVITY);
		}
		
	};
	
	private View.OnClickListener from_log = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent logPickerIntent = new Intent(Intent.ACTION_VIEW,CallLog.Calls.CONTENT_FILTER_URI);
			startActivityForResult(logPickerIntent, APP.LOG_PICKER_ACTIVITY);
		}
		
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("msg",String.valueOf(requestCode)+" : "+String.valueOf(resultCode));
		switch(requestCode){
		case APP.CONTACT_PICKER_ACTIVITY: 
					if(resultCode==APP.RES_ERROR){
						Cursor cursor = null;
						String disp_name = "";
						String number = "";
						try{
							Uri result = data.getData();
							Log.d("msg", result.toString());
							String id = result.getLastPathSegment();
							cursor = getContentResolver().query(Phone.CONTENT_URI,null,Phone.CONTACT_ID+"=?",new String[]{id},null);
							int nameId = cursor.getColumnIndex(Phone.DISPLAY_NAME);
							int numId = cursor.getColumnIndex(Phone.NUMBER);
							if(cursor.moveToFirst()){
								disp_name = cursor.getString(nameId);
								number = cursor.getString(numId);
								number = number.replace(" ", "").replace("-", "");
							}
						}catch(Exception e){
							Log.e("msg", "Failed to get data");
						}finally{
							if(cursor==null)
								break;
							cursor.close();
							EditText edName = (EditText)findViewById(R.id.contact_name);
							edName.setText(disp_name);
							edName = (EditText)findViewById(R.id.contact_number);
							edName.setText(number);
						}
					}
					break;
		default: break;
		}
	};

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
	}*/

}
