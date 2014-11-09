package hjhenriq.chat.model;

import java.util.List;

public class Message {
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
