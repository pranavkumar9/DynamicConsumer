package entity;

public class JsonMessage {
	public String revId; 
	public String exchName;
	public String message;
	
	public JsonMessage(String revId, String exchName, String message) {
		super();
		this.revId = revId;
		this.exchName = exchName;
		this.message = message;
	}
	public JsonMessage() {
		super();
	}
	public String getRevId() {
		return revId;
	}
	public void setRevId(String revId) {
		this.revId = revId;
	}
	
	public String getExchName() {
		return exchName;
	}
	public void setExchName(String exchName) {
		this.exchName = exchName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
