package com.ablabs.myorganizer;

import java.util.ArrayList;
import java.util.Calendar;

import Data.CalendarManager;
import Data.Constants;
import Data.ContactsStruct;
import Data.DatabaseHelper;
import Data.ExportListAdapter;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class Export extends Activity {

	ArrayList<OrganizerStruct> arrlst;
	ArrayList<ContactsStruct> arrlstContacts;
	DatabaseHelper databaseHelper;
	Context cntxt;

	String[] strNames = { "Archana", "Dipti", "Abhijeet" };

	String[] strTo = { "archanaj512@gmail.com", "diptisuvarnkar@gmail.com",
			"abhijeet1289@gmail.com" };
	ExportListAdapter adapter;
	int iFilter = 1;
	Calendar calStart, calEnd;
	CalendarManager calMngr;
	TextView txtvwExportInfo;

	String strWhereClause, strExportDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.export);
			cntxt = this;
			databaseHelper = new DatabaseHelper(cntxt);
			calMngr = new CalendarManager();
			calStart = Calendar.getInstance();
			calEnd = Calendar.getInstance();
			strWhereClause = "where Date BETWEEN '"
					+ calMngr.formatDate(calStart, CalendarManager.strFormatDB)
					+ "' AND '"
					+ calMngr.formatDate(calEnd, CalendarManager.strFormatDB)
					+ "' AND Type=" + 2;

			txtvwExportInfo = (TextView) findViewById(R.id.txtvwExportInfo);

			txtvwExportInfo.setText(calMngr.formatDate(calStart,
					"EEEE, d MMMM yy")
					+ " TO "
					+ calMngr.formatDate(calEnd, "EEEE, d MMMM yy"));

			if (iFilter == 1) {
				txtvwExportInfo.setText(calMngr.formatDate(calStart,
						"EEEE, d MMMM yy"));
			}

			LinearLayout linlaySend = (LinearLayout) findViewById(R.id.linlaySend);
			linlaySend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ArrayList<String> strlstTo = Constants.arrListContacts;

					String[] namesArr = new String[strlstTo.size()];
					for (int i = 0; i < strlstTo.size(); i++) {
						namesArr[i] = strlstTo.get(i);
					}
					setExportDetails();
					sendMail(namesArr, strExportDetails);
				}
			});

			ImageView imgvViewHome = (ImageView) findViewById(R.id.imgvwHome);
			imgvViewHome.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});

			ImageView imgvwReminder = (ImageView) findViewById(R.id.imgvwReminder);
			imgvwReminder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
					startActivity(new Intent(cntxt, Reminders.class));
					overridePendingTransition(R.anim.slide_rtl, R.anim.hold);
				}
			});

			ImageView imgvwFilter = (ImageView) findViewById(R.id.imgvwFilter);
			imgvwFilter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showDialog();
				}
			});

			final LinearLayout linlayAddNew = (LinearLayout) findViewById(R.id.linlayAddContact);
			Button btnAddNew = (Button) findViewById(R.id.btnAddNewContact);
			btnAddNew.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					linlayAddNew.setVisibility(View.VISIBLE);
				}
			});

			final EditText edtName = (EditText) findViewById(R.id.edtName);
			final EditText edtEmail = (EditText) findViewById(R.id.edtEmail);

			Button btnSaveContact = (Button) findViewById(R.id.btnSaveContact);
			btnSaveContact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String strName = edtName.getText().toString().trim(), strEmail = edtEmail
							.getText().toString().trim();
					if (strName.length() > 0 && strEmail.length() > 0) {
						databaseHelper.insertContact(strName, strEmail);
						linlayAddNew.setVisibility(View.GONE);
						setAdapter();
					}

				}
			});

			Button btnCloseContact = (Button) findViewById(R.id.btnCloseContact);
			btnCloseContact.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					linlayAddNew.setVisibility(View.GONE);
				}
			});

			setAdapter();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setExportDetails() {
		switch (iFilter) {
		case 1:
			strExportDetails = calMngr.formatDate(calStart, "EEE, d MMM yyyy");
			break;

		case 2:
			strExportDetails = "\n\n FROM  "
					+ calMngr.formatDate(calStart, "d MMM yyyy") + "  TO  "
					+ calMngr.formatDate(calEnd, "d MMM yyyy");
			break;

		case 3:
			strExportDetails = "\n\n FROM  "
					+ calMngr.formatDate(calStart, "d MMM yyyy") + "  TO  "
					+ calMngr.formatDate(calEnd, "d MMM yyyy");
			break;

		case 4:
			strExportDetails = "\n\n FROM  "
					+ calMngr.formatDate(calStart, "d MMM yyyy") + "  TO  "
					+ calMngr.formatDate(calEnd, "d MMM yyyy");
			break;

		default:
			break;
		}

	}

	public void setAdapter() {
		try {
			//			arrlst = databaseHelper.fetchAllLogsByType(iIncome_expense);
			arrlstContacts = databaseHelper.fetchContacts();
			adapter = new ExportListAdapter(cntxt, arrlstContacts);

			final ParallaxListView lstvwOrganizer = (ParallaxListView) findViewById(R.id.lstvwExport);
			lstvwOrganizer.setAdapter(adapter);

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

					databaseHelper.deleteContact(iID);
					setAdapter();
					//                    adapter.notifyDataSetChanged();
					//                    adapter.notifyDataSetInvalidated();	
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

	public void showDialog() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final AlertDialog dialog = builder.create();
			dialog.setTitle(R.string.filterEntries);
			View view = LayoutInflater.from(cntxt).inflate(R.layout.filter,
					null);

			RadioButton rg1 = (RadioButton) view.findViewById(R.id.radioToday);
			rg1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked)
						iFilter = 1;
				}
			});

			RadioButton rg2 = (RadioButton) view
					.findViewById(R.id.radioThisWeek);
			rg2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked)
						iFilter = 2;
				}
			});

			RadioButton rg3 = (RadioButton) view
					.findViewById(R.id.radioThisMonth);
			rg3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked)
						iFilter = 3;
				}
			});

			RadioButton rg4 = (RadioButton) view
					.findViewById(R.id.radioThisYear);
			rg4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked)
						iFilter = 4;
				}
			});

			switch (iFilter) {
			case 1:
				rg1.setChecked(true);
				break;
			case 2:
				rg2.setChecked(true);
				break;
			case 3:
				rg3.setChecked(true);
				break;
			case 4:
				rg4.setChecked(true);
				break;
			default:
				break;
			}

			Button btnOk = (Button) view.findViewById(R.id.btn_add_entity);
			btnOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();
					filterData();
				}
			});

			Button btnCancel = (Button) view.findViewById(R.id.btn_add_cancel);
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
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

	public void filterData() {

		try {
			String strStartDate = null, strEndDate = null;

			txtvwExportInfo.setText(calMngr.formatDate(calStart,
					"EEE, d MMM yy")
					+ " TO "
					+ calMngr.formatDate(calEnd, "EEE, d MMM yy"));

			if (iFilter == 2) {
				int iDayOfWeek = calStart.get(Calendar.DAY_OF_WEEK);
				calStart.add(Calendar.DAY_OF_WEEK, -iDayOfWeek);
			}

			if (iFilter == 3) {
				int iDayOfWeek = calStart.get(Calendar.DAY_OF_WEEK);
				calStart.set(Calendar.DAY_OF_MONTH, 1);
			}
			if (iFilter == 4) {
				int iDayOfWeek = calStart.get(Calendar.DAY_OF_WEEK);
				calStart.set(Calendar.MONTH, Calendar.JANUARY);
				calStart.set(Calendar.DAY_OF_YEAR, 1);
			}

			else {
				txtvwExportInfo.setText(calMngr.formatDate(calStart,
						"EEEE, d MMMM yy"));
			}
			fillArraylist();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void fillArraylist() {
		strWhereClause = "where Date BETWEEN '"
				+ calMngr.formatDate(calStart, CalendarManager.strFormatDB)
				+ "' AND '"
				+ calMngr.formatDate(calEnd, CalendarManager.strFormatDB)
				+ "' AND Type=" + 2;
		arrlst = databaseHelper.fetchAllLogsByWhere(strWhereClause);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillArraylist();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	public void sendMail(String[] strTO, String strDuration) {
		try {

			String strText = "";

			for (int i = 0; i < arrlst.size(); i++) {
				strText += arrlst.get(i).getStrEntity() + " "
						+ arrlst.get(i).getStrName() + " - "
						+ arrlst.get(i).getStrAmount().replace(".0", "") + "\n";
			}

			//			String ImagePath = saveFile(btmap);
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("application/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, strTO);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Expenses of : " + strDuration);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strText);
			//			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + ImagePath));
			startActivity(Intent.createChooser(emailIntent, "Send Email"));
			// isEnabled = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		overridePendingTransition(R.anim.hold, R.anim.slide_ltr);
	}

}
