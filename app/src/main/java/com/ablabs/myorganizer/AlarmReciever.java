package com.ablabs.myorganizer;

import Data.Constants;
import Data.DatabaseHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReciever extends BroadcastReceiver {
	
	Context cntxt;
	Intent intent;
	int drawable;
	String strTask,strSubject;
	DatabaseHelper databasehelper;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		cntxt=context;
		this.intent=intent;
		
		strTask=intent.getExtras().getString("Task");
		strSubject=intent.getExtras().getString("Subject");
		
		drawable=intent.getExtras().getInt("Drawable");
		databasehelper=new DatabaseHelper(context);
		// here you can start an activity or service depending on your need
		// for ex you can start an activity to vibrate phone or to ring the
		// phone

		// String phoneNumberReciver="7798750268";// phone number to which SMS
		// to be send
		// String message="Hi I will be there later, See You soon";// message to
		// send
		// SmsManager sms = SmsManager.getDefault();
		// sms.sendTextMessage(phoneNumberReciver, null, message, null, null);
		// Show the toast like in above screen shot
//		Toast.makeText(context, "Alarm Triggered and SMS Sent",
//				Toast.LENGTH_LONG).show();
//
//		sendNotification(context, Reminders.class, "ba", "baba");
		
		Notify(context, strTask, strSubject);

	}

	private static final int NOTIFY_1 = 0x1001;

	public static void sendNotification(Context caller,
			Class<?> activityToLaunch, String title, String msg) {
		NotificationManager notifier = (NotificationManager) caller
				.getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notify = new Notification(R.drawable.ic_launcher,
				"", System.currentTimeMillis());
		notify.icon = R.drawable.ic_launcher;
		notify.tickerText = "New Alerts";
		notify.when = System.currentTimeMillis();
		notify.flags |= Notification.FLAG_AUTO_CANCEL;

		Intent toLaunch = new Intent(caller, activityToLaunch);
		PendingIntent intentBack = PendingIntent.getActivity(caller, 0,
				toLaunch, 0);
		notify.setLatestEventInfo(caller, title, msg, intentBack);
		notifier.notify(NOTIFY_1, notify);

		notifier.cancel(NOTIFY_1);
	}
	
	
	
	public void Notify(Context caller,String notificationTitle, String notificationMessage) {
		  NotificationManager notificationManager = (NotificationManager) caller.getSystemService(Context.NOTIFICATION_SERVICE);
		  @SuppressWarnings("deprecation")
		  Notification notification = new Notification(drawable,
		    strTask, System.currentTimeMillis());
		  notification.iconLevel=5;
		  
		   Intent notificationIntent = new Intent(caller, Reminders.class);
		  PendingIntent pendingIntent = PendingIntent.getActivity(caller, 0,
		    notificationIntent, 0);

		   notification.setLatestEventInfo(caller, notificationTitle,
		    notificationMessage, pendingIntent);
		  notificationManager.notify(Constants.iCount, notification);
//		  notificationManager.cancel(Constants.iCount);
		 }

}