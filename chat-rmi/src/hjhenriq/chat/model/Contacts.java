package hjhenriq.chat.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Contacts implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3497049052670075693L;
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
	public void remove(String name) {
		Iterator<Person> i = this.people.iterator();
		while(i.hasNext()) {
			Person p = i.next();
			if (p.getName().equals(name)) {
				this.people.remove(p);
				break;
			}
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		Iterator<Person> it;
		Person current = null;
		for(it = this.people.iterator(); it.hasNext();) {
			current = it.next();
			s += current.getName() + "\n";
		}
		return s;
	}
}
