package com.beetoo.callblocker;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ListView contactList;
	private ContactListAdapter mainAdapter;
	private List<Contact> contactContent;
	private TextView loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		contactList = (ListView)findViewById(R.id.contactList);
		contactList.setTextFilterEnabled(true);
		loading = (TextView)findViewById(R.id.loading);
		setListeners();
		registerForContextMenu(contactList);
	}

	private void setListeners() {
		// TODO Auto-generated method stub
		Button btn = (Button)findViewById(R.id.addContact);
		btn.setOnClickListener(startAddForm);
		/*contactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,long id) {
				// TODO Auto-generated method stub
				ContactListAdapter.ContactHolder holder = (ContactListAdapter.ContactHolder)view.getTag();
				Log.d("msg","pos-"+String.valueOf(pos)+"id-"+String.valueOf(holder.id));
			}
		});*/
	}
	
	private View.OnClickListener startAddForm = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent in = new Intent(v.getContext(),AddContactActivity.class);
			startActivityForResult(in, 0);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.add_item: Intent in = new Intent(getApplicationContext(),AddContactActivity.class);
							startActivityForResult(in, 0);
							break;
		case R.id.settings_item: Intent in1 = new Intent(getApplicationContext(),AppSettings.class);
							startActivity(in1);
							break;
		default: break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.d("msg",String.valueOf(requestCode)+" : "+String.valueOf(resultCode));
		// TODO Auto-generated method stub
		switch(resultCode){
			case APP.RES_OK: Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show();
						 //refreshContacts();
						new GetContactListAsync().execute();
						 break;
			case APP.RES_ERROR: Toast.makeText(this, "Number already present", Toast.LENGTH_SHORT).show();
						break;
			case APP.RES_CANCEL: break;
			default: break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.contact_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		ContactListAdapter.ContactHolder holder = (ContactListAdapter.ContactHolder)info.targetView.getTag();
		long id = holder.id;
		switch(item.getItemId()){
			case R.id.action_delete: DatabaseHandler db = new DatabaseHandler(getBaseContext());
				db.deleteContact(id);
				Animation anim = AnimationUtils.loadAnimation(getBaseContext(), android.R.anim.slide_out_right);
				anim.setDuration(800);
				info.targetView.startAnimation(anim);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						contactContent.remove(info.position);
						mainAdapter.notifyDataSetChanged();
					}
				}, anim.getDuration());
				//new GetContactListAsync().execute();
				Toast.makeText(getBaseContext(), "Contact deleted.", Toast.LENGTH_SHORT).show();
				if(contactContent.size()<1){
					loading.setText(R.string.no_contact);
					loading.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.action_edit: 
				break;
			default: break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new GetContactListAsync().execute();
		//Toast.makeText(this, Locale.getDefault().toString(), Toast.LENGTH_SHORT).show();
	}
	
	private class GetContactListAsync extends AsyncTask<Void, Void, List<Contact>>{

		@Override
		protected List<Contact> doInBackground(Void... urls) {
			DatabaseHandler db = new DatabaseHandler(getBaseContext());
			List<Contact> contacts= db.getAllContacts();
			//Contact[] list;
			if(contacts!=null && contacts.size()>0){
				//Log.d("msg","contact-size "+String.valueOf(contacts.size()));
				//list = new Contact[contacts.size()];
				contactContent = contacts;
				return contacts;
			}else{
				return null;
			}
		}

		@Override
        protected void onPostExecute(List<Contact> list){
			if(list == null){
				loading.setText(R.string.no_contact);
			}
			else{
				ContactListAdapter contactAdapter = new ContactListAdapter(getApplicationContext(), R.layout.contact_list_item, list);
				mainAdapter = contactAdapter;
				contactList.setAdapter(mainAdapter);
				loading.setVisibility(View.INVISIBLE);
				contactList.setVisibility(View.VISIBLE);
			}
       }
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			loading.setText(R.string.loading);
			loading.setVisibility(View.VISIBLE);
			contactList.setVisibility(View.INVISIBLE);
			super.onPreExecute();
		}
	}
}
