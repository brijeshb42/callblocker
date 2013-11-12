package com.beetoo.callblocker;

public class Contact {
	private int id;
	private String name,number;
	
	public Contact(){
		
	}
	public Contact(int Id,String name,String number){
		this.id = Id;
		this.name = name;
		this.number = number;
	}
	public Contact(String name,String number){
		this.name = name;
		this.number = number;
	}
	public int getID(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getNumber(){
		return this.number;
	}
	public void setNumber(String num){
		this.number = num;
	}
}
