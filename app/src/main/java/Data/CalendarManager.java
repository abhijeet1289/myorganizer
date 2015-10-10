package Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.text.format.DateFormat;

public class CalendarManager
{

	public static String strFormatDB = "yyy-MM-dd";

	/**
	 * GET DATE AND FORMAT AND RETURNS CALENDAR OBJECT
	 * 
	 * @param strDate
	 * @param strFormat
	 * @return
	 */
	public Calendar getCalendarFromString(String strDate, String strFormat)
	{

		SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.getDefault());

		Calendar cal = Calendar.getInstance();

		try
		{
			cal.setTime(sdf.parse(strDate));
			if (cal.HOUR == 12 || cal.HOUR < 8)
				cal.add(Calendar.HOUR, 12);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if (cal.HOUR_OF_DAY > 7 && cal.HOUR_OF_DAY <= 12)
		// cal.set(Calendar.AM_PM, Calendar.AM);
		// else
		// cal.set(Calendar.AM_PM, Calendar.PM);
		return cal;
	}

	/**
	 * GETS CALENDAR OBJECT AND FORMAT AND RETURNS FORMATTED STRING
	 * 
	 * @param cal
	 * @param strDateFormat
	 * @return
	 */
	public String formatDate(Calendar cal, String strDateFormat)
	{
		return (String) DateFormat.format(strDateFormat, cal);
	}

	public int DateDifference(String strDate1, String strDate2,String strFormat)
	{
		int iRes = 0;
		Calendar cal1 = getCalendarFromString(strDate1, strFormat);
		Calendar cal2 = getCalendarFromString(strDate2, strFormat);
		setZeroMinutes(cal1);
		setZeroMinutes(cal2);

		if (cal1.getTime().toString().equalsIgnoreCase(cal2.getTime().toString()))
		{
			iRes = 0;
		}
		else if (cal2.before(cal1))
		{
			iRes = (int) ((cal1.getTime().getTime() - cal2.getTime().getTime()) / (1000 * 60 * 60 * 24));
		}
		else
			iRes = (int) ((cal2.getTime().getTime() - cal1.getTime().getTime()) / (1000 * 60 * 60 * 24));

		return iRes;
	}

	public int DateDifference(Calendar cal1, Calendar cal2)
	{
		int iRes = 0;
		setZeroMinutes(cal1);
		setZeroMinutes(cal2);

		if (cal1.getTime().toString().equalsIgnoreCase(cal2.getTime().toString()))
		{
			iRes = 0;
		}
		else if (cal2.before(cal1))
		{
			iRes = (int) ((cal1.getTime().getTime() - cal2.getTime().getTime()) / (1000 * 60 * 60 * 24));

		}
		else
			iRes = (int) ((cal2.getTime().getTime() - cal1.getTime().getTime()) / (1000 * 60 * 60 * 24));

		return iRes;
	}

	public void setZeroMinutes(Calendar cal)
	{
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
	}
}
