package hjhenriq.chat.client;

import hjhenriq.chat.server.ChatServerIF;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ChatClientDriver {

	public static void main(String[] args) {
		String serverUrl = "rmi://127.0.0.1:1099/ChatServer";
		ChatServerIF chatServer;
		ChatClient chatClient;
		try {
			chatServer = (ChatServerIF)Naming.lookup(serverUrl);
			chatClient = new ChatClient(chatServer);
			chatClient.enterToChat();
		} catch (MalformedURLException e) {
			System.out.println("Wrong URL...");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.out.println("Problem with remote object...");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Server not bound...");
			e.printStackTrace();
		}
	}

}
