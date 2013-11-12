package com.beetoo.callblocker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactListAdapter extends ArrayAdapter<Contact>{
	
	private int layoutID;
	//private static final int ROW_ID = 1;
	private Context context;
	private List<Contact> list;// = null;

	public ContactListAdapter(Context context, int layoutID,List<Contact> list) {
		super(context, layoutID, list);
		this.context = context;
		this.layoutID = layoutID;
		this.list = list;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		ContactHolder holder = null;
		if(row == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutID, parent, false);
			holder = new ContactHolder();
			holder.name = (TextView)row.findViewById(R.id.contact_name);
			holder.number = (TextView)row.findViewById(R.id.contact_number);
			row.setTag(holder);
		}
		else{
			holder = (ContactHolder)row.getTag();
		}
		
		Contact contact = list.get(position);
		holder.name.setText(contact.getName());
		holder.number.setText(contact.getNumber());
		holder.id = contact.getID();
		return row;
		//return super.getView(position, convertView, parent);
	}
	
	static class ContactHolder{
		TextView name,number;
		public int id;
	}
	
}
