package Data;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.ablabs.myorganizer.Export;
import com.ablabs.myorganizer.R;

public class ExportListAdapter extends BaseAdapter {

	Context context;
	LayoutInflater li;
	ArrayList<ContactsStruct> list;
	DatabaseHelper databaseHelper;
	
	public ExportListAdapter(Context context, ArrayList<ContactsStruct> list) {
		super();
		this.context = context;
		this.list = list;
		li = LayoutInflater.from(context);
		databaseHelper=new DatabaseHelper(context);

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
		if (convertView == null) {
			convertView = li.inflate(R.layout.list_item_export, null);
			holder = new viewHolder();
			holder.chkbxName = (CheckBox) convertView
					.findViewById(R.id.chkbxName);
			holder.imgvwDelete=(ImageView) convertView.findViewById(R.id.imgvwDeleteContact);

			convertView.setTag(holder);
		} else
			holder = (viewHolder) convertView.getTag();


		holder.chkbxName.setText(list.get(position).getStrName());
		holder.chkbxName.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					Constants.arrListContacts.add(list.get(position).getStrEmail());
				}
				else
				{
					if(Constants.arrListContacts.contains(list.get(position).getStrEmail()))
					{
						Constants.arrListContacts.remove(list.get(position).getStrEmail());
					}
				}
				
				String strContacts="";
				for (int i = 0; i < Constants.arrListContacts.size(); i++) {
					strContacts+=Constants.arrListContacts.get(i)+"\n";
				}
			}
		});
		holder.imgvwDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialogToDelete(list.get(position).getiID());
			}
		});
		
//		holder.chkbxName.setTag(list.get(position).getStrEmail());
		return convertView;
	}

	class viewHolder {

		CheckBox chkbxName;
		
		ImageView imgvwDelete;
	}

	
	public void showDialogToDelete(final int iID) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			final AlertDialog dialog = builder.create();
			
			dialog.setMessage("Are you sure you want to delete this contact?");
			
			//			dialog.setTitle(R.string.addEntity);
			View view = LayoutInflater.from(context).inflate(
					R.layout.contact_delete, null);

			Button btnEdit = (Button) view.findViewById(R.id.btn_OK);
			btnEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					databaseHelper.deleteContact(iID);
					
					Export objExport= new Export();
					objExport.setAdapter();
					dialog.dismiss();

				}
			});

			Button btnDelete = (Button) view.findViewById(R.id.btn_Cancel);
			btnDelete.setOnClickListener(new OnClickListener() {

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
	
}
