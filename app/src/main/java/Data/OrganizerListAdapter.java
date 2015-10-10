package Data;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ablabs.myorganizer.Home;
import com.ablabs.myorganizer.R;

public class OrganizerListAdapter extends BaseAdapter {

	CalendarManager calMngr;
	Context context;
	LayoutInflater li;
	ArrayList<OrganizerStruct> list;

	public OrganizerListAdapter(Context context, ArrayList<OrganizerStruct> list) {
		super();
		this.context = context;
		this.list = list;
		li = LayoutInflater.from(context);

		calMngr = new CalendarManager();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewHolder holder = null;
		LayoutInflater inflater;
		if (convertView == null) {
			convertView = li.inflate(R.layout.list_item, null);
			holder = new viewHolder();
			holder.txtvwName = (TextView) convertView
					.findViewById(R.id.txtvwName);
			holder.txtvwDetails = (TextView) convertView
					.findViewById(R.id.txtvwdetails);
			holder.txtvwAmount = (TextView) convertView
					.findViewById(R.id.txtvwAmount);
			holder.txtvwDate = (TextView) convertView
					.findViewById(R.id.txtvwDate);

			convertView.setTag(holder);
		} else
			holder = (viewHolder) convertView.getTag();

		String strName = list.get(position).getStrEntity();
		if (list.get(position).getStrName().toString().trim().length() > 0)
			strName += " - " + list.get(position).getStrName();

		holder.txtvwName.setText(strName);
		holder.txtvwDetails.setText(list.get(position).getStrDetails());
		holder.txtvwAmount.setText(list.get(position).getStrAmount());
		holder.txtvwDate.setText(calMngr.formatDate(calMngr
				.getCalendarFromString(list.get(position).getStrDate(),
						CalendarManager.strFormatDB), "dd MMMM yyyy"));
		
		
		Utilities.overrideFonts(context, convertView, "inglobal.ttf");
		return convertView;
	}

	class viewHolder {

		TextView txtvwName, txtvwDetails, txtvwAmount, txtvwDate;
	}

	
}
