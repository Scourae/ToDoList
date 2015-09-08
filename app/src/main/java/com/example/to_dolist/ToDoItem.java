package com.example.to_dolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;

public class ToDoItem {
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss", Locale.CANADA);
	public enum Urgency {
		LOW, MEDIUM, HIGH
	};
	
	public final static String NAME = "name";
	public final static String URGENCY = "urgency";
	public final static String DATE = "date";
	public final static String DESCRIPTION = "description";
	
	private String mName;
	private Date mDate;
	private Urgency mUrgency;
	private String mDescription;
	
	ToDoItem(String name, Date date,  Urgency urgency, String description){
		mName = name;
		mDate = date;
		mUrgency = urgency;
		mDescription = description;
	}
	
	ToDoItem(Intent intent) {

		mName = intent.getStringExtra(ToDoItem.NAME);
		mUrgency = Urgency.valueOf(intent.getStringExtra(ToDoItem.URGENCY));
		mDescription = intent.getStringExtra(ToDoItem.DESCRIPTION);

		try {
			mDate = format.parse(intent.getStringExtra(ToDoItem.DATE));
		} catch (ParseException e) {
			mDate = new Date();
		}
	}
	
	public String getName(){
		return mName;
	}
	public String getDate() {
		return format.format(mDate);
	}
	public Date getDateRaw() {
		return mDate;
	}
	public Urgency getUrgency() {
		return mUrgency;
	}
	public String getDescription() {
		return mDescription;
	}
	
	public static void packageIntent(Intent intent, String name,
			Urgency urgency, String description, String date) {

		intent.putExtra(ToDoItem.NAME, name);
		intent.putExtra(ToDoItem.URGENCY, urgency.toString());
		intent.putExtra(ToDoItem.DESCRIPTION, description);
		intent.putExtra(ToDoItem.DATE, date);
	
	}
}