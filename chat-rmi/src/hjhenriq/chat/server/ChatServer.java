package hjhenriq.chat.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import hjhenriq.chat.client.ChatClientIF;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, ChatClientIF> clientsNames;
	private HashMap<String, String> namesPass;

	protected ChatServer() throws RemoteException {
		super();
		clientsNames = new HashMap<String, ChatClientIF>();
		namesPass = new HashMap<String, String>();
	}

	@Override
	public synchronized void authenticate(ChatClientIF chatClient, String name,
			String pass) throws RemoteException {
		if (namesPass.get(name).equals(pass)) {
			clientsNames.put(name, chatClient);
			chatClient.retrieveAuthentication(true);
			printAuthenticated(name, pass, true);
		} else {
			chatClient.retrieveAuthentication(false);
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
			chatClient.retrieveRegistration(false);
			printReg(name, pass, false);
		} else {
			namesPass.put(name, pass);
			clientsNames.put(name, chatClient);
			chatClient.retrieveRegistration(true);
			printReg(name, pass, true);
		}
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
