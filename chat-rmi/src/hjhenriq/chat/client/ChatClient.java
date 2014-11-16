package hjhenriq.chat.client;

import hjhenriq.chat.model.Conversation;
import hjhenriq.chat.model.Message;
import hjhenriq.chat.model.Person;
import hjhenriq.chat.model.User;
import hjhenriq.chat.server.ChatServerIF;

import java.io.Console;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8214387253929191454L;
	private ChatServerIF mServer;
	private User mUser;
	private Console mConsole;
	private Conversation mCurrentConversation;
	private boolean isAuthenticated;
	private boolean isCoordinator;
	private HashMap<String, ChatClientIF> mParticipants;
	private ChatClientIF mCoordinator;
	private boolean hasToReply;

	protected ChatClient(ChatServerIF chatServer) throws RemoteException {
		super();
		this.mServer = chatServer;
		this.mConsole = System.console();
		this.mUser = null;
		this.mCurrentConversation = null;
		this.isAuthenticated = false;
		this.isCoordinator = false;
		this.mParticipants = null;
		this.mCoordinator = null;
		this.hasToReply = false;
	}

	/*
	 * ---------------- Interface Methods: -------------------------
	 * ----------------------- Connection with server
	 * ----------------------------
	 */

	@Override
	public void retrieveAuthentication(User answer) {
		if (answer != null) {
			this.isAuthenticated = true;
			this.mUser = answer;
			System.out.println("Welcome back " + this.mUser.getName() + "!");
		} else {
			this.isAuthenticated = false;
			System.out.println("Wrong user or password, please try again.");
		}
	}

	@Override
	public void retrieveRegistration(User answer) {
		if (answer != null) {
			this.isAuthenticated = true;
			this.mUser = answer;
			System.out.println("Successfully registered!");
		} else {
			this.isAuthenticated = false;
			System.out.println("Problems in registration. Please try again.");
		}
	}

	/*
	 * ---------------- Interface Methods: -------------------------
	 * ------------------------- Peer communication --------------------------
	 */

	@Override
	public synchronized void receive(Message m) {
		this.mCurrentConversation.addMessage(m);
		if (m.getFlag() == 1) {
			this.mCoordinator = null;
			this.mCurrentConversation = null;
		}
		System.out.println(m.toString());
	}

	@Override
	public synchronized void broadcast(Message m, ChatClientIF initiator)
			throws RemoteException {
		Iterator<ChatClientIF> it;
		for (it = this.mParticipants.values().iterator(); it.hasNext();) {
			ChatClientIF c = it.next();
			if (c != initiator)
				c.receive(m);
		}
		System.out.println(m.toString());

	}

	@Override
	public synchronized boolean replyInvitation(ChatClientIF c, String name)
			throws RemoteException {
		this.hasToReply = true;
		String prompt = "You have been invited to a new conversation by "
				+ name + ".\nWrite yes to accept the invitation.\n";
		String option = this.mConsole.readLine(prompt);
		if (option.equals("yes")) {
			if (this.isCoordinator) {
				this.isCoordinator = false;
			}
			else
				// Tells the coordinator that is leaving the conversation
				this.mCoordinator.leaveConversation(this.mUser.getName());
			this.mCoordinator = c;
			this.mCoordinator.receive(new Message("I'm connected", this.mUser
					.getName()));
			return true;
		}

		return false;
	}

	@Override
	public void leaveConversation(String name) throws RemoteException {
		this.mParticipants.remove(name);
	}
	
	@Override
	public void enterConversation() throws RemoteException {
		this.hasToReply = false;
		printConversationMenu(this.isCoordinator);
		runConversation();
	}

	/*
	 * ----------- Methods for Authentication and Registration -----------------
	 */

	private void sendNewUser() throws RemoteException {
		boolean validName = false;
		String name = "default";
		while (!validName) {
			name = mConsole.readLine("Enter new name: ");
			validName = this.mServer.register(this, name);
			if (!validName)
				System.out.println("Name already used.");
		}
		String pass = mConsole.readLine("Enter new password: ");
		this.mServer.register(this, name, pass);
	}

	private void authenticateNamePass() throws RemoteException {
		String name = mConsole.readLine("Enter your name: ");
		String pass = mConsole.readLine("Enter your password: ");
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
				case 0:
					System.exit(0);
				case 1:
					sendNewUser();
					if (this.isAuthenticated)
						return;
					break;
				case 2:
					authenticateNamePass();
					if (this.isAuthenticated)
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

	/*
	 * --------------------------- Main Menu -----------------------------
	 */

	public void run() {
		boolean running = true;
		System.out.println("Running");
		String menu = "Enter: \n" + "[0] to exit\n"
				+ "[1] to add a new contact\n" + "[2] to remove a contact\n"
				+ "[3] to show connected contacts\n" + "[4] to list contacts\n"
				+ "[5] to create a conversation\n";

		while (running && !this.hasToReply) {
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
					showConnectedContacts();
					break;
				case 4:
					listContacts();
					break;
				case 5:
					createConversation();
					break;
				default:
					running = false;
					this.mServer.updateUser(this.mUser.getName(), this.mUser);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * -------------------------- Main Methods -------------------------------
	 */

	private void addContact() {
		printContactMenu("add");
		String name = mConsole.readLine();
		this.mUser.getContacts().add(new Person(name));
	}

	private void removeContact() {
		printContactMenu("remove");
		String name = mConsole.readLine();
		this.mUser.getContacts().remove(name);
	}

	private void listContacts() {
		printContactMenu("list");
	}

	private void showConnectedContacts() throws RemoteException {
		String prompt = this.mServer.connectedList(this.mUser.getContacts());
		System.out.println(prompt);
	}

	private void createConversation() throws RemoteException {
		this.mCurrentConversation = new Conversation();
		this.isCoordinator = true;
		this.mParticipants = new HashMap<String, ChatClientIF>();
		printConversationMenu(this.isCoordinator);
		runConversation();
	}

	private void runConversation() throws RemoteException {
		Scanner sc = new Scanner(System.in);
		while (!this.hasToReply) {
			//String line = this.mConsole.readLine("> ");
			String line = sc.nextLine();
			if (hasToReply)
				break;
			if (line.equalsIgnoreCase("exit"))
				break;
			if (line.equalsIgnoreCase("help"))
				printConversationMenu(this.isCoordinator);
			if (line.startsWith("add(") && line.endsWith(")"))
				addParticipant(line.substring(4, line.length() - 1));
			else if (this.isCoordinator && line.startsWith("remove(")
					&& line.endsWith(")"))
				removeParticipant(line.substring(7, line.length() - 1));
			else
				send(new Message(line, this.mUser.getName()));
		}
		sc.close();
		this.mConsole = System.console();
	}

	/*
	 * -------------------- Methods for a Conversation ------------------------
	 */

	private synchronized void addParticipant(String name) throws RemoteException {
		ChatClientIF newParticipant = this.mServer.getClient(name);
		if (newParticipant != null) {
			boolean accepted = false;
			if (this.isCoordinator)
				accepted = newParticipant.replyInvitation((ChatClientIF)this,
						this.mUser.getName());
			else
				accepted = newParticipant.replyInvitation(this.mCoordinator,
						this.mUser.getName());
			if (accepted) {
				this.mCurrentConversation.addParticipant(new Person(name));
				this.mParticipants.put(name, newParticipant);
				newParticipant.enterConversation();
			} else {
				System.out.println("The user " + name
						+ " didn't accept the invitation");
			}
		} else {
			System.out.println("That user is not connected right now.");
		}
	}

	private void removeParticipant(String name) throws RemoteException {
		if (this.isCoordinator) {
			ChatClientIF c = this.mParticipants.get(name);
			if (c == null)
				return;
			String prompt = "You have been removed from the conversation.";
			Message m = new Message("Coordinator", prompt);
			c.receive(m);
		}
	}

	private void send(Message m) throws RemoteException {
		if (this.isCoordinator) {
			Iterator<ChatClientIF> it;
			for (it = this.mParticipants.values().iterator(); it.hasNext();) {
				ChatClientIF c = it.next();
				c.receive(m);
			}
		} else {
			this.mCoordinator.broadcast(m, this);
		}
	}

	/*
	 * ------------------------ Methods for Printing
	 * -----------------------------
	 */

	private void printConversationMenu(boolean coordinator) {
		String menu = "Welcome to the chat! \n" + "Enter exit to close \n"
				+ "Enter help to see this message again \n"
				+ "Enter add([name]) to add someone to the conversation \n";
		if (coordinator)
			menu += "Enter remove([name]) to remove someone from the conversation\n";
		menu += "Or enter anything else to send it as a message.";
		System.out.println(menu);
	}

	private void printContactMenu(String action) {
		if (action.equals("add"))
			System.out.println("Write the name of the new contact");
		else if (action.equals("remove"))
			System.out.println("Write the name of the contact to be removed");
		else
			System.out.println("This are your contacts \n"
					+ this.mUser.getContacts().toString());
	}

}
