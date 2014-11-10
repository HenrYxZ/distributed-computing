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

	public void authenticate(ChatClientIF chatClient, String name, String pass) {
		if (namesPass.get(name).equals(pass)) {
			clientsNames.put(name, chatClient);
			chatClient.retrieveAuthentication(true);
		}
		else chatClient.retrieveAuthentication(false);
			
	}

	@Override
	public boolean register(ChatClientIF chatClient, String name) {
		if (namesPass.containsKey(name))
			return false;
		else
			return true;
	}

	public void register(ChatClientIF chatClient, String name, String pass) {
		namesPass.put(name, pass);
		clientsNames.put(name, chatClient);
	}

}
