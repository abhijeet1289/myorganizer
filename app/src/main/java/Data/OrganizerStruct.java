package Data;

public class OrganizerStruct {

	public String strName = "", strDetails = "",strDate="",strAmount="",strEntity="";
	
	public int iId,iType=0;

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	public String getStrDetails() {
		return strDetails;
	}

	public void setStrDetails(String strDetails) {
		this.strDetails = strDetails;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrEntity() {
		return strEntity;
	}

	public void setStrEntity(String strEntity) {
		this.strEntity = strEntity;
	}

	public int getiId() {
		return iId;
	}

	public void setiId(int iId) {
		this.iId = iId;
	}

	public int getiType() {
		return iType;
	}

	public void setiType(int iType) {
		this.iType = iType;
	}
	

}
