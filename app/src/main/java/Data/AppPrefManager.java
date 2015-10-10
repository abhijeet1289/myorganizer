package Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class AppPrefManager {
	
	Context cntxt;
	
	public void saveInPrefs(int iFilter)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(cntxt);
		  SharedPreferences.Editor editor = preferences.edit();
		  editor.putInt("Filter",iFilter);
		  editor.apply();
	}
	
	public int getPrefs()
	{
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(cntxt);
		  int name = preferences.getInt("Filter",0);
		  return name;
	}

	public AppPrefManager(Context cntxt) {
		super();
		this.cntxt = cntxt;
	}

}
