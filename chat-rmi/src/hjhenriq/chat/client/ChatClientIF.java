package hjhenriq.chat.client;

import hjhenriq.chat.model.Message;
import hjhenriq.chat.model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientIF extends Remote{
	void retrieveAuthentication(User answer) throws RemoteException;
	void retrieveRegistration(User answer) throws RemoteException;
	void receive(Message m) throws RemoteException;
	void broadcast(Message m, ChatClientIF initiator) throws RemoteException;
	boolean replyInvitation(ChatClientIF c, String name) throws RemoteException;
	void leaveConversation(String name) throws RemoteException;
	void enterConversation() throws RemoteException;
	
}
