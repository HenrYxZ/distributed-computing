package hjhenriq.chat.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Conversation {
	private Stack<Message> messages;
	private List<Person> participants;
	
	public Conversation(){
		this.messages = new Stack<Message>();
		this.participants =  new LinkedList<Person>();
	}
	
	public void addMessage(Message m) {
		this.messages.add(m);
	}
	public void addParticipant(Person p) {
		this.participants.add(p);
	}
	public void removeParticipant(Person p) {
		this.participants.remove(p);
	}
	public String listMessages(int n) {
		String s = "";
		Message currentMsg;
		for (int i = 0; i < n; i++) {
			currentMsg = this.messages.get(i);
			// Don't show system messages
			if (currentMsg.getFlag() < 2)
				s += currentMsg.toString();
		}
		return s;
	}
}
