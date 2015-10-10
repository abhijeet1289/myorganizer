package com.ablabs.myorganizer;

import java.util.ArrayList;
import java.util.Calendar;

import Data.CalendarManager;
import Data.Constants;
import Data.DatabaseHelper;
import Data.OrganizerStruct;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

public class NewEntry extends Activity {

	Context cntxt;
	ArrayAdapter<String> adapter;
	Spinner spinner;
	ArrayList<String> arrlst, arrlstEntries;
	DatabaseHelper databaseHelper;
	int iType = 1;
	CalendarManager calMngr;
	Calendar calToday;
	AutoCompleteTextView edtxtName, edtxtAmount, edtxtDetails;
	EditText edtSelDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.new_entry);
			cntxt = this;
			databaseHelper = new DatabaseHelper(cntxt);

			calMngr = new CalendarManager();
			calToday = Calendar.getInstance();
			final OrganizerStruct objOrganizerStruct = new OrganizerStruct();

			edtxtName = (AutoCompleteTextView) findViewById(R.id.edtxtName);
			edtxtAmount = (AutoCompleteTextView) findViewById(R.id.edtxtAmount);
			edtxtDetails = (AutoCompleteTextView) findViewById(R.id.edtxtDetails);

			ArrayList<String> arrlstName = databaseHelper.fetchAllNames();
			ArrayList<String> arrlstDetails = databaseHelper.fetchAllDetails();

			ArrayAdapter<String> adapterName = new ArrayAdapter<String>(cntxt,
					android.R.layout.simple_list_item_activated_1, arrlstName);
			edtxtName.setAdapter(adapterName);
			edtxtName.setThreshold(1);

			ArrayAdapter<String> adapterDetails = new ArrayAdapter<String>(
					cntxt, android.R.layout.simple_list_item_1, arrlstDetails);
			edtxtDetails.setAdapter(adapterDetails);
			edtxtDetails.setThreshold(1);

			Button btnSave = (Button) findViewById(R.id.btnSave);
			btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (edtxtAmount.getText().toString().trim().length() > 0
							|| adapter == null) {
						objOrganizerStruct.setStrName(edtxtName.getText()
								.toString().trim());
						objOrganizerStruct.setStrAmount(edtxtAmount.getText()
								.toString().trim());
						objOrganizerStruct.setStrDetails(edtxtDetails.getText()
								.toString().trim());
						objOrganizerStruct.setStrAmount(edtxtAmount.getText()
								.toString().trim());
						objOrganizerStruct.setStrEntity(adapter.getItem(
								spinner.getSelectedItemPosition()).toString());
						objOrganizerStruct.setiType(iType);
						objOrganizerStruct.setStrDate(calMngr.formatDate(
								calToday, CalendarManager.strFormatDB));

						databaseHelper.insertOrganizerEntry(
								DatabaseHelper.tbl_Organizer,
								objOrganizerStruct);

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
					edtxtName.setText("");
					edtxtAmount.setText("");
					edtxtDetails.setText("");
				}
			});
			RadioButton rg1 = (RadioButton) findViewById(R.id.radio0);
			rg1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						iType = 1;
						setAdapterToSpinner();
					}

				}
			});

			RadioButton rg2 = (RadioButton) findViewById(R.id.radio1);
			rg2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						iType = 2;
						setAdapterToSpinner();
					}
				}
			});

			spinner = (Spinner) findViewById(R.id.spinner1);

			try {

				setAdapterToSpinner();

				ImageView imgvwAdd = (ImageView) findViewById(R.id.imgvwAdd);
				imgvwAdd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showDialog();
					}
				});

				Button btnSelectCal = (Button) findViewById(R.id.btnSelDates);
				edtSelDate = (EditText) findViewById(R.id.edtDate);
				btnSelectCal.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(cntxt, SelectDateTime.class);
						intent.putExtra("Date", calMngr.formatDate(calToday, "dd MM yyyy"));
						startActivityForResult(intent, 11);

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
			calToday.setTime(calRes.getTime());
			edtSelDate.setText(calMngr.formatDate(calRes, "EEE, d MMM yy"));
		}
	}

	public void showDialog() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final AlertDialog dialog = builder.create();
			dialog.setTitle(R.string.addEntity);
			View view = LayoutInflater.from(cntxt).inflate(R.layout.add_entity,
					null);

			final EditText edtxtEntity = (EditText) view
					.findViewById(R.id.edtxtEntity);

			Button btnOk = (Button) view.findViewById(R.id.btn_add_entity);
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Constants constants = new Constants(cntxt);
					if (edtxtEntity.getText().toString().trim().length() > 0) {

						String strEntityName = edtxtEntity.getText().toString()
								.trim();

						databaseHelper
								.insertEntityEntry(DatabaseHelper.tbl_Entity,
										strEntityName, iType);

						dialog.dismiss();

						setAdapterToSpinner();
						//				spinner.setSelection(spinner.getChildCount()-1);
						spinner.setSelection(adapter.getCount() - 1);
					} else {
						constants.showToast("Please enter entity");
					}
				}
			});

			Button btnCancel = (Button) view.findViewById(R.id.btn_add_cancel);
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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

	public void selectDateDialog() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final AlertDialog dialog = builder.create();
			dialog.setTitle(R.string.addEntity);
			View view = LayoutInflater.from(cntxt).inflate(R.layout.add_entity,
					null);

			final EditText edtxtEntity = (EditText) view
					.findViewById(R.id.edtxtEntity);

			Button btnOk = (Button) view.findViewById(R.id.btn_add_entity);
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Constants constants = new Constants(cntxt);
					if (edtxtEntity.getText().toString().trim().length() > 0) {

						String strEntityName = edtxtEntity.getText().toString()
								.trim();

						databaseHelper
								.insertEntityEntry(DatabaseHelper.tbl_Entity,
										strEntityName, iType);

						dialog.dismiss();

						setAdapterToSpinner();
						//				spinner.setSelection(spinner.getChildCount()-1);
						spinner.setSelection(adapter.getCount() - 1);
					} else {
						constants.showToast("Please enter entity");
					}
				}
			});

			Button btnCancel = (Button) view.findViewById(R.id.btn_add_cancel);
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void setAdapterToSpinner()

	{
		try {
			arrlstEntries = databaseHelper.fetchAllEntitiesByType(iType);
			adapter = new ArrayAdapter<String>(cntxt,
					android.R.layout.simple_list_item_1, arrlstEntries);
			adapter.notifyDataSetChanged();
			spinner.setAdapter(adapter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
