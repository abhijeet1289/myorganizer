package Data;

public class ReminderStruct {

	public String strReminderType = "", strSubject = "",strDate="";
	int iID;

	public int getiID() {
		return iID;
	}

	public void setiID(int iID) {
		this.iID = iID;
	}

	public String getStrReminderType() {
		return strReminderType;
	}

	public void setStrReminderType(String strReminderType) {
		this.strReminderType = strReminderType;
	}

	public String getStrSubject() {
		return strSubject;
	}

	public void setStrSubject(String strSubject) {
		this.strSubject = strSubject;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	

}
