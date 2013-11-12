package com.beetoo.callblocker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	
	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "BlockedNumbers";
	private static final String TABLE_CONTACTS = "contacts";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_PH_NO = "phone_number";

	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" +KEY_ID +" INTEGER PRIMARY KEY, " +KEY_NAME+ " TEXT, " +KEY_PH_NO+ " TEXT UNIQUE)";
		db.execSQL(CREATE_CONTACTS_TABLE);
		Log.d("msg",CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACTS);
		onCreate(db);
	}
	
	public long addNumber(Contact contact){
		Log.d("msg","added contact");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, contact.getName());
		values.put(KEY_PH_NO, contact.getNumber());
		long id = db.insert(TABLE_CONTACTS, null, values);
		db.close();
		return id;
	}
	
	public Contact getContact(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS,new String[] {KEY_ID,KEY_NAME,KEY_PH_NO},KEY_ID+"=?",new String[] {String.valueOf(id)},null,null,null,null);
		if(cursor!=null)
			cursor.moveToFirst();
		else
			return null;
		Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
		db.close();
		return contact;
	}
	
	public List<Contact> getAllContacts(){
		List<Contact> contactList = new ArrayList<Contact>();
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS+" ORDER BY "+KEY_NAME+" ASC";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			do{
				Contact contact = new Contact();
				contact.setId(Integer.parseInt(cursor.getString(0)));
				contact.setName(cursor.getString(1));
				contact.setNumber(cursor.getString(2));
				contactList.add(contact);
			}while(cursor.moveToNext());
		}else
			return null;
		db.close();
		return contactList;
	}
	
	public List<String> getAllNumbers(){
		List<String> numList = new ArrayList<String>();
		String selectQuery = "SELECT "+KEY_PH_NO+" FROM "+TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst()){
			do{
				numList.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}else{
			db.close();
			return null;
		}
		db.close();
		return numList;
	}
	
	public int getCount(){
		SQLiteDatabase db = this.getWritableDatabase();
		String countQuery = "SELECT COUNT(*) AS COUNT FROM "+TABLE_CONTACTS;
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor!=null)
			cursor.moveToFirst();
		else
			return 0;
		int count = Integer.parseInt(cursor.getString(0));
		db.close();
		return count;
	}
	
	public void deleteContact(long id){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, KEY_ID+"=?", new String[] {String.valueOf(id)});
		db.close();
	}
}
