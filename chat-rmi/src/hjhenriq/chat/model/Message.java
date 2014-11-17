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
	private int flag;

	public int getFlag() {
		/**
		 * The flags are:
		 *  0 for normal message,
		 *  1 for message with a file
		 *  2 for message saying the client has been removed from a conversation
		 *  3 for system messages
		 */
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	/**
	 * Creates a new message
	 * @param t the text of the message
	 * @param o name of the owner 
	 */

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
