package neurotech.com.br.wantit.model;

import java.util.Date;

public class RequestLocation {
	
	private String deviceId;
	private String loja;
	private Date dateTime;
	private String local;
	
	public RequestLocation() {
		super();
	}
	
	public RequestLocation(String deviceId, String loja, Date dateTime) {
		super();
		this.deviceId = deviceId;
		this.loja = loja;
		this.dateTime = dateTime;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
	public String getLoja() {
		return loja;
	}
	public void setLoja(String loja) {
		this.loja = loja;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}

}
