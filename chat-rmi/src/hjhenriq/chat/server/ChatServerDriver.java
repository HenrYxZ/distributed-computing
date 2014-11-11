package hjhenriq.chat.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ChatServerDriver {

	public static void main(String[] args) throws RemoteException, MalformedURLException {
		Naming.rebind("rmi://127.0.0.1:1099/ChatServer", new ChatServer());
	}

}
