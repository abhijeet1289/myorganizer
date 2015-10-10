package com.ablabs.myorganizer;

import java.util.ArrayList;
import java.util.Calendar;

import Data.CalendarManager;
import Data.Constants;
import Data.DatabaseHelper;
import Data.OrganizerStruct;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddReminder extends Activity {

	Context cntxt;
	ArrayAdapter<String> adapter;
	Spinner spinnerTask;
	ArrayList<String> arrlst, arrlstEntries;
	DatabaseHelper databaseHelper;
	int iType = 1;
	CalendarManager calMngr;
	Calendar calDate;
	AutoCompleteTextView edtxtSubject;
	EditText edtSelDate, edtSelTime;
	
	boolean bEditSave=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.add_reminder);
			cntxt = this;
			databaseHelper = new DatabaseHelper(cntxt);
			
			Intent intent=getIntent();
			

			calMngr = new CalendarManager();
			calDate = Calendar.getInstance();
			final OrganizerStruct objOrganizerStruct = new OrganizerStruct();

			edtxtSubject = (AutoCompleteTextView) findViewById(R.id.edtxtSubject);
			edtSelDate = (EditText) findViewById(R.id.edtDate);
			edtSelTime = (EditText) findViewById(R.id.edtTime);

			ArrayList<String> arrlstName = databaseHelper.fetchReminderSubjects();

			ArrayAdapter<String> adapterName = new ArrayAdapter<String>(cntxt,
					android.R.layout.simple_list_item_activated_1, arrlstName);
			edtxtSubject.setAdapter(adapterName);
			edtxtSubject.setThreshold(1);

			Button btnSave = (Button) findViewById(R.id.btnSave);
			btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (edtxtSubject.getText().toString().trim().length() > 0) {
						objOrganizerStruct.setStrName(edtxtSubject.getText()
								.toString().trim());

						databaseHelper.insertReminder(
								Constants.strTasks[spinnerTask
										.getSelectedItemPosition()],
								edtxtSubject.getText().toString().trim(),
								calMngr.formatDate(calDate, calMngr.strFormatDB
										+ " hh:mm:ss"));

						Constants.iCount = Integer.parseInt(databaseHelper
								.fetchLastReminderID());
						setAlarm();

						if (spinnerTask.getSelectedItem().toString()
								.equals("Birthday"))
							setBirthdayAlarm();

						setResult(RESULT_OK);
						finish();

					} else {
						Constants constants = new Constants(cntxt);
						constants.showToast("Please enter data");
					}
				}
			});
			Button btnClear = (Button) findViewById(R.id.btnClear);
			btnClear.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					edtxtSubject.setText("");
				}
			});

			spinnerTask = (Spinner) findViewById(R.id.spinnerTasks);

			try {

				setAdapterToSpinner();

				Button btnSelectCal = (Button) findViewById(R.id.btnSelDates);
				btnSelectCal.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(cntxt, SelectDateTime.class);
						intent.putExtra("Date",calMngr.formatDate(calDate, "dd MM yyyy"));
						startActivityForResult(intent, 11);

					}
				});

				Button btnSelectTime = (Button) findViewById(R.id.btnSelTime);
				btnSelectTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(cntxt, SelectDateTime.class);
						intent.putExtra("Time", calMngr.formatDate(calDate, "hh:mm a"));
						startActivityForResult(intent, 12);

					}
				});

				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//		overridePendingTransition(R.anim.slide_rtl,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Calendar calRes = (Calendar) data.getExtras().get("Calendar");
			//Date
			if (requestCode == 11) {
				calDate.set(Calendar.DAY_OF_YEAR,
						calRes.get(Calendar.DAY_OF_YEAR));
				calDate.set(Calendar.MONTH, calRes.get(Calendar.MONTH));
				calDate.set(Calendar.YEAR, calRes.get(Calendar.YEAR));

				edtSelDate.setText(calMngr.formatDate(calRes, "EEE, d MMM yy"));
			}

			if (requestCode == 12) {
				calDate.set(Calendar.HOUR_OF_DAY,
						calRes.get(Calendar.HOUR_OF_DAY));
				calDate.set(Calendar.MINUTE, calRes.get(Calendar.MINUTE));
				edtSelTime.setText(calMngr.formatDate(calRes, "hh:mm a"));
			}
			calDate.set(Calendar.SECOND, 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void setAdapterToSpinner()

	{
		try {
			adapter = new ArrayAdapter<String>(cntxt,
					android.R.layout.simple_list_item_1, Constants.strTasks);
			adapter.notifyDataSetChanged();
			spinnerTask.setAdapter(adapter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	private void setAlarm() {
		Context context = getApplicationContext();

		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmReciever.class);
		i.putExtra("Task",
				Constants.strTasks[spinnerTask.getSelectedItemPosition()]);
		i.putExtra("Subject", edtxtSubject.getText().toString().trim());
		i.putExtra("Drawable",
				Constants.iTaskDrawables[spinnerTask.getSelectedItemPosition()]);

		PendingIntent pi = PendingIntent.getBroadcast(context,
				Constants.iCount, i, 0);
		//		Calendar myCal = Calendar.getInstance();
		// myCal.setTimeInMillis(TIME_THE_ALARM_SHOULD_GO_OFF_AS_A_LONG);
		mgr.set(AlarmManager.RTC_WAKEUP, calDate.getTimeInMillis(), pi);
		Log.i("Alarm", "alarm set for " + calDate.getTime().toLocaleString());
//		Toast.makeText(getApplicationContext(),
//				"Alarm set for " + calDate.getTime().toLocaleString(),
//				Toast.LENGTH_LONG).show();

	}

	private void setBirthdayAlarm() {
		Context context = getApplicationContext();

		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmReciever.class);
		i.putExtra("Task",
				Constants.strTasks[spinnerTask.getSelectedItemPosition()]);
		i.putExtra("Subject", edtxtSubject.getText().toString().trim());
		i.putExtra("Drawable",
				Constants.iTaskDrawables[spinnerTask.getSelectedItemPosition()]);

		PendingIntent pi = PendingIntent.getBroadcast(context,
				Constants.iCount, i, 0);
		//		Calendar myCal = Calendar.getInstance();
		// myCal.setTimeInMillis(TIME_THE_ALARM_SHOULD_GO_OFF_AS_A_LONG);

		Calendar calNextCalendar = Calendar.getInstance();
		if(calNextCalendar.after(calDate))
		{
			calDate.add(Calendar.YEAR, 1);
		}
		
		calNextCalendar.add(Calendar.DAY_OF_YEAR, 1);
		mgr.setRepeating(AlarmManager.RTC_WAKEUP, calDate.getTimeInMillis(),
				(long) 3.156e+10, pi);
		//				mgr.setRepeating(iType, triggerAtMillis, intervalMillis, operation)
		Log.i("Alarm", "alarm set for " + calDate.getTime().toLocaleString());
//		Toast.makeText(getApplicationContext(),
//				"Alarm set for " + calDate.getTime().toLocaleString(),
//				Toast.LENGTH_LONG).show();

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.hold, R.anim.slide_ltr);
	}

}
