package com.ablabs.myorganizer;

import java.util.Calendar;
import java.util.Date;

import Data.CalendarManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;


public class SelectDateTime extends Activity
{
	DatePicker datePicker;
	TimePicker timePicker;
	TextView txtvwSelectedValue;
	Intent intent;

	static Context cntxt;
	DatePicker.OnDateChangedListener dateChangeListner;
	public static String keyDate = "Date", keyTime = "Time", keyResultIntent = "Calendar", keyPastDate = "PastDate";
	Calendar calSelection, calLastEntry;
	CalendarManager calMngr;
	String strDateFormat = "EEEE, dd MMM yyyy", strTimeFormat = "hh:mm a";
	String strSelDate = "", strSelTime = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.select_date_time);
			intent = getIntent();
			cntxt = this;
			calMngr = new CalendarManager();
			calLastEntry = Calendar.getInstance();
			calSelection = Calendar.getInstance();
			datePicker = (DatePicker) findViewById(R.id.xdatepicker);
			timePicker = (TimePicker) findViewById(R.id.xtimepicker);
			txtvwSelectedValue = (TextView) findViewById(R.id.xtxtvwSelectedValue1);

			if (intent.getExtras().containsKey(keyDate))
			{
				showDatePicker();
			}

			if (intent.getExtras().containsKey(keyTime))
			{
				showTimePicker();
			}

			if (intent.getExtras().containsKey(keyPastDate))
			{
				calLastEntry = calMngr.getCalendarFromString(intent.getExtras().getString(keyPastDate), "dd MM yyyy");
			}

			txtvwSelectedValue.setText(strSelDate + " " + strSelTime.trim());
			
			TextView txtvwBack=(TextView) findViewById(R.id.txtvwBack);
			txtvwBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});
			
			TextView txtvwDone=(TextView) findViewById(R.id.txtvwDone);
			txtvwDone.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					setBackDate();
					finish();
				}
			});
			

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void showDatePicker()
	{
		try
		{

			if (!intent.getExtras().getString(keyDate).equals(""))
			{
				calSelection = calMngr.getCalendarFromString(intent.getExtras().getString(keyDate), "dd MM yyyy");
			}

			datePicker.setVisibility(View.VISIBLE);

			strSelDate = calMngr.formatDate(calSelection, strDateFormat);

			txtvwSelectedValue.setText(strSelDate + " " + strSelTime.trim());

			dateChangeListner = new DatePicker.OnDateChangedListener()
			{

				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
				{
					calSelection.set(Calendar.MONTH, monthOfYear);
					calSelection.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					calSelection.set(Calendar.YEAR, year);

					calSelection.set(Calendar.HOUR_OF_DAY, calLastEntry.get(Calendar.HOUR_OF_DAY));
					calSelection.set(Calendar.MINUTE, calLastEntry.get(Calendar.MINUTE));
					calSelection.set(Calendar.SECOND, 0);

//					if (calLastEntry.after(calSelection))
//					{
//
//						calSelection.setTime(calLastEntry.getTime());
//
//						datePicker.init(calSelection.get(Calendar.YEAR), calSelection.get(Calendar.MONTH), calSelection.get(Calendar.DAY_OF_MONTH),
//								dateChangeListner);
//
//					}
					strSelDate = calMngr.formatDate(calSelection, strDateFormat);
					txtvwSelectedValue.setText(strSelDate + " " + strSelTime.trim());
				}
			};
			datePicker.init(calSelection.get(Calendar.YEAR), calSelection.get(Calendar.MONTH), calSelection.get(Calendar.DAY_OF_MONTH),
					dateChangeListner);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void showTimePicker()
	{

		try
		{
			timePicker.setVisibility(View.VISIBLE);
			String strTime = "";
			if (!intent.getExtras().getString(keyTime).equals(""))
			{

				strTime = intent.getExtras().getString(keyTime);

				Calendar calIntent = Calendar.getInstance();
				calIntent = calMngr.getCalendarFromString(strTime, "hh:mm a");
				calSelection.set(Calendar.HOUR_OF_DAY, calIntent.get(Calendar.HOUR_OF_DAY));
				calSelection.set(Calendar.MINUTE, calIntent.get(Calendar.MINUTE));

			}

			timePicker.setCurrentHour(calSelection.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calSelection.get(Calendar.MINUTE));

			strSelTime = calMngr.formatDate(calSelection, strTimeFormat);
			txtvwSelectedValue.setText(strSelDate + " " + strSelTime.trim());

			timePicker.setOnTimeChangedListener(new OnTimeChangedListener()
			{
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
				{
					try
					{
						calSelection.set(Calendar.HOUR_OF_DAY, hourOfDay);
						calSelection.set(Calendar.MINUTE, minute);

						Calendar nowCal = Calendar.getInstance();
						nowCal.set(Calendar.SECOND, 0);

						Date setdate = calSelection.getTime();
						Date today = nowCal.getTime();

						String strMinutes = "" + minute;
						if (strMinutes.length() == 1)
						{
							strMinutes = "0" + strMinutes;
						}

						if (hourOfDay == 0)
						{
							strSelTime = " 12:" + strMinutes + " AM";
						}
						else if (hourOfDay == 12)
						{
							strSelTime = " 12:" + strMinutes + " PM";
						}
						else if (hourOfDay < 12)
						{
							strSelTime = " " + hourOfDay + ":" + strMinutes + " AM";
						}
						else
							strSelTime = " " + (hourOfDay - 12) + ":" + strMinutes + " PM";

						txtvwSelectedValue.setText(strSelDate + " " + strSelTime.trim());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed()
	{
		setResult(RESULT_CANCELED);
		finish();
		
	}

	public boolean dateValidation(String startDate, String endDate)
	{
		Calendar calStartIn = null, calEndIn = null;

		if (!startDate.equals(""))
		{
			calStartIn = calMngr.getCalendarFromString(startDate, "dd MMMM yyyy");
			calStartIn.set(Calendar.HOUR_OF_DAY, 0);
			calStartIn.set(Calendar.MINUTE, 0);
			calStartIn.set(Calendar.SECOND, 0);
		}
		else
			return true;

		if (!endDate.equals(""))
		{
			calEndIn = calMngr.getCalendarFromString(endDate, "dd MMMM yyyy");
			calEndIn.set(Calendar.HOUR_OF_DAY, 0);
			calEndIn.set(Calendar.MINUTE, 0);
			calEndIn.set(Calendar.SECOND, 0);

			if (!startDate.equals(""))
			{
				if (!calStartIn.after(calEndIn))
				{
					return true;
				}
				else
				{
					return false;
				}
			}

		}
		else
		{
			return true;
		}

		if (!calEndIn.after(calStartIn))
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public String capitalizeFirstLetter(String strInput)
	{
		String strResult = strInput.toLowerCase();
		strResult = Character.toString(strInput.charAt(0)).toUpperCase() + strInput.substring(1);

		return strResult;
	}

	public String uppercaseMonth(String ddmmyyy)
	{
		String[] strArr = ddmmyyy.split(" ");
		String strLocal = "";
		for (int i = 0; i < strArr.length; i++)
		{
			strLocal += " " + capitalizeFirstLetter(strArr[i]);
		}

		return strLocal;
	}

	public void setBackDate()
	{
		Intent returnIntent = new Intent();
		returnIntent.putExtra(keyResultIntent, calSelection);
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
