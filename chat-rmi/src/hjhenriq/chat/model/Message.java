package hjhenriq.chat.model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8275425198222735927L;
	private String text;
	private String owner;
	private List<Person> receivers;
	
	public Message(String t, String o) {
		this.text = t;
		this.owner = o;
	}
	
	public void addReceiver(Person p) {
		this.receivers.add(p);
	}
	
	@Override
	public String toString() {
		return this.owner + ": " + this.text;
	}
}
