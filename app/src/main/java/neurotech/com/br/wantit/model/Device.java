package neurotech.com.br.wantit.model;

import java.util.List;

public class Device {

	private String deviceId;
	private String deviceName;
	private List<App> listApp;

	public Device(String deviceId, String deviceName, List<App> listApp) {
		super();
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.listApp = listApp;
	}

	public Device() {
		super();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public List<App> getListApp() {
		return listApp;
	}

	public void setListApp(List<App> listApp) {
		this.listApp = listApp;
	}

	@Override
	public String toString() {
		return "Device [deviceId=" + deviceId + ", deviceName=" + deviceName
				+ "]";
	}
}
