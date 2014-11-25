package hjhenriq.chat.model;

import java.io.Serializable;

public class Person implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7417080450980108786L;
	private String name;
	public String getName() {
		return this.name;
	}
	public Person(String n) {
		this.name = n;
	}
}
