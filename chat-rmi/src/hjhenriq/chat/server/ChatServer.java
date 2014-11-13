package hjhenriq.chat.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import hjhenriq.chat.client.ChatClientIF;
import hjhenriq.chat.model.Contacts;
import hjhenriq.chat.model.User;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, ChatClientIF> clientsNames;
	private HashMap<String, String> namesPass;
	private HashMap<String, User> namesUsers;

	protected ChatServer() throws RemoteException {
		super();
		clientsNames = new HashMap<String, ChatClientIF>();
		namesPass = new HashMap<String, String>();
		namesUsers = new HashMap<String, User>();
	}

	// ------------------------ Interface Methods ------------------------------
	@Override
	public synchronized void authenticate(ChatClientIF chatClient, String name,
			String pass) throws RemoteException {
		if (namesPass.containsKey(name) && namesPass.get(name).equals(pass)) {
			clientsNames.put(name, chatClient);
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
			clientsNames.put(name, chatClient);
			User u = new User(new Contacts(), name);
			this.namesUsers.put(name, u);
			chatClient.retrieveRegistration(u);
			printReg(name, pass, true);
		}
	}
	
	@Override
	public synchronized void updateUser(String name, User newUser) 
			throws RemoteException{
		this.namesUsers.replace(name, newUser);
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

}
