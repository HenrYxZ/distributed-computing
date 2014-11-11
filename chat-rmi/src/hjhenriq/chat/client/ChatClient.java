package hjhenriq.chat.client;

import hjhenriq.chat.server.ChatServerIF;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8214387253929191454L;
	private ChatServerIF mServer;
	private String mName;

	protected ChatClient(ChatServerIF chatServer) throws RemoteException {
		super();
		this.mServer = chatServer;
		enterToChat();
	}

	public void sendNewUser() throws RemoteException {
		Scanner sc = new Scanner(System.in);
		boolean validName = false;
		String name = "default";
		while (!validName) {
			System.out.println("Enter new name: ");
			name = sc.nextLine();
			validName = this.mServer.register(this, name);
			if (!validName)
				System.out.println("Name already used.");
		}
		System.out.println("Enter new password: ");
		String pass = sc.nextLine();
		sc.close();
		this.mServer.register(this, name, pass);
	}

	public void authenticateNamePass() throws RemoteException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your name: ");
		String name = sc.nextLine();
		System.out.println("Enter your password: ");
		String pass = sc.nextLine();
		sc.close();
		this.mServer.authenticate(this, name, pass);
	}

	public void enterToChat() {
		Scanner sc = new Scanner(System.in);
		String menu = "Hello! Choose one option:\n" + "[1] Create new user."
				+ "[2] Sign in with existing user" + "[0] Exit";
		System.out.println(menu);
		try {
			switch (sc.nextInt()) {
			case 1:
				sendNewUser();
				break;
			case 2:
				authenticateNamePass();
				break;
			default:
				break;

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sc.close();
	}

	@Override
	public void retrieveAuthentication(boolean answer) {
		if (answer == true) {
			System.out.println("Welcome back " + this.mName + "!");
			run();
		} else {
			System.out.println("Wrong user or password, please try again.");
			enterToChat();
		}
	}

	@Override
	public void retrieveRegistration(boolean answer) {
		if (answer == true) {
			System.out.println("Successfully registered!");
			run();
		} else {
			System.out.println("Problems in registration. Please try again.");
			enterToChat();
		}
	}

	public void run() {
		Scanner sc;
		while (true) {
			sc = new Scanner(System.in);
			System.out.println("Running");
			String menu = "Enter: " + "[0] to go back"
					+ "[1] to add a new contact" + "[2] to remove a contact"
					+ "[3] to list contacts" + "[4] to create a conversation";
			switch (sc.nextInt()) {
			case 0:
				sc.close();
				return;
			case 1:
				//TODO Menu options
			}
			System.out.println(menu);
			if (sc.nextLine().equals("exit"))
				break;

		}
		sc.close();
	}

}
