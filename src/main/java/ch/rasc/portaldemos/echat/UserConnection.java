package ch.rasc.portaldemos.echat;

public class UserConnection {
	private String username;

	private String browser;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	@Override
	public String toString() {
		return "UserConnection [username=" + username + ", browser=" + browser + "]";
	}

}
