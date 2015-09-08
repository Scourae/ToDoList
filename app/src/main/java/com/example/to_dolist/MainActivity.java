package com.example.to_dolist;
/**
 * Main functionality of the To Do List
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.to_dolist.SwipeDetector.Action;
import com.example.to_dolist.ToDoItem.Urgency;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private static final String mToDoFileName = "ToDoData";
	private ToDoListAdapter mToDoList;
	SwipeDetector swipeDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mToDoList = new ToDoListAdapter(this);
		getListView().setAdapter(mToDoList);
		loadToDo();
		swipeDetector = new SwipeDetector();
		getListView().setOnTouchListener(swipeDetector);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		createAlerts();
		if (mToDoList.getCount() == 0)
			loadToDo();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveToDo();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.to_do_activity_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Intent addItem = new Intent(MainActivity.this, AddToDoActivity.class);
			startActivityForResult(addItem, 1);
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK)
		{
			if (requestCode == 1)
			{
				ToDoItem newThing = new ToDoItem(data);
				mToDoList.add(newThing);
			}
			else if (requestCode == 2)
			{
				ToDoItem editedThing = new ToDoItem(data);
				int position = data.getIntExtra("position", -1);
				mToDoList.edit(editedThing, position);
			}
		}
	}
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
		if(swipeDetector.swipeDetected()) 
		{
            if (swipeDetector.getAction() == Action.LtR) 
            {
            	mToDoList.remove(position);
            } 
            else if (swipeDetector.getAction() == Action.RtL) 
            {
            	Intent editItem = new Intent(MainActivity.this, EditToDoActivity.class);
            	editItem.putExtra(ToDoItem.NAME, mToDoList.getItem(position).getName());
            	editItem.putExtra(ToDoItem.URGENCY, mToDoList.getItem(position).getUrgency().toString());
            	editItem.putExtra(ToDoItem.DESCRIPTION, mToDoList.getItem(position).getDescription());
            	editItem.putExtra(ToDoItem.DATE, mToDoList.getItem(position).getDate());
            	editItem.putExtra("position", position);
            	startActivityForResult(editItem, 2);
            }
        } 
	}
	
	private void loadToDo(){
		try {
			FileInputStream fout = openFileInput(mToDoFileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fout));
			
			String name;
			Date date;
			String urgency;
			String description;
			
			while((name = reader.readLine()) != null)
			{
				date = ToDoItem.format.parse(reader.readLine());
				urgency = reader.readLine();
				description = reader.readLine();
				
				mToDoList.add(new ToDoItem(name, date, Urgency.valueOf(urgency), description));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		
	}
	
	private void saveToDo(){
		try {
			FileOutputStream fout = openFileOutput(mToDoFileName, Context.MODE_PRIVATE);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fout));
			
			ToDoItem item;
			for (int i = 0; i < mToDoList.getCount(); i++)
			{
				item = mToDoList.getItem(i);
				writer.write(item.getName());
				writer.newLine();
				writer.write(item.getDate());
				writer.newLine();
				writer.write(item.getUrgency().toString());
				writer.newLine();
				writer.write(item.getDescription());
				writer.newLine();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createAlerts() {
		List<AlertDialog> builderList = new ArrayList<>();
		Date date = new Date();
		for (int i = 0; i < mToDoList.getCount(); i++) {
			final ToDoItem item = mToDoList.getItem(i);
			if (date.after(item.getDateRaw())) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(item.getName())
						.setMessage(item.getDate() + "\n" + item.getDescription())
						.setPositiveButton(R.string.keep, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								// do nothing
							}
						})
						.setNegativeButton(R.string.remove, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								mToDoList.removeItem(item);
							}
						});
				builderList.add(builder.create());
			}
			else {
				break;
			}
		}

		// display oldest at the top
		for (int i = builderList.size()-1; i >= 0; i--) {
			builderList.get(i).show();
		}
	}
}
