package com.example.to_dolist;

import java.util.Calendar;
import java.util.Date;

import com.example.to_dolist.ToDoItem.Urgency;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddToDoActivity extends Activity {
	private EditText mNameText;
	private RadioButton mDefaultUrgencyButton;
	private RadioGroup mUrgencyRadioGroup;
	private EditText mDescriptionText;
	private static TextView mDateView;
	private static TextView mTimeView;
	
	private static String timeString;
	private static String dateString;
	private Date mDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_to_do_item);

		mNameText = (EditText) findViewById(R.id.name);
		mDefaultUrgencyButton = (RadioButton) findViewById(R.id.urgencyLow);
		mUrgencyRadioGroup = (RadioGroup) findViewById(R.id.urgencyGroup);
		mDescriptionText = (EditText) findViewById(R.id.description);
		mDateView = (TextView) findViewById(R.id.date);
		mTimeView = (TextView) findViewById(R.id.time);

		setDefaultDateTime();

		Button datePickerButton = (Button) findViewById(R.id.date_picker);
		datePickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePicker();
			}
		});

		Button timePickerButton = (Button) findViewById(R.id.time_picker);
		timePickerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimePicker();
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent end = new Intent();
				setResult(1, end);
				finish();
			}
		});

		final Button resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDefaultDateTime();
				mNameText.setText("");
				mDefaultUrgencyButton.setChecked(true);
				mDescriptionText.setText("");
			}
		});

		final Button submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String nameString = getName();
				if (nameString == "")
				{
					Toast.makeText(getApplicationContext(), "Error: Name not set!", Toast.LENGTH_SHORT).show();
					return;
				}
				Urgency urgency = getUrgency();
				String fullDate = dateString + " at " + timeString;
				String descriptionString = getDescription();
				Intent data = new Intent();
				ToDoItem.packageIntent(data, nameString, urgency, descriptionString,
						fullDate);
				setResult(RESULT_OK, data);
				finish();
			}
		});
	}
	
	private void setDefaultDateTime() {
		mDate = new Date();
		mDate = new Date(mDate.getTime());

		Calendar cal = Calendar.getInstance();
		cal.setTime(mDate);

		setDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));

		mDateView.setText(dateString);

		setTimeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
				cal.get(Calendar.MILLISECOND));

		mTimeView.setText(timeString);
	}
	
	private static void setDateString(int year, int monthOfYear, int dayOfMonth) {
		monthOfYear++;
		String month = "" + monthOfYear;
		String day = "" + dayOfMonth;

		if (monthOfYear < 10)
			month = "0" + monthOfYear;
		if (dayOfMonth < 10)
			day = "0" + dayOfMonth;

		dateString = year + "/" + month + "/" + day;
	}

	private static void setTimeString(int hourOfDay, int minute, int mili) {
		String hour = "" + hourOfDay;
		String min = "" + minute;

		if (hourOfDay < 10)
			hour = "0" + hourOfDay;
		if (minute < 10)
			min = "0" + minute;

		timeString = hour + ":" + min + ":00";
	}
	
	private void showDatePicker() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void showTimePicker() {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}
	
	public static class DatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			setDateString(year, monthOfYear, dayOfMonth);
			mDateView.setText(dateString);
		}
		
	}
	
	public static class TimePickerFragment extends DialogFragment implements
	TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hour, minute, true);
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setTimeString(hourOfDay, minute, 0);
			mTimeView.setText(timeString);
		}
	}
	
	private String getName() {
		return mNameText.getText().toString();
	}
	
	private Urgency getUrgency() {

		switch (mUrgencyRadioGroup.getCheckedRadioButtonId()) {
		case R.id.urgencyLow: {
			return Urgency.LOW;
		}
		case R.id.urgencyMid: {
			return Urgency.MEDIUM;
		}
		default: {
			return Urgency.HIGH;
		}
		}
	}
	
	private String getDescription() {
		return mDescriptionText.getText().toString();
	}
	
}