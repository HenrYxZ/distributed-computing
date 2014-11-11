package hjhenriq.chat.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hjhenriq.chat.client.ChatClientIF;

public interface ChatServerIF extends Remote{
	void authenticate(ChatClientIF chatClient, String name, String pass)
			throws RemoteException;

	boolean register(ChatClientIF chatClient, String name)
			throws RemoteException;

	void register(ChatClientIF chatClient, String name, String pass)
			throws RemoteException;
}
