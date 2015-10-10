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
import android.widget.ImageView;
import android.widget.TextView;

import com.ablabs.myorganizer.Home;
import com.ablabs.myorganizer.R;

public class ReminderListAdapter extends BaseAdapter {

	CalendarManager calMngr;
	Context context;
	LayoutInflater li;
	ArrayList<ReminderStruct> list;

	public ReminderListAdapter(Context context, ArrayList<ReminderStruct> list) {
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
			convertView = li.inflate(R.layout.list_item_reminders, null);
			holder = new viewHolder();
			holder.txtvwType = (TextView) convertView
					.findViewById(R.id.txtvwType);
			holder.txtvwSubject = (TextView) convertView
					.findViewById(R.id.txtvwSubject);
			holder.txtvwDate = (TextView) convertView
					.findViewById(R.id.txtvwDate);
			
			holder.imgvw=(ImageView) convertView.findViewById(R.id.imgvwReminderIcon);

			convertView.setTag(holder);
		} else
			holder = (viewHolder) convertView.getTag();

		String strName = list.get(position).getStrReminderType();

		holder.txtvwType.setText(strName);
		holder.txtvwSubject.setText(list.get(position).getStrSubject());
		holder.txtvwDate.setText(calMngr.formatDate(calMngr
				.getCalendarFromString(list.get(position).getStrDate(),
						CalendarManager.strFormatDB), "dd MMMM yyyy"));
		
		holder.imgvw.setImageResource(getIndexOfString(strName));

		return convertView;
	}
	
	public int getIndexOfString(String strType)
	{
		for (int i = 0; i < Constants.strTasks.length; i++) {
			if(Constants.strTasks[i].equalsIgnoreCase(strType)){
				return Constants.iTaskDrawables[i];
			}
		}
		return 0;
	}

	class viewHolder {

		TextView txtvwType, txtvwSubject, txtvwDate;
		
		ImageView imgvw;
	}

}
