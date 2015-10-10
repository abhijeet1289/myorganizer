package com.ablabs.myorganizer;

import java.util.ArrayList;
import java.util.Calendar;

import Data.AppPrefManager;
import Data.CalendarManager;
import Data.DatabaseHelper;
import Data.OrganizerListAdapter;
import Data.OrganizerStruct;
import Data.Utilities;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener {

	DatabaseHelper databaseHelper;
	Context cntxt;
	int iIncome_expense = 1;

	LinearLayout linlayIncome, linlayExpense;
	TextView txtvwIncome, txtvwExpense, txtvwTotal;
	ArrayList<OrganizerStruct> arrlst;
	int iFilter = 1;
	OrganizerListAdapter adapter;

	AppPrefManager appPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.home);
			//setContentView(R.layout.list_multiple_parallax);

			cntxt = this;
			appPrefs = new AppPrefManager(cntxt);
			databaseHelper = new DatabaseHelper(cntxt);
			iFilter=appPrefs.getPrefs();

			//arrlst = databaseHelper.fetchAllLogsByType(2);
			//adapter = new OrganizerListAdapter(cntxt,
			//	arrlst);

			//ParallaxListView listView = (ParallaxListView) findViewById(R.id.list_view);
			//			CustomListAdapter adapter = new CustomListAdapter(LayoutInflater.from(this));
			//listView.setAdapter(adapter);

			ImageView imgvwAddEntry = (ImageView) findViewById(R.id.imgvwAddEntry);
			imgvwAddEntry.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(cntxt, NewEntry.class);

					startActivityForResult(intent, 11);
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

			linlayIncome = (LinearLayout) findViewById(R.id.linlayIncome);
			linlayIncome.setOnClickListener(this);

			linlayExpense = (LinearLayout) findViewById(R.id.linlayExpense);
			linlayExpense.setOnClickListener(this);

			txtvwIncome = (TextView) findViewById(R.id.txtvwIncomeName);
			txtvwExpense = (TextView) findViewById(R.id.txtvwExpenseName);

			txtvwTotal = (TextView) findViewById(R.id.txtvwTotal);
			
			Utilities.overrideFonts(cntxt, txtvwTotal, "inglobal.ttf");

			ImageView imgvwReminder = (ImageView) findViewById(R.id.imgvwReminder);
			imgvwReminder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(cntxt, Reminders.class));
					overridePendingTransition(R.anim.slide_rtl, R.anim.hold);
					
				}
			});

			ImageView imgvwExport = (ImageView) findViewById(R.id.imgvwExport);
			imgvwExport.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(cntxt, Export.class));
					overridePendingTransition(R.anim.slide_rtl, R.anim.hold);
					//					finish();
				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		overridePendingTransition(R.anim.hold, R.anim.slide_ltr);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		arrlst = databaseHelper.fetchAllLogsByType(iIncome_expense);
		setAdapter(iIncome_expense);
	}

	public void setAdapter(int iIncome_expense) {
		try {
			//			arrlst = databaseHelper.fetchAllLogsByType(iIncome_expense);
			adapter = new OrganizerListAdapter(cntxt, arrlst);

			final ParallaxListView lstvwOrganizer = (ParallaxListView) findViewById(R.id.listView1);
			lstvwOrganizer.setAdapter(adapter);

			lstvwOrganizer
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub

							showDialogToDelete(arrlst.get(arg2).getiId());

							return true;
						}
					});

			String strTotal = "";
			if (iIncome_expense == 1)
				strTotal = "Total Income: ";
			else
				strTotal = "Total Expenditure: ";

			strTotal += databaseHelper.fetchTotalByType(iIncome_expense);
			txtvwTotal.setText(strTotal);
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

	public void checkDBOperations() {

	}

	@Override
	public void onClick(View v) {
		try {
			// TODO Auto-generated method stub
			if (v == linlayIncome)

				iIncome_expense = 1;
			else if (v == linlayExpense)
				iIncome_expense = 2;

			if (iIncome_expense == 1) {
				linlayIncome.setBackgroundColor(getResources().getColor(
						R.color.inc_exp_selected));

				linlayExpense.setBackgroundColor(getResources().getColor(
						R.color.inc_exp_normal));
			}

			else {
				linlayIncome.setBackgroundColor(getResources().getColor(
						R.color.inc_exp_normal));
				linlayExpense.setBackgroundColor(getResources().getColor(
						R.color.inc_exp_selected));

			}
			arrlst = databaseHelper.fetchAllLogsByType(iIncome_expense);
			setAdapter(iIncome_expense);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void filterData() {

		try {
			String strStartDate = null, strEndDate = null;
			Calendar calStart, calEnd;

			calStart = Calendar.getInstance();
			calEnd = Calendar.getInstance();
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

			CalendarManager calMngr = new CalendarManager();
			String strWhereClause = "where Date BETWEEN '"
					+ calMngr.formatDate(calStart, CalendarManager.strFormatDB)
					+ "' AND '"
					+ calMngr.formatDate(calEnd, CalendarManager.strFormatDB)
					+ "' AND Type=" + iIncome_expense;

			arrlst = databaseHelper.fetchAllLogsByWhere(strWhereClause);
			setAdapter(iIncome_expense);
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
			
			int iSelected=appPrefs.getPrefs();

			RadioButton rg1 = (RadioButton) view.findViewById(R.id.radioToday);
			rg1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked){
						iFilter = 1;
						appPrefs.saveInPrefs(iFilter);
					}
				}
			});

			RadioButton rg2 = (RadioButton) view
					.findViewById(R.id.radioThisWeek);
			rg2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked){
						iFilter = 2;
						appPrefs.saveInPrefs(iFilter);
					}
				}
			});

			RadioButton rg3 = (RadioButton) view
					.findViewById(R.id.radioThisMonth);
			rg3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked){
						iFilter = 3;
						appPrefs.saveInPrefs(iFilter);
					}
				}
			});

			RadioButton rg4 = (RadioButton) view
					.findViewById(R.id.radioThisYear);
			rg4.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked){
						iFilter = 4;
						appPrefs.saveInPrefs(iFilter);
					}
				}
			});

			switch (iSelected) {
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

	public void sendMail(String strDuration) {
		try {

			String strText = "";

			for (int i = 0; i < arrlst.size(); i++) {
				strText += arrlst.get(i).getStrEntity() + " "
						+ arrlst.get(i).getStrName() + " - "
						+ arrlst.get(i).getStrAmount() + "\n";
			}

			//			String ImagePath = saveFile(btmap);
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("application/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "archanaj512@gmail.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Expenses of Month: " + strDuration);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strText);
			//			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + ImagePath));
			startActivity(Intent.createChooser(emailIntent, "Send Email"));
			// isEnabled = true;
		} catch (Exception e) {
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

					databaseHelper.DeleteLogEntry(iID);
					setAdapter(iIncome_expense);
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

}
