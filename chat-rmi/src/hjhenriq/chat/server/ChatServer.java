package hjhenriq.chat.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;

import hjhenriq.chat.client.ChatClientIF;
import hjhenriq.chat.model.Contacts;
import hjhenriq.chat.model.Person;
import hjhenriq.chat.model.User;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, ChatClientIF> namesClients;
	private HashMap<String, String> namesPass;
	private HashMap<String, User> namesUsers;

	protected ChatServer() throws RemoteException {
		super();
		namesClients = new HashMap<String, ChatClientIF>();
		namesPass = new HashMap<String, String>();
		namesUsers = new HashMap<String, User>();
	}

	// ------------------------ Interface Methods ------------------------------
	@Override
	public synchronized void authenticate(ChatClientIF chatClient, String name,
			String pass) throws RemoteException {
		if (namesPass.containsKey(name) && namesPass.get(name).equals(pass)) {
			namesClients.put(name, chatClient);
			User u = this.namesUsers.get(name);
			chatClient.retrieveAuthentication(u);
			printAuthenticated(name, pass, true);
		} else {
			chatClient.retrieveAuthentication(null);
			printAuthenticated(name, pass, false);
		}
	}

	@Override
	public synchronized boolean register(ChatClientIF chatClient, String name) {
		if (namesPass.containsKey(name)) {
			printRegistered(name, false);
			return false;
		} else {
			printRegistered(name, true);
			return true;
		}
	}

	@Override
	public synchronized void register(ChatClientIF chatClient, String name, String pass)
			throws RemoteException {
		if (namesPass.containsKey(name)) {
			chatClient.retrieveRegistration(null);
			printReg(name, pass, false);
		} else {
			namesPass.put(name, pass);
			namesClients.put(name, chatClient);
			User u = new User(new Contacts(), name);
			this.namesUsers.put(name, u);
			chatClient.retrieveRegistration(u);
			printReg(name, pass, true);
		}
	}
	
	@Override
	public synchronized void updateUser(String name, User newUser) 
			throws RemoteException{
		// this method is called by the client when he is logging out
		this.namesUsers.replace(name, newUser);
		this.namesClients.remove(name);
		printUserLeft(name);
	}
	
	@Override
	public synchronized String connectedList(Contacts contactsList) throws RemoteException {
		String answer = "";
		Iterator<Person> it;
		Person currentContact = null;
		for(it = contactsList.getPeople().iterator(); it.hasNext();){
			currentContact = it.next();
			if(this.namesClients.containsKey(currentContact.getName()))
				answer += currentContact.getName() + "\n";
		}
		printShowConnected();
		if (answer.isEmpty())
			return "There isn't any contact connected";
		else
			return "The contacts connected now are: \n" + answer;
		
	}
	
	@Override
	public ChatClientIF getClient(String name) throws RemoteException {
		printGetClient(name);
		return this.namesClients.get(name);
	}

	// ----------------- Functions for Printing Output ---------------------
	private void printAuthenticated(String name, String pass, boolean success) {
		if (success)
			System.out.println("(" + name + ", " + pass + ") authenticated.");
		else
			System.out.println("Authentication failed, (" + name + ", " + pass
					+ ").");
	}

	private void printRegistered(String name, boolean success) {
		if (success)
			System.out.println("Name " + name + " selected.");
		else
			System.out.println("Name " + name + " already used.");
	}

	private void printReg(String name, String pass, boolean success) {
		if (success)
			System.out.println("(" + name + ", " + pass + ") registered.");
		else
			System.out.println("Authentication failed, (" + name + ", " + pass
					+ ").");
	}
	
	private void printUserLeft(String name) {
		System.out.println("User " + name + " has logged out.");
	}
	
	private void printShowConnected() {
		System.out.println("Someone asked for the connected contacts list");
	}
	
	private void printGetClient(String name) {
		System.out.println("Someone asked for the client with name " + name);
	}
}
