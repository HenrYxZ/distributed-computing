package hjhenriq.chat.client;

import hjhenriq.chat.model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientIF extends Remote{
	void retrieveAuthentication(User answer) throws RemoteException;
	void retrieveRegistration(User answer) throws RemoteException;
}
