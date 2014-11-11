package hjhenriq.chat.client;

import hjhenriq.chat.model.Contacts;
import hjhenriq.chat.model.Conversation;
import hjhenriq.chat.model.Person;
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
	private Contacts mContacts; 

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
		this.mName = name;
		this.mServer.register(this, name, pass);
	}

	public void authenticateNamePass() throws RemoteException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your name: ");
		String name = sc.nextLine();
		System.out.println("Enter your password: ");
		String pass = sc.nextLine();
		sc.close();
		this.mName = name;
		this.mServer.authenticate(this, name, pass);
	}

	public void enterToChat() {
		Scanner sc = new Scanner(System.in);
		String menu = "Hello! Choose one option:\n" + "[1] Create new user."
				+ "[2] Sign in with existing user" + "[0] Exit";
		System.out.println(menu);
		try {
			if (sc.hasNextInt()) {
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
		sc = new Scanner(System.in);
		
		System.out.println("Running");
		String menu = "Enter: \n" 
				+ "[0] to go back\n"
				+ "[1] to add a new contact\n" 
				+ "[2] to remove a contact\n"
				+ "[3] to list contacts\n" 
				+ "[4] to create a conversation";
		System.out.println(menu);
		if (sc.hasNextInt()) {
			switch (sc.nextInt()) {
			case 1:
				addContact();
			case 2:
				removeContact();
			case 3:
				listContacts();
			case 4:
				createConversation();
			default:
				sc.close();
				return;
			}
		}
	}

	//------------------ Output Printing Functions --------------------------------
	
	private void createConversation() {
		Conversation conv = new Conversation();
		printConversationMenu();
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
		}
		sc.close();
		
	}

	private void listContacts() {
		printContactMenu("list");
	}

	private void removeContact() {
		printContactMenu("remove");
		Scanner sc = new Scanner(System.in);
		String name = sc.nextLine();
		mContacts.remove(name);
		sc.close();
	}

	private void addContact() {
		printContactMenu("add");
		Scanner sc = new Scanner(System.in);
		String name = sc.nextLine();
		mContacts.add(new Person(name));
		sc.close();
	}

	//------------------------ Methods for Printing --------------------------------
	public void printConversationMenu(){
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
			System.out.println("This are your contacts \n" + mContacts.toString());
	}
}
