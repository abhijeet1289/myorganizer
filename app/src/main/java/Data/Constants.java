package Data;

import java.util.ArrayList;

import com.ablabs.myorganizer.R;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Constants {

	public static Context cntxt;

	public static String[] strTasks = { "Task", "Meeting",
			"Doctor Appointment","Birthday","Anniversary","Tv Show"};
	
	public static int iCount=0;
	
	public static int[] iTaskDrawables={R.drawable.task,R.drawable.meeting,R.drawable.doctor,R.drawable.birthday,R.drawable.bouquet_48,R.drawable.television};
	
	public static ArrayList<String> arrListContacts=new ArrayList<String>();

	public Constants(Context cntxt) {
		super();
		this.cntxt = cntxt;
	}

	public void showToast(String str) {
		Toast toast = new Toast(cntxt);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.makeText(cntxt, str, Toast.LENGTH_LONG).show();
	}

}