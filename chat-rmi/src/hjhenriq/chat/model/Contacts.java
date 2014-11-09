package hjhenriq.chat.model;

import java.util.LinkedHashSet;

public class Contacts {
	private LinkedHashSet<Person> people;

	public Contacts() {
		this.people = new LinkedHashSet<Person>();
	}
	public void add(Person p) {
		this.people.add(p);
	}
	public void remove(Person p) {
		this.people.remove(p);
	}
}
