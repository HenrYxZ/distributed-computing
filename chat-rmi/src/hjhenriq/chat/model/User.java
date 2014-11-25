package hjhenriq.chat.model;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8604734104590735943L;
	private Contacts mContacts;
	private String mName;

	public User(Contacts contacts, String name) {
		this.mContacts = contacts;
		this.mName = name;
	}

	public Contacts getContacts() {
		return mContacts;
	}

	public String getName() {
		return mName;
	}

}
