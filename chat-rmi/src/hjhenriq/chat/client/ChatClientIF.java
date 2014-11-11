package hjhenriq.chat.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClientIF extends Remote{
	void retrieveAuthentication(boolean answer) throws RemoteException;
	void retrieveRegistration(boolean answer) throws RemoteException;
}
