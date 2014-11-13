package hjhenriq.chat.client;

import hjhenriq.chat.model.Contacts;
import hjhenriq.chat.model.Conversation;
import hjhenriq.chat.model.Person;
import hjhenriq.chat.server.ChatServerIF;

import java.io.Console;
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
	private Contacts mContacts;
	private Console mConsole;
	private Conversation mCurrentConversation;
	private boolean authenticated;

	protected ChatClient(ChatServerIF chatServer) throws RemoteException {
		super();
		this.mServer = chatServer;
		this.mConsole = System.console();
		this.mContacts = new Contacts();
		this.mCurrentConversation = null;
		this.authenticated = false;
	}

	public void sendNewUser() throws RemoteException {
		boolean validName = false;
		String name = "default";
		while (!validName) {
			name = mConsole.readLine("Enter new name: ");
			validName = this.mServer.register(this, name);
			if (!validName)
				System.out.println("Name already used.");
		}
		String pass = mConsole.readLine("Enter new password: ");
		this.mName = name;
		this.mServer.register(this, name, pass);
	}

	public void authenticateNamePass() throws RemoteException {
		String name = mConsole.readLine("Enter your name: ");
		String pass = mConsole.readLine("Enter your password: ");
		this.mName = name;
		this.mServer.authenticate(this, name, pass);
	}

	public void enterToChat() {
		String menu = "Hello! Choose one option:\n" + "[1] Create new user."
				+ "[2] Sign in with existing user" + "[0] Exit\n";
		boolean running = true;
		while (running) {

			try {
				int option = Integer.parseInt(mConsole.readLine(menu));
				switch (option) {
				case 1:
					sendNewUser();
					if (this.authenticated)
						return;
					break;
				case 2:
					authenticateNamePass();
					if (this.authenticated)
						return;
					break;
				default:
					running = false;
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		boolean running = true;
		System.out.println("Running");
		String menu = "Enter: \n" + "[0] to go back\n"
				+ "[1] to add a new contact\n" + "[2] to remove a contact\n"
				+ "[3] to list contacts\n" + "[4] to create a conversation\n";
		
		while (running) {
			try {
				int option = Integer.parseInt(mConsole.readLine(menu));
				switch (option) {
				case 1:
					addContact();
					break;
				case 2:
					removeContact();
					break;
				case 3:
					listContacts();
					break;
				case 4:
					createConversation();
					break;
				default:
					running = false;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ---------------- Interface Methods:              -------------------------
	//----------------------- Connection with server ----------------------------
	@Override
	public void retrieveAuthentication(boolean answer) {
		if (answer == true) {
			this.authenticated = true;
			System.out.println("Welcome back " + this.mName + "!");
		} else {
			this.authenticated = false;
			System.out.println("Wrong user or password, please try again.");
		}
	}

	@Override
	public void retrieveRegistration(boolean answer) {
		if (answer == true) {
			this.authenticated = true;
			System.out.println("Successfully registered!");
		} else {
			this.authenticated = false;
			System.out.println("Problems in registration. Please try again.");
		}
	}

	

	// ------------------ Methods for Running Menu -------------------------------
	
	private void addContact() {
		printContactMenu("add");
		String name = mConsole.readLine();
		mContacts.add(new Person(name));
	}
	
	
	
	private void removeContact() {
		printContactMenu("remove");
		String name = mConsole.readLine();
		mContacts.remove(name);
	}

	private void listContacts() {
		printContactMenu("list");
	}

	private void createConversation() {
		mCurrentConversation = new Conversation();
		printConversationMenu();
		while (true) {
			String line = mConsole.readLine();
			if (line.equalsIgnoreCase("exit"))
				break;
		}
	}

	// ------------------------ Methods for Printing -----------------------------
	public void printConversationMenu() {
		String menu = "Welcome to the chat! \n"
				+ "Enter exit to close \n"
				+ "Enter add([name]) to add someone to the conversation \n"
				+ "Enter remove([name]) to remove someone from the conversation";
		System.out.println(menu);
	}

	public void printContactMenu(String action) {
		if (action.equals("add"))
			System.out.println("Write the name of the new contact");
		else if (action.equals("remove"))
			System.out.println("Write the name of the contact to be removed");
		else
			System.out.println("This are your contacts \n"
					+ mContacts.toString());
	}
}
