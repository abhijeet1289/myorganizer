package com.ablabs.myorganizer;

import java.util.ArrayList;
import java.util.Calendar;

import Data.CalendarManager;
import Data.Constants;
import Data.DatabaseHelper;
import Data.OrganizerListAdapter;
import Data.OrganizerStruct;
import Data.ReminderListAdapter;
import Data.ReminderStruct;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

public class Reminders extends Activity {

	Context cntxt;
	ReminderListAdapter adapter;
	Spinner spinner;
	ArrayList<ReminderStruct> arrlst, arrlstEntries;
	DatabaseHelper databaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.reminders);
			cntxt = this;
			databaseHelper = new DatabaseHelper(cntxt);


				ImageView imgvwAdd = (ImageView) findViewById(R.id.imgvwAddReminder);
				imgvwAdd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						
						startActivity(new Intent(cntxt, AddReminder.class));
						overridePendingTransition(R.anim.slide_rtl, R.anim.hold);
					}
				});
				
				
				ImageView imgvViewHome = (ImageView) findViewById(R.id.imgvwHome);
				imgvViewHome.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

				ImageView imgvwExport = (ImageView) findViewById(R.id.imgvwExport);
				imgvwExport.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						startActivity(new Intent(cntxt, Export.class));
						overridePendingTransition(R.anim.slide_rtl, R.anim.hold);
						//					finish();
					}
				});


			//		overridePendingTransition(R.anim.slide_rtl,0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		arrlst=databaseHelper.fetchReminders();
		setAdapter();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
//			Calendar calRes = (Calendar) data.getExtras().get("Calendar");
//			calToday.setTime(calRes.getTime());
//			edtSelDate.setText(calMngr.formatDate(calRes, "EEE, d MMM yy"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void setAdapter() {
		try {
			adapter = new ReminderListAdapter(cntxt, arrlst);

			final ParallaxListView lstvwOrganizer = (ParallaxListView) findViewById(R.id.lstvwReminders);
			lstvwOrganizer.setAdapter(adapter);
			lstvwOrganizer
			.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0,
						View arg1, int position, long arg3) {

					showDialogToDelete(arrlst.get(position).getiID());

					return true;
				}
			});


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showDialogToDelete(final int iID) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(cntxt);
			final AlertDialog dialog = builder.create();
			//			dialog.setTitle(R.string.addEntity);
			View view = LayoutInflater.from(cntxt).inflate(
					R.layout.entry_options, null);

			Button btnEdit = (Button) view.findViewById(R.id.btn_edit);
			btnEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();

				}
			});

			Button btnDelete = (Button) view.findViewById(R.id.btn_delete);
			btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					
					databaseHelper.deleteReminder(iID);
					CancelNotification(cntxt,iID);
					arrlst=databaseHelper.fetchReminders();
					setAdapter();
					dialog.dismiss();

				}
			});

			dialog.setView(view);
			dialog.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void CancelNotification(Context ctx, int notifyId) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nMgr = (NotificationManager) ctx
				.getSystemService(ns);
		nMgr.cancel(notifyId);
	}

}
