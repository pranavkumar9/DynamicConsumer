package entity;

public class JsonMessage {
	public String consumerName; 
	public String TableNo;
	public String ExchName;
	public String message;
	public JsonMessage(String consumerName, String tableNo, String exchName, String message) {
		super();
		this.consumerName = consumerName;
		TableNo = tableNo;
		ExchName = exchName;
		this.message = message;
	}
	public JsonMessage() {
		super();
	}
	public String getConsumerName() {
		return consumerName;
	}
	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}
	public String getTableNo() {
		return TableNo;
	}
	public void setTableNo(String tableNo) {
		TableNo = tableNo;
	}
	public String getExchName() {
		return ExchName;
	}
	public void setExchName(String exchName) {
		ExchName = exchName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
