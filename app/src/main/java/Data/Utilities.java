package Data;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class Utilities {
	
	public static void overrideFonts(final Context context, final View v,
			String strFontName) {
		try {

			Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"fonts/" + strFontName);
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child,strFontName);
				}
			} else if (v instanceof TextView) {

				((TextView) v).setTypeface(tf);
			} else if (v instanceof EditText) {

				// ((EditText) v).setTypeface(appFont);
			}
		} catch (Exception e) {

		}
	}

}
