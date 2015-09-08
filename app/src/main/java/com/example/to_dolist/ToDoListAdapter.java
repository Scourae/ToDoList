package com.example.to_dolist;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ToDoListAdapter extends BaseAdapter {
	private List <ToDoItem> mToDoItems;
	private Context mContext;
	private AlarmManager mAlarmManager;

	private final long tenMin = 10*60*1000L;
	
	public ToDoListAdapter(Context context) {
		mContext = context;
		mToDoItems = new ArrayList<ToDoItem> ();
	}
	
	public void add(ToDoItem object){
		if (mToDoItems.size() == 0)
			mToDoItems.add(object);
		else
		{	
			int i;
			for (i = 0; i < mToDoItems.size(); i++)
			{
				if (mToDoItems.get(i).getDateRaw().after(object.getDateRaw()))
				{
					mToDoItems.add(i, object);
					break;
				}
			}
			if (i == mToDoItems.size())
				mToDoItems.add(object);
		}
		setAlarm(object);
		notifyDataSetChanged();
	}
	
	public void edit(ToDoItem object, int previousPosition)
	{
		mToDoItems.remove(previousPosition);
		add(object);
	}
	
	public void remove(int position) {
		removeAlarm(mToDoItems.get(position));
		mToDoItems.remove(position);
		notifyDataSetChanged();
	}

	public void removeItem(ToDoItem item) {
		removeAlarm(item);
		mToDoItems.remove(item);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mToDoItems.size();
	}

	@Override
	public ToDoItem getItem(int position) {
		return mToDoItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		RelativeLayout itemLayout;
		TextView itemTitle;
		TextView itemDate;
		TextView itemDescription;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null)
		{
			holder = new ViewHolder ();
			LayoutInflater inflate = LayoutInflater.from(mContext);
			holder.itemLayout = (RelativeLayout) inflate.inflate(R.layout.to_do_item, null);
			holder.itemTitle = (TextView) holder.itemLayout.findViewById(R.id.ToDoItemTitle);
			holder.itemDate = (TextView) holder.itemLayout.findViewById(R.id.ToDoItemDate);
            holder.itemDescription = (TextView) holder.itemLayout.findViewById(R.id.ToDoItemDescription);
			convertView = holder.itemLayout;
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.itemTitle.setText(mToDoItems.get(position).getName());
		holder.itemDate.setText(mToDoItems.get(position).getDate());
        holder.itemDescription.setText(mToDoItems.get(position).getDescription());
		
		switch (mToDoItems.get(position).getUrgency())
		{
			case LOW: 
				holder.itemLayout.setBackgroundColor(0xFF48B427);
				break;
			case MEDIUM: 
				holder.itemLayout.setBackgroundColor(0xFFEFFD0E);
				break;
			case HIGH: 
				holder.itemLayout.setBackgroundColor(0xFFFF0000);
		}
			
		return convertView;
	}

	private void setAlarm(ToDoItem item) {
		mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent notificationReceiverIntent = new Intent(mContext, NotificationReceiver.class);
		notificationReceiverIntent.putExtra(ToDoItem.NAME, item.getName());
		notificationReceiverIntent.putExtra(ToDoItem.DESCRIPTION, item.getDescription());
		notificationReceiverIntent.putExtra(ToDoItem.DATE, item.getDateRaw().getTime());
		notificationReceiverIntent.putExtra(ToDoItem.URGENCY, item.getUrgency().ordinal());

		PendingIntent notificationReceiverPendingIntent = PendingIntent.getBroadcast(mContext, 0, notificationReceiverIntent, PendingIntent.FLAG_ONE_SHOT);
		if (Build.VERSION.SDK_INT >= 19) {
			mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, item.getDateRaw().getTime()-tenMin, notificationReceiverPendingIntent);
		}
		else {
			mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, item.getDateRaw().getTime() - tenMin, notificationReceiverPendingIntent);
		}
	}

	private void removeAlarm(ToDoItem item) {
		mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent notificationReceiverIntent = new Intent(mContext, NotificationReceiver.class);
		notificationReceiverIntent.putExtra(ToDoItem.NAME, item.getName());
		notificationReceiverIntent.putExtra(ToDoItem.DESCRIPTION, item.getDescription());
		notificationReceiverIntent.putExtra(ToDoItem.DATE, item.getDateRaw().getTime());
		notificationReceiverIntent.putExtra(ToDoItem.URGENCY, item.getUrgency().ordinal());

		PendingIntent notificationReceiverPendingIntent = PendingIntent.getBroadcast(mContext, 0, notificationReceiverIntent, PendingIntent.FLAG_ONE_SHOT);

		mAlarmManager.cancel(notificationReceiverPendingIntent);
	}
}