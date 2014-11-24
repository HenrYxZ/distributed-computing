package hjhenriq.chat.client;

import hjhenriq.chat.model.Message;
import hjhenriq.chat.model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ChatClientIF extends Remote{
	void retrieveAuthentication(User answer) throws RemoteException;
	void retrieveRegistration(User answer) throws RemoteException;
	void receive(Message m) throws RemoteException;
	void broadcast(Message m, String initiator) throws RemoteException;
	void recvInvitation(ChatClientIF c, String name) throws RemoteException;
	void accepted(String name, ChatClientIF c) throws RemoteException;
	void leftConversation(String name) throws RemoteException;
	void newCoordinator(HashMap<String, ChatClientIF> participants)
			throws RemoteException;
	void newCoordinator(ChatClientIF coord) throws RemoteException;
	String getName() throws RemoteException;
	
}
