package Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author AbhijeetJ
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	// Used To Print LOG messages
	public final String TAG = "DatabaseHelper";

	// App Database Name
	private final static String DB_NAME = "Organizer.sqlite";

	// Stores DB path
	private String DatabasePath;

	// Stores Context
	private final Context myContext;

	/**
	 * Table Names
	 */
	// private final static String tbl_Awards = "Awards";
	// private final static String tbl_Bpdiary = "BPDiary";

	public final static String tbl_Organizer = "Organizer";
	public final static String tbl_Entity = "Entity";
	public final static String tbl_Reminders = "Reminders";
	public final static String tbl_Contacts = "Contacts";

	SQLiteDatabase checkDB = null;
	String strPACKAGENAME = "com.ablabs.myorganizer";
	public String strDB_path = "/data/data/" + strPACKAGENAME + "/databases/";
	private String strmyPath = strDB_path + DB_NAME;

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public DatabaseHelper(Context mContext) {
		super(mContext, DB_NAME, null, 1);
		this.myContext = mContext;
		// Logger.d(TAG, "PATH" + myContext.getDatabasePath(DB_NAME));
		DatabasePath = myContext.getDatabasePath(DB_NAME).toString();

		try {
			// Create Database if not Created
			createDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to create the database inside application
	 * 
	 * @throws IOException
	 */
	public void createDatabase() throws IOException {
		try {
			// check if the database exists
			boolean dbExist = checkDatabase();
			if (!dbExist) {
				// database is not present copy database
				this.getReadableDatabase();
				try {
					copyDatabase();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDatabase() {

		try {
			checkDB = null;
			try {
				File file = new File(DatabasePath);
				if (file.exists()) {
					checkDB = SQLiteDatabase.openDatabase(DatabasePath, null,
							SQLiteDatabase.OPEN_READONLY);
				} else {
					return false;
				}
			} catch (SQLException e) {
				Log.d("DB Exception", "");
			}
			if (checkDB != null) {
				checkDB.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by tranfering bytestream.
	 * */
	private void copyDatabase() throws IOException {
		try {
			// Open your local db as the input stream
			InputStream myInput = myContext.getAssets().open(DB_NAME);
			// Path to the just created empty db
			String outFileName = DatabasePath;
			// Open the empty db as the output stream

			OutputStream myOutput = new FileOutputStream(outFileName);
			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024 * 2];
			int length;

			while ((length = myInput.read(buffer)) > 0) {
				try {
					myOutput.write(buffer, 0, length);
				} catch (Exception e) {
				}
			}
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertOrganizerEntry(String strTable,
			OrganizerStruct objOrganizerStruct) {
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("Name", objOrganizerStruct.getStrName());
			cv.put("Amount", objOrganizerStruct.getStrAmount());

			cv.put("Details", objOrganizerStruct.getStrDetails());
			cv.put("Entity", objOrganizerStruct.getStrEntity());
			cv.put("Type", objOrganizerStruct.getiType());
			cv.put("Date", objOrganizerStruct.getStrDate());

			long no_row = 0;

			no_row = myDatabase.update(strTable, cv,
					"Date=? and Amount=? and Entity=?",
					new String[] { objOrganizerStruct.getStrDate(),
							objOrganizerStruct.getStrAmount(),
							"" + objOrganizerStruct.getStrEntity() });

			if (no_row == 0)
				no_row = myDatabase.insert(strTable, null, cv);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "Insertion/Updation Error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	public void insertEntityEntry(String strTable, String strEntityName,
			int iType) {
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("EntityName", strEntityName);
			cv.put("Type", iType);

			long no_row = 0;

			no_row = myDatabase.update(strTable, cv, "EntityName=? and Type=?",
					new String[] { strEntityName, "" + iType });

			if (no_row == 0)
				no_row = myDatabase.insert(strTable, null, cv);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Insertion/Updation Error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	public ArrayList<OrganizerStruct> fetchAllLogsByType(int iType) {
		// iCycleNo=1;
		ArrayList<OrganizerStruct> arrList = new ArrayList<OrganizerStruct>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Organizer + " where Type="
					+ iType + " ORDER BY Date desc";
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						OrganizerStruct objOrganizerStruct = new OrganizerStruct();
						objOrganizerStruct.setiId(cursor.getInt(0));
						objOrganizerStruct.setStrName(cursor.getString(1));
						objOrganizerStruct
								.setStrAmount("" + cursor.getFloat(2));
						objOrganizerStruct.setStrDetails(cursor.getString(3));
						objOrganizerStruct.setStrEntity(cursor.getString(4));
						objOrganizerStruct.setiType(cursor.getInt(5));
						objOrganizerStruct.setStrDate(cursor.getString(6));

						arrList.add(objOrganizerStruct);
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}

	public ArrayList<OrganizerStruct> fetchAllLogsByWhere(String strWhereClause) {
		// iCycleNo=1;
		ArrayList<OrganizerStruct> arrList = new ArrayList<OrganizerStruct>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Organizer + " "
					+ strWhereClause + " ORDER BY Date desc";
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						OrganizerStruct objOrganizerStruct = new OrganizerStruct();
						objOrganizerStruct.setiId(cursor.getInt(0));
						objOrganizerStruct.setStrName(cursor.getString(1));
						objOrganizerStruct
								.setStrAmount("" + cursor.getFloat(2));
						objOrganizerStruct.setStrDetails(cursor.getString(3));
						objOrganizerStruct.setStrEntity(cursor.getString(4));
						objOrganizerStruct.setiType(cursor.getInt(5));
						objOrganizerStruct.setStrDate(cursor.getString(6));

						arrList.add(objOrganizerStruct);
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}

	public ArrayList<String> fetchAllNames() {
		// iCycleNo=1;
		ArrayList<String> arrList = new ArrayList<String>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT Name FROM " + tbl_Organizer;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						arrList.add(cursor.getString(0));
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}

	public ArrayList<String> fetchAllDetails() {
		// iCycleNo=1;
		ArrayList<String> arrList = new ArrayList<String>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT Details FROM " + tbl_Organizer;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						arrList.add(cursor.getString(0));
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}
	
	
	public ArrayList<String> fetchReminderSubjects() {
		// iCycleNo=1;
		ArrayList<String> arrList = new ArrayList<String>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT Subject FROM " + tbl_Reminders;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						arrList.add(cursor.getString(0));
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}
	
	

	public ArrayList<String> fetchAllEntities() {
		// iCycleNo=1;
		ArrayList<String> arrList = new ArrayList<String>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Entity;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {

						arrList.add(cursor.getString(1));
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}

	public ArrayList<String> fetchAllEntitiesByType(int iType) {
		// iCycleNo=1;
		ArrayList<String> arrList = new ArrayList<String>();
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Entity + " where Type= "
					+ iType;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {

						arrList.add(cursor.getString(1));
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrList;
	}

	public String fetchTotalByType(int iType) {
		// iCycleNo=1;
		String strList = "";
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT SUM(Amount) FROM " + tbl_Organizer
					+ " where Type= " + iType;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					//					do {

					strList = cursor.getString(0);
					//					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return strList;
	}

	public boolean DeleteLogEntry(int id) {
		try {
			checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkDB
				.delete(tbl_Organizer, "ID = ?", new String[] { "" + id }) > 0;
	}

	public void insertReminder(String strType, String strSubject, String strDate) {
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("Type", strType);
			cv.put("Subject", strSubject);
			cv.put("Date", strDate);

			long no_row = 0;

			no_row = myDatabase.update(tbl_Reminders, cv,
					"Type=? and Subject=? and Date=?", new String[] { strType,
							strSubject, "" + strDate });

			if (no_row == 0)
				no_row = myDatabase.insert(tbl_Reminders, null, cv);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Insertion/Updation Error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	public String fetchLastReminderID() {
		// iCycleNo=1;
		String strList = "";
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT Max(ID) FROM " + tbl_Reminders;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					//					do {

					strList = cursor.getString(0);
					//					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return strList;
	}

	public ArrayList<ReminderStruct> fetchReminders() {
		// iCycleNo=1;
		ArrayList<ReminderStruct> arrlstReminders = new ArrayList<ReminderStruct>();
		String strList = "";
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Reminders
					+ " order by Date desc";
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						ReminderStruct objReminder = new ReminderStruct();
						objReminder.setiID(cursor.getInt(0));
						objReminder.setStrReminderType(cursor.getString(1));
						objReminder.setStrSubject(cursor.getString(2));
						objReminder.setStrDate(cursor.getString(3));

						arrlstReminders.add(objReminder);
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrlstReminders;
	}
	
	public boolean deleteReminder(int id) {
		try {
			checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkDB
				.delete(tbl_Reminders, "ID = ?", new String[] { "" + id }) > 0;
	}
	
	
	
	public void insertContact(String strName, String strEmail) {
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("Name", strName);
			cv.put("Email", strEmail);

			long no_row = 0;

			no_row = myDatabase.update(tbl_Contacts, cv,
					"Name=? and Email=?", new String[] { strName,
					strEmail});

			if (no_row == 0)
				no_row = myDatabase.insert(tbl_Contacts, null, cv);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "Insertion/Updation Error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}
	
	public ArrayList<ContactsStruct> fetchContacts() {
		// iCycleNo=1;
		ArrayList<ContactsStruct> arrlstReminders = new ArrayList<ContactsStruct>();
		String strList = "";
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {
			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Contacts
					+ " order by Name asc";
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						ContactsStruct objContacts = new ContactsStruct();
						objContacts.setiID(cursor.getInt(0));
						objContacts.setStrName(cursor.getString(1));
						objContacts.setStrEmail(cursor.getString(2));

						arrlstReminders.add(objContacts);
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}
		return arrlstReminders;
	}
	
	public boolean deleteContact(int iID) {
		try {
			checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkDB
				.delete(tbl_Contacts, "ID = ?", new String[] { ""+iID }) > 0;
	}
	

	/*public void insertDailyPlan(String strTable, String strDate, int iLogged) {

		SQLiteDatabase myDatabase = null;

		try {
			myDatabase = this.getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("LogDate", strDate);
			cv.put("Logged", iLogged);

			long no_row = 0;

			// Checks whether Record to Insert/Update
			// if (myDetails.getiId() == 0) {
			// Insert New Record.

			no_row = myDatabase.update(strTable, cv, "LogDate=?",
					new String[] { strDate });

			if (no_row == 0)
				no_row = myDatabase.insert(strTable, null, cv);
			// } else {
			// // Update Record
			// no_row = myDatabase.update(tbl_LifestyleCartia, cv,
			// COL_USER_DETAILS_ID + " = ?", new String[] { ""
			// + myDetails.getiId() });
			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "Insertion/Updation Error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	public void updateReminder(int iReminderID, String strTime) {

		SQLiteDatabase myDatabase = null;
		ContentValues cv = new ContentValues();
		try {
			myDatabase = this.getWritableDatabase();

			cv.put("Time", strTime);

			long no_row = myDatabase.update(tbl_Reminders, cv, "ID = ?",
					new String[] { "" + iReminderID });

			Log.i(TAG, "Updation Done");
			// Utilities.showToast(no_row + " Update done");
			// }
		} catch (Exception e) {

			e.printStackTrace();
			Log.i(TAG, "Updation Error");
			// Utilities.showToast("Update error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	public void updateReminderStatus(int iReminderID, int iStatus) {

		SQLiteDatabase myDatabase = null;
		ContentValues cv = new ContentValues();
		try {
			myDatabase = this.getWritableDatabase();

			cv.put("Status", iStatus);

			long no_row = myDatabase.update(tbl_Reminders, cv, "ID = ?",
					new String[] { "" + iReminderID });

			Log.i(TAG, "Updation Done");
			// Utilities.showToast(no_row + " Update done");
			// }
		} catch (Exception e) {

			e.printStackTrace();
			Log.i(TAG, "Updation Error");
			// Utilities.showToast("Update error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	public String[] fetchReminders() {
		String[] strArr = new String[2];
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {

			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Reminders;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();

			int iCounter = 0;
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						strArr[iCounter] = cursor.getString(2);
						iCounter++;
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}

		return strArr;
	}

	public int[] fetchRemindersStatus() {
		int[] strArr = new int[2];
		Cursor cursor = null;
		SQLiteDatabase myDatabase = null;
		try {

			myDatabase = this.getReadableDatabase();
			String querry = "SELECT * FROM " + tbl_Reminders;
			cursor = myDatabase.rawQuery(querry, null);
			int count = cursor.getCount();

			int iCounter = 0;
			if (count > 0) {
				if (cursor.moveToFirst()) {
					do {
						strArr[iCounter] = cursor.getInt(3);
						iCounter++;
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			if (myDatabase != null) {
				myDatabase.close();
			}
		}

		return strArr;
	}

	public void clearActivitiesPerDay() {

		SQLiteDatabase myDatabase = null;

		try {
			myDatabase = this.getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("ActivityDate", "");
			cv.put("ActivitySelect", "");

			long no_row = 0;

			// Checks whether Record to Insert/Update
			// if (myDetails.getiId() == 0) {
			// Insert New Record.

			no_row = myDatabase.update(tbl_DailyActivities, cv, null, null);

			// } else {
			// // Update Record
			// no_row = myDatabase.update(tbl_LifestyleCartia, cv,
			// COL_USER_DETAILS_ID + " = ?", new String[] { ""
			// + myDetails.getiId() });
			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "Insertion/Updation Error");
		} finally {
			if (myDatabase != null) {
				myDatabase.close();
			}
		}
	}

	*//**
		* Insert data for BPDiary
		* 
		* @param strDate
		* @param iLogged
		*/
	/*

	public int getAwardID(int PlanID) {

	int awardID = 0;
	SQLiteDatabase myDatabase = null;
	try {
		myDatabase = this.getWritableDatabase();
		String querry = "";
		querry = "SELECT max(Award) FROM Awards where PlanID='" + PlanID
				+ "'";

		Cursor cursor = myDatabase.rawQuery(querry, null);

		int count = cursor.getCount();

		// if (count > 0) {
		// querry = "SELECT * FROM Awards";

		if (cursor.moveToFirst()) {
			do {
				awardID = Integer.parseInt(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		// }
		return awardID;

	} catch (Exception e) {
		Log.e("Error", e.getMessage());

	} finally {
		if (myDatabase != null) {
			myDatabase.close();
		}
	}

	// TODO Auto-generated method stub
	return 0;
	}

	*//**
		* Method to Read all subevent types already exist in database
		*/
	/*
	public ArrayList<String> readsubeventtypes(String event) {
	ArrayList<String> result = new ArrayList<String>();
	try {
		checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
				SQLiteDatabase.OPEN_READONLY);
		String selectQuerry = "SELECT EventSubType FROM EventTypes where EventType='"
				+ event + "'";
		Cursor cursor = checkDB.rawQuery(selectQuerry, null);
		if (cursor.moveToFirst()) {
			do {
				result.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		checkDB.close();
	}
	return result;
	}

	*//**
		* Method to Read all subevent types already exist in database
		*/
	/*
	public ArrayList<String> readdates() {
	ArrayList<String> result = new ArrayList<String>();
	try {
		checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
				SQLiteDatabase.OPEN_READONLY);
		String selectQuerry = "SELECT Date FROM Event";
		Cursor cursor = checkDB.rawQuery(selectQuerry, null);
		if (cursor.moveToFirst()) {
			do {
				result.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		checkDB.close();
	}
	return result;
	}

	*//**
		* get events for one date
		* 
		* */
	/*
	public ArrayList<String> getEvents(String date, String col) {
	ArrayList<String> result = new ArrayList<String>();
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READONLY);
	String selectQuerry = "SELECT " + col + " FROM Event Where Date='"
			+ date + "'";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.moveToFirst()) {
		do {
			result.add(cursor.getString(0));
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	return result;
	}

	*//**
		* Method to check availablity of same sub event
		* 
		* */
	/*
	private boolean isEntryExistsevent(String mainevent, String subevent) {
	boolean isExist = false;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	String selectQuerry = "SELECT EventSubType FROM EventTypes WHERE EventSubType = '"
			+ subevent + "' AND EventType= '" + mainevent + "'";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.getCount() > 0) {
		isExist = true;
	}
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	return isExist;
	}

	*//**
		* Method to update already existing event entry
		* */
	/*
	private void updateEntryeventdetails(String mainevent, String subevent,
	String Clinic_contact, String loc, String date, String time,
	String remtype, String notes, String prevdate, String eventid) {
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	ContentValues cv = new ContentValues();
	cv.put("EventType", mainevent);
	cv.put("EventSubType", subevent);
	cv.put("Contact", Clinic_contact);
	cv.put("Location", loc);
	cv.put("Date", date);
	cv.put("ReminderType", remtype);
	cv.put("Notes", notes);
	cv.put("Time", time);
	// cv.put("Event_id", eventid);
	// cv.put("Doc_name", name);

	String table = "Event";
	// appDatabase.update(table, cv,
	// "CarerName = '"+name+"'"+"AND "+"CarerPhNo ='" + contact +
	// "'"+"AND "+"CarerAddress='"+address+"'",null);//,Day_type = ?
	checkDB.update(table, cv, "ID=?", new String[] { eventid });// ,remtype,notes,time});
	// and Contact=? and Location=? and ReminderType=? and Notes=? and
	// Time=?
	// appDatabase.update(table, cv,"Dateadded = ?" and column=?,new
	// String[]{strDateAdded,string2});
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	*//**
		* Method to insert records if not exist already in database
		* */
	/*

	private void insertEntryevent(String mainevent, String subevent) {
	long id = -1;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);

	SQLiteStatement stmt = checkDB
			.compileStatement("INSERT INTO EventTypes(EventType, EventSubType) VALUES(?,?)");

	stmt.bindString(1, mainevent);
	stmt.bindString(2, subevent);
	id = stmt.executeInsert();
	long temp = id;
	} catch (SQLException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	*//**
		* Method to insert new subevent in database
		* 
		* */
	/*
	public void insertEntryForEvent(String mainevent, String subevent) {
	try {
	if (isEntryExistsevent(mainevent, subevent)) {
		updateEntryevent(mainevent, subevent);
	} else {
		insertEntryevent(mainevent, subevent);
	}
	} catch (SQLException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	*//**
		* Method to update already existing event details entry
		* */
	/*
	private void updateEntryevent(String mainevent, String subevent) {
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	ContentValues cv = new ContentValues();
	cv.put("EventType", mainevent);
	cv.put("EventSubType", subevent);
	String table = "EventTypes";
	// appDatabase.update(table, cv,
	// "CarerName = '"+name+"'"+"AND "+"CarerPhNo ='" + contact +
	// "'"+"AND "+"CarerAddress='"+address+"'",null);//,Day_type = ?
	checkDB.update(table, cv, "EventType=? and EventSubType=?",
			new String[] { mainevent, subevent });
	// appDatabase.update(table, cv,"Dateadded = ?" and column=?,new
	// String[]{strDateAdded,string2});
	} catch (Exception e) {
	e.printStackTrace();
	}
	}

	*//**
		* Method to check availability of same sub event
		* 
		* */
	/*
	private boolean isEntryExistseventdetails(String date, String subtype,
	String eventid) {
	boolean isExist = false;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	String selectQuerry = "SELECT ID FROM Event WHERE ID = '" + eventid
			+ "'" + " AND EventSubType= '" + subtype + "' AND Date = '"
			+ date + "'";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.getCount() > 0) {
		isExist = true;
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return isExist;
	}

	*//**
		* Method to insert records if not exist already in database
		* */
	/*

	private void insertEntryeventdetails(String mainevent, String subevent,
	String Clinic_contact, String loc, String date, String time,
	String remtype, String notes) {
	long id = -1;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);

	SQLiteStatement stmt = checkDB
			.compileStatement("INSERT INTO Event(EventType, EventSubType,Contact,Location,Date,ReminderType,Notes,Time,Event_id) VALUES(?,?,?,?,?,?,?,?,?)");

	stmt.bindString(1, mainevent);
	stmt.bindString(2, subevent);
	stmt.bindString(3, Clinic_contact);
	stmt.bindString(4, loc);
	stmt.bindString(5, date);
	stmt.bindString(6, remtype);
	stmt.bindString(7, notes);
	stmt.bindString(8, time);
	// stmt.bindString(9, eventid);
	id = stmt.executeInsert();
	long temp = id;
	} catch (SQLException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	*//**
		* Method to insert new all event details in database
		* 
		* */
	/*
	public void insertEntryEventdetails(String mainevent, String subevent,
	String Clinic_contact, String loc, String date, String time,
	String remtype, String notes, String eventid, String dateprev) {
	try {
	if (!eventid.equals(""))
		// if (isEntryExistseventdetails(dateprev, subevent, eventid))
		// {
		// updateEntryeventdetails(mainevent, subevent, Clinic_contact,
		// loc, date, time, remtype, notes, dateprev,eventid);
		updateNewEventWithID(mainevent, subevent, Clinic_contact, loc,
				date, time, remtype, notes, dateprev, eventid);
	// }
	else {
		insertEntryeventdetails(mainevent, subevent, Clinic_contact,
				loc, date, time, remtype, notes);
	}
	} catch (SQLException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	*//**
		* Method to check availability of same sub event
		* 
		* */
	/*
	public boolean isEntryExistscontact(int ID, String Contacttype,
	String oldcontacttype) {
	boolean isExist = false;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	String selectQuerry = "SELECT ID FROM Contacts WHERE ContactType = '"
			+ oldcontacttype + "'" + " AND ID= " + ID;
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.getCount() > 0) {
		isExist = true;
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return isExist;
	}

	*//**
		* Method to update already existing event entry
		* */
	/*
	private void updateEntryContacts(String contacttype, String name,
	String speclty, String phon, String mob, String fax, String email,
	String address, String notes, String oldcontacttype, int ID) {
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	ContentValues cv = new ContentValues();
	cv.put("ContactType", contacttype);
	cv.put("Name", name);
	cv.put("Speciality", speclty);
	cv.put("Phone", phon);
	cv.put("Mobile", mob);
	cv.put("Fax", fax);
	cv.put("Email", email);
	cv.put("Address", address);
	cv.put("Notes", notes);
	// cv.put("Doc_name", name);

	String table = "Contacts";
	checkDB.update(table, cv, "ContactType = '" + oldcontacttype + "'"
			+ "AND " + "ID =" + ID, null);// +
											// "'"+"AND "+"CarerAddress='"+address+"'",null);//,Day_type
											// = ?
	
	 * checkDB.update(table, cv, "ContactType=? and ID=?", new String[]
	 * { oldcontacttype, ID });
	 // ,remtype,notes,time});
		// and Contact=? and Location=? and ReminderType=? and Notes=?
		// and Time=?
		// appDatabase.update(table, cv,"Dateadded = ?" and column=?,new
		// String[]{strDateAdded,string2});
	} catch (Exception e) {
	e.printStackTrace();
	}
	}

	*//**
		* Method to insert records if not exist already in database
		* */
	/*

	private void insertEntryContacts(String contacttype, String name,
	String speclty, String phon, String mob, String fax, String email,
	String address, String notes) {
	long id = -1;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);

	SQLiteStatement stmt = checkDB
			.compileStatement("INSERT INTO Contacts(ContactType, Name,Speciality,Phone,Mobile,Fax,Email,Address,Notes) VALUES(?,?,?,?,?,?,?,?,?)");

	stmt.bindString(1, contacttype);
	stmt.bindString(2, name);
	stmt.bindString(3, speclty);
	stmt.bindString(4, phon);
	stmt.bindString(5, mob);
	stmt.bindString(6, fax);
	stmt.bindString(7, email);
	stmt.bindString(8, address);
	stmt.bindString(9, notes);
	id = stmt.executeInsert();
	long temp = id;
	} catch (SQLException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	*//**
		* Method to insert all Contact details in database
		* 
		* */
	/*
	public void insertEntrycontact(String contacttype, String name,
	String speclty, String phon, String mob, String fax, String email,
	String address, String notes, String oldcontacttype, int id) {
	try {
	if (isEntryExistscontact(id, contacttype, oldcontacttype)) {
		updateEntryContacts(contacttype, name, speclty, phon, mob, fax,
				email, address, notes, oldcontacttype, id);
	} else {
		insertEntryContacts(contacttype, name, speclty, phon, mob, fax,
				email, address, notes);
	}
	} catch (SQLException e) {
	e.printStackTrace();
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	checkDB.close();
	}
	}

	public String getItemDetails(String col, String subevent, String mainevent,
	String time) {
	// SELECT Contact FROM Event where EventSubType='' AND Time='' AND
	// EventType=''
	String colval = "";
	Cursor cursor = null;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READONLY);
	String selectMonth = "SELECT " + col
			+ "  FROM Event where EventSubType='" + subevent
			+ "' AND Time='" + time + "' AND EventType='" + mainevent
			+ "'";
	cursor = checkDB.rawQuery(selectMonth, null);
	if (cursor.moveToFirst()) {
		do {
			colval = cursor.getString(0).trim();
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return colval;
	}

	public void insertTreatmentTrackerLog(String strTable,
	TreatmentLog objTreatmentLog) {
	SQLiteDatabase myDatabase = null;
	try {
	myDatabase = this.getWritableDatabase();
	ContentValues cv = new ContentValues();

	cv.put("BodyRegion", objTreatmentLog.getStrBodyRegion());
	cv.put("Date", objTreatmentLog.getStrDate());
	cv.put("CycleNo", objTreatmentLog.getiCycleNo());
	cv.put("Severity", objTreatmentLog.getStrSeverity());
	cv.put("Notes", objTreatmentLog.getStrNotes());

	long no_row = 0;

	// Checks whether Record to Insert/Update
	// if (myDetails.getiId() == 0) {
	// Insert New Record.

	no_row = myDatabase.update(strTable, cv, "Date=?",
			new String[] { objTreatmentLog.getStrDate() });

	if (no_row == 0)
		no_row = myDatabase.insert(strTable, null, cv);
	// } else {
	// // Update Record
	// no_row = myDatabase.update(tbl_LifestyleCartia, cv,
	// COL_USER_DETAILS_ID + " = ?", new String[] { ""
	// + myDetails.getiId() });
	// }

	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	}

	public void insertOnlyTreatmentTrackerLog(String strTable,
	TreatmentLog objTreatmentLog) {
	SQLiteDatabase myDatabase = null;
	try {
	myDatabase = this.getWritableDatabase();
	ContentValues cv = new ContentValues();

	cv.put("BodyRegion", objTreatmentLog.getStrBodyRegion());
	cv.put("Date", objTreatmentLog.getStrDate());
	cv.put("CycleNo", objTreatmentLog.getiCycleNo());
	cv.put("Severity", objTreatmentLog.getStrSeverity());
	cv.put("Notes", objTreatmentLog.getStrNotes());

	long no_row = 0;

	// Checks whether Record to Insert/Update
	// if (myDetails.getiId() == 0) {
	// Insert New Record.

	// no_row = myDatabase.update(strTable, cv, "Date=?", new String[]
	// { objTreatmentLog.getStrDate() });
	//
	// if (no_row == 0)
	no_row = myDatabase.insert(strTable, null, cv);
	// } else {
	// // Update Record
	// no_row = myDatabase.update(tbl_LifestyleCartia, cv,
	// COL_USER_DETAILS_ID + " = ?", new String[] { ""
	// + myDetails.getiId() });
	// }

	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	}

	public boolean overwriteTreatmentTrackerLog(String strDate,
	String strBodyRegion, String strSeverity, int iCycleNo) {
	SQLiteDatabase myDatabase = null;
	boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "SELECT * FROM " + tbl_TreatmentTracker
			+ " where Date = '" + strDate + "' AND BodyRegion = '"
			+ strBodyRegion + "' AND Severity='" + strSeverity + "'";
	Cursor cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0)
		bReturn = true;
	else
		bReturn = false;

	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	return bReturn;
	}

	public boolean overwriteTreatmentTrackerLogWithoutID(String strDate,
	String strBodyRegion, String strSeverity, int iCycleNo, int iID) {
	SQLiteDatabase myDatabase = null;
	boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "SELECT * FROM " + tbl_TreatmentTracker
			+ " where Date = '" + strDate + "' AND BodyRegion = '"
			+ strBodyRegion + "' AND Severity='" + strSeverity
			+ "' AND ID <>" + iID;
	Cursor cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0)
		bReturn = true;
	else
		bReturn = false;

	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	return bReturn;
	}

	public int fetchEntriesInCycle(String strDate, String strBodyRegion,
	String strSeverity, int iCycleNo) {
	int count = 0;
	SQLiteDatabase myDatabase = null;
	boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "SELECT * FROM " + tbl_TreatmentTracker
			+ " where CycleNo=" + iCycleNo;
	Cursor cursor = myDatabase.rawQuery(querry, null);
	count = cursor.getCount();

	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	return count;
	}

	public void updateTreatmentTrackerLogByID(String strTable,
	TreatmentLog objTreatmentLog) {
	SQLiteDatabase myDatabase = null;
	try {
	myDatabase = this.getWritableDatabase();
	ContentValues cv = new ContentValues();

	cv.put("BodyRegion", objTreatmentLog.getStrBodyRegion());
	cv.put("Date", objTreatmentLog.getStrDate());
	cv.put("CycleNo", objTreatmentLog.getiCycleNo());
	cv.put("Severity", objTreatmentLog.getStrSeverity());
	cv.put("Notes", objTreatmentLog.getStrNotes());

	long no_row = 0;

	// Checks whether Record to Insert/Update
	// if (myDetails.getiId() == 0) {
	// Insert New Record.

	no_row = myDatabase.update(strTable, cv, "ID=?", new String[] { ""
			+ objTreatmentLog.getiID() });

	// if (no_row == 0)
	// no_row = myDatabase.insert(strTable, null, cv);

	// } else {
	// // Update Record
	// no_row = myDatabase.update(tbl_LifestyleCartia, cv,
	// COL_USER_DETAILS_ID + " = ?", new String[] { ""
	// + myDetails.getiId() });
	// }

	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	}

	public ArrayList<TreatmentLog> fetchTreatmentTracker(int iCycleNo) {
	// iCycleNo=1;
	ArrayList<TreatmentLog> arrList = new ArrayList<TreatmentLog>();
	Cursor cursor = null;
	SQLiteDatabase myDatabase = null;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "SELECT * FROM " + tbl_TreatmentTracker
			+ " where CycleNo = " + iCycleNo + " ORDER BY Date asc";
	cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();
	if (count > 0) {
		if (cursor.moveToFirst()) {
			do {
				TreatmentLog objTreatmentLog = new TreatmentLog();
				objTreatmentLog.setiID(cursor.getInt(0));
				objTreatmentLog.setStrBodyRegion(cursor.getString(1));
				objTreatmentLog.setStrDate(cursor.getString(2));
				objTreatmentLog.setiCycleNo(cursor.getInt(3));
				objTreatmentLog.setStrSeverity(cursor.getString(4));
				objTreatmentLog.setStrNotes(cursor.getString(5));

				arrList.add(objTreatmentLog);
			} while (cursor.moveToNext());
		}
	}

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	if (cursor != null) {
		cursor.close();
	}

	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	return arrList;
	}

	public int fetchTreatmentTrackerID(String strDate, String strBodyRegion,
	String strSeverity, int iCycleNo) {
	// iCycleNo=1;
	int iID = 0;
	// ArrayList<TreatmentLog> arrList = new ArrayList<TreatmentLog>();
	Cursor cursor = null;
	SQLiteDatabase myDatabase = null;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "SELECT * FROM " + tbl_TreatmentTracker
			+ " where Date = '" + strDate + "' AND BodyRegion = '"
			+ strBodyRegion + "' AND Severity='" + strSeverity + "'";
	cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0) {
		if (cursor.moveToFirst()) {
			iID = cursor.getInt(0);

		}
	}
	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	if (cursor != null) {
		cursor.close();
	}

	if (myDatabase != null) {
		myDatabase.close();
	}
	}

	return iID;
	}

	public boolean DeletSubeventRecord(String name) {
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	} catch (Exception e) {
	e.printStackTrace();
	}
	return checkDB.delete("EventTypes", "EventSubType = ?",
		new String[] { name }) > 0;
	}

	public void DeletContacts(int ID) {
	SQLiteDatabase myDatabase = null;
	// boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "delete from Contacts" + "  where ID=" + ID;

	myDatabase.execSQL(querry);

	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Deletion Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	}

	public boolean Deleteventontrigger(String time) {
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	} catch (Exception e) {
	e.printStackTrace();
	}
	return checkDB.delete("Event", "Time = ?", new String[] { time }) > 0;
	}

	public TreatmentLog fetchSingleTreatment(int iID) {

	TreatmentLog objTreatmentLog = new TreatmentLog();
	// iCycleNo=1;
	Cursor cursor = null;
	SQLiteDatabase myDatabase = null;
	try {

	myDatabase = this.getReadableDatabase();
	String querry = "SELECT * FROM " + tbl_TreatmentTracker
			+ " where ID = " + iID;
	cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0) {
		if (cursor.moveToFirst()) {
			// do {

			objTreatmentLog.setiID(cursor.getInt(0));
			objTreatmentLog.setStrBodyRegion(cursor.getString(1));
			objTreatmentLog.setStrDate(cursor.getString(2));
			objTreatmentLog.setiCycleNo(cursor.getInt(3));
			objTreatmentLog.setStrSeverity(cursor.getString(4));
			objTreatmentLog.setStrNotes(cursor.getString(5));

			// arrList.add(objTreatmentLog);

			// } while (cursor.moveToNext());
		}
	}

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	if (cursor != null) {
		cursor.close();
	}

	if (myDatabase != null) {
		myDatabase.close();
	}
	}

	return objTreatmentLog;
	}

	public int fetchLastCycleNo() {

	// TreatmentLog objTreatmentLog = new TreatmentLog();
	int iCycleNo = 1;
	Cursor cursor = null;
	SQLiteDatabase myDatabase = null;
	try {

	myDatabase = this.getReadableDatabase();
	String querry = "SELECT CycleNo FROM " + tbl_TreatmentTracker
			+ " ORDER BY CycleNo desc limit 1 ";
	cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0) {
		if (cursor.moveToFirst()) {
			// do {

			iCycleNo = cursor.getInt(0);
		}
	}

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	if (cursor != null) {
		cursor.close();
	}

	if (myDatabase != null) {
		myDatabase.close();
	}
	}

	return iCycleNo;
	}

	public String fetchLastEntryDate() {

	// TreatmentLog objTreatmentLog = new TreatmentLog();
	String iCycleNo = "";
	Cursor cursor = null;
	SQLiteDatabase myDatabase = null;
	try {

	myDatabase = this.getReadableDatabase();
	String querry = "SELECT Date FROM " + tbl_TreatmentTracker
			+ " ORDER BY Date desc limit 1 ";
	cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0) {
		if (cursor.moveToFirst()) {
			// do {

			iCycleNo = cursor.getString(0);
		}
	}

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	if (cursor != null) {
		cursor.close();
	}

	if (myDatabase != null) {
		myDatabase.close();
	}
	}

	return iCycleNo;
	}

	public String fetchFirstEntryOfCurrentCycle(int iCycleNo) {

	// TreatmentLog objTreatmentLog = new TreatmentLog();
	String strDate = "";
	Cursor cursor = null;
	SQLiteDatabase myDatabase = null;
	try {

	myDatabase = this.getReadableDatabase();
	String querry = "SELECT Date FROM " + tbl_TreatmentTracker
			+ " where CycleNo=" + iCycleNo
			+ " ORDER BY Date asc limit 1 ";
	cursor = myDatabase.rawQuery(querry, null);
	int count = cursor.getCount();

	if (count > 0) {
		if (cursor.moveToFirst()) {
			// do {

			strDate = cursor.getString(0);
		}
	}

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	if (cursor != null) {
		cursor.close();
	}

	if (myDatabase != null) {
		myDatabase.close();
	}
	}

	return strDate;
	}

	public ArrayList<String> getlist(String month, String col) {
	// int result=cursor.getCount();
	ArrayList<String> result = new ArrayList<String>();
	try {
	int i = 0;
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	String selectQuerry = "SELECT " + col
			+ " FROM Event WHERE strftime('%Y-%m',  Date) = '" + month
			+ "' Order by Date DESC";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	result = new ArrayList<String>(cursor.getCount());
	if (cursor.moveToFirst()) {
		do {
			// result.add(cursor.getString(0));
			result.add(cursor.getString(0));
			i++;
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return result;
	}

	public String[] getmonths() {
	// int result=cursor.getCount();
	String result[] = null;
	try {
	int i = 0;
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READWRITE);
	String selectQuerry = "SELECT distinct strftime('%Y-%m',Date) FROM Event ORDER BY Date";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	result = new String[cursor.getCount()];
	if (cursor.moveToFirst()) {
		do {
			// result.add(cursor.getString(0));
			result[i] = cursor.getString(0);
			i++;
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return result;
	}

	public ArrayList<String> getfromcontacts(String type, String col) {
	ArrayList<String> result = new ArrayList<String>();
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READONLY);
	String selectQuerry = "SELECT " + col
			+ " FROM Contacts Where ContactType='" + type + "'";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.moveToFirst()) {
		do {
			result.add(cursor.getString(0));
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return result;
	}

	public String getstringfromcontacts(String type, String col) {
	String result = null;
	try {

	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READONLY);
	type = type.replace("\'", "\''");
	String selectQuerry = "SELECT " + col
			+ " FROM Contacts Where Name='" + type + "'";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.moveToFirst()) {
		do {
			result = cursor.getString(0);
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return result;
	}

	public String geteventidcontacts(String subeventtype, String date,
	int mainevent) {
	String result = null;
	try {
	checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
			SQLiteDatabase.OPEN_READONLY);
	String selectQuerry = "SELECT ID FROM Event where EventType="
			+ mainevent + " AND EventSubType='" + subeventtype
			+ "' AND Date='" + date + "'";
	Cursor cursor = checkDB.rawQuery(selectQuerry, null);
	if (cursor.moveToFirst()) {
		do {
			result = cursor.getString(0);
		} while (cursor.moveToNext());
	}
	} catch (Exception e) {
	e.printStackTrace();
	}
	return result;
	}

	public void updateNewTreatmentTracker(TreatmentLog objTreatmentTracker) {

	SQLiteDatabase myDatabase = null;
	// boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	// update TreatmentTracker set BodyRegion='Chest',Severity='Severe',
	// CycleNo=2, Date='2014-08-09', Notes=null where id=1

	String querry = "update " + tbl_TreatmentTracker
			+ " set BodyRegion='"
			+ objTreatmentTracker.getStrBodyRegion() + "',Severity='"
			+ objTreatmentTracker.getStrSeverity() + "', CycleNo="
			+ objTreatmentTracker.getiCycleNo() + ", Notes='"
			+ objTreatmentTracker.getStrNotes() + "' where Date='"
			+ objTreatmentTracker.getStrDate() + "' AND BodyRegion = '"
			+ objTreatmentTracker.getStrBodyRegion()
			+ "' AND Severity='" + objTreatmentTracker.getStrSeverity()
			+ "'";

	if (objTreatmentTracker.getStrNotes().equals(""))
		querry = "update " + tbl_TreatmentTracker + " set BodyRegion='"
				+ objTreatmentTracker.getStrBodyRegion()
				+ "',Severity='" + objTreatmentTracker.getStrSeverity()
				+ "', CycleNo=" + objTreatmentTracker.getiCycleNo()
				+ ", Notes=null where Date='"
				+ objTreatmentTracker.getStrDate()
				+ "' AND BodyRegion = '"
				+ objTreatmentTracker.getStrBodyRegion()
				+ "' AND Severity='"
				+ objTreatmentTracker.getStrSeverity() + "'";
	myDatabase.execSQL(querry);
	// int count = cursor.getCount();
	//
	// if (count > 0)
	// bReturn= true;
	// else
	// bReturn= false;

	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	// return bReturn;

	}

	public void updateNewTreatmentTrackerForID(
	TreatmentLog objTreatmentTracker, int iID) {

	SQLiteDatabase myDatabase = null;
	// boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	// update TreatmentTracker set BodyRegion='Chest',Severity='Severe',
	// CycleNo=2, Date='2014-08-09', Notes=null where id=1

	String querry = "update " + tbl_TreatmentTracker + " set Date='"
			+ objTreatmentTracker.getStrDate() + "', BodyRegion='"
			+ objTreatmentTracker.getStrBodyRegion() + "',Severity='"
			+ objTreatmentTracker.getStrSeverity() + "', CycleNo="
			+ objTreatmentTracker.getiCycleNo() + ", Notes='"
			+ objTreatmentTracker.getStrNotes() + "' where ID=" + iID;

	if (objTreatmentTracker.getStrNotes().equals(""))
		querry = "update " + tbl_TreatmentTracker + " set Date='"
				+ objTreatmentTracker.getStrDate() + "', BodyRegion='"
				+ objTreatmentTracker.getStrBodyRegion()
				+ "',Severity='" + objTreatmentTracker.getStrSeverity()
				+ "', CycleNo=" + objTreatmentTracker.getiCycleNo()
				+ ", Notes=null where ID=" + iID;
	myDatabase.execSQL(querry);
	// int count = cursor.getCount();
	//
	// if (count > 0)
	// bReturn= true;
	// else
	// bReturn= false;

	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	// return bReturn;

	}

	public void deleteTreatmentTrackerLog(int iID) {

	SQLiteDatabase myDatabase = null;
	// boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	String querry = "delete from " + tbl_TreatmentTracker
			+ "  where ID=" + iID;

	myDatabase.execSQL(querry);

	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}

	}

	public void updateWithIDCV(TreatmentLog objTreatmentLog, int iID) {
	SQLiteDatabase myDatabase = null;
	try {
	myDatabase = this.getWritableDatabase();
	ContentValues cv = new ContentValues();

	cv.put("BodyRegion", objTreatmentLog.getStrBodyRegion());
	cv.put("Date", objTreatmentLog.getStrDate());
	cv.put("CycleNo", objTreatmentLog.getiCycleNo());
	cv.put("Severity", objTreatmentLog.getStrSeverity());
	cv.put("Notes", objTreatmentLog.getStrNotes());

	long no_row = 0;

	// Checks whether Record to Insert/Update
	// if (myDetails.getiId() == 0) {
	// Insert New Record.

	no_row = myDatabase.update(tbl_TreatmentTracker, cv, "ID=?",
			new String[] { "" + objTreatmentLog.getiID() });

	// if (no_row == 0)
	// no_row = myDatabase.insert(strTable, null, cv);

	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	}

	public void updateNewEventWithID(String mainevent, String subevent,
	String Clinic_contact, String loc, String date, String time,
	String remtype, String notes, String prevdate, String eventid) {

	SQLiteDatabase myDatabase = null;
	// boolean bReturn = true;
	try {
	myDatabase = this.getReadableDatabase();
	// update TreatmentTracker set BodyRegion='Chest',Severity='Severe',
	// CycleNo=2, Date='2014-08-09', Notes=null where id=1

	// checkDB = SQLiteDatabase.openDatabase(strmyPath, null,
	// SQLiteDatabase.OPEN_READWRITE);
	// ContentValues cv = new ContentValues();
	// cv.put("EventType", mainevent);
	// cv.put("EventSubType", subevent);
	// cv.put("Contact", Clinic_contact);
	// cv.put("Location", loc);
	// cv.put("Date", date);
	// cv.put("ReminderType", remtype);
	// cv.put("Notes", notes);
	// cv.put("Time", time);

	String querry = "update Event set EventType='" + mainevent
			+ "', EventSubType='" + subevent + "',Contact='"
			+ Clinic_contact + "', Location='" + loc + "', Date='"
			+ date + "', ReminderType='" + remtype + "', Notes='"
			+ notes + "',Time='" + time + "' where ID=" + eventid;

	myDatabase.execSQL(querry);
	} catch (Exception e) {
	e.printStackTrace();
	Log.i(TAG, "Insertion/Updation Error");
	} finally {
	if (myDatabase != null) {
		myDatabase.close();
	}
	}
	// return bReturn;

	}*/
}
