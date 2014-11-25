package hjhenriq.chat.client;

import hjhenriq.chat.model.Conversation;
import hjhenriq.chat.model.Message;
import hjhenriq.chat.model.Person;
import hjhenriq.chat.model.User;
import hjhenriq.chat.server.ChatServerIF;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8214387253929191454L;
	private static final int NORMAL_MSG_FLAG = 0;
	private static final int ATTACHED_MSG_FLAG = 1;
	private static final int REMOVED_MSG_FLAG = 2;
	private static final int SYSTEM_MSG_FLAG = 3;
	private ChatServerIF mServer;
	private User mUser;
	private Console mConsole;
	private Conversation mCurrentConversation;
	private boolean isAuthenticated;
	private boolean isCoordinator;
	private boolean goBackToMain;
	private HashMap<String, ChatClientIF> mParticipants;
	private ChatClientIF mCoordinator;
	private HashMap<String, ChatClientIF> mPendingInvitations;
	private int filesCounter;

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
		this.mPendingInvitations = new HashMap<String, ChatClientIF>();
		this.goBackToMain = false;
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
		switch (m.getFlag()) {
		case NORMAL_MSG_FLAG:
			this.mCurrentConversation.addMessage(m);
			break;
		case ATTACHED_MSG_FLAG:
			try {
				recvFile(m.getText());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		case REMOVED_MSG_FLAG:
			this.mCoordinator = null;
			this.mCurrentConversation = null;
			this.goBackToMain = true;
			break;
		}
		if (m.getFlag() == ATTACHED_MSG_FLAG) {
			System.out.println("");
		} else {
			System.out.println(m.toString());
		}
	}

	@Override
	public synchronized void broadcast(Message m, String initiator)
			throws RemoteException {
		this.mCurrentConversation.addMessage(m);
		Iterator<ChatClientIF> it;
		for (it = this.mParticipants.values().iterator(); it.hasNext();) {
			ChatClientIF c = it.next();
			if (!c.getName().equals(initiator))
				c.receive(m);
		}
		System.out.println(m.toString());

	}

	@Override
	public synchronized void recvInvitation(ChatClientIF c, String name)
			throws RemoteException {
		String prompt = "You have been invited to a new conversation by "
				+ name + ".";
		System.out.println(prompt);
		this.mPendingInvitations.put(name, c);
	}

	@Override
	public synchronized void accepted(String name, ChatClientIF c)
			throws RemoteException {
		if (this.mCurrentConversation != null)
			this.mParticipants.put(name, c);

	}

	@Override
	public void leftConversation(String name) throws RemoteException {
		String prompt = name + " has left the conversation.";
		Message m = new Message(prompt, "Notice");
		send(m);
		this.mParticipants.remove(name);
	}

	@Override
	public void newCoordinator(HashMap<String, ChatClientIF> participants)
			throws RemoteException {
		this.isCoordinator = true;
		this.mParticipants = participants;
	}

	@Override
	public void newCoordinator(ChatClientIF coord) throws RemoteException {
		this.mCoordinator = coord;
	};

	@Override
	public String getName() throws RemoteException {
		if (this.isAuthenticated)
			return this.mUser.getName();
		else
			return null;
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
		this.goBackToMain = false;
		System.out.println("Running");
		String menu = "Enter: \n" + "[0] to exit\n"
				+ "[1] to add a new contact\n" + "[2] to remove a contact\n"
				+ "[3] to show connected contacts\n" + "[4] to list contacts\n"
				+ "[5] to create a conversation\n"
				+ "[6] to list pending invitations to conversations\n";

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
					showConnectedContacts();
					break;
				case 4:
					listContacts();
					break;
				case 5:
					createConversation();
					break;
				case 6:
					listPendingConversations();
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

	private void createConversation() throws IOException {
		this.mCurrentConversation = new Conversation();
		this.isCoordinator = true;
		this.mParticipants = new HashMap<String, ChatClientIF>();
		printConversationMenu(this.isCoordinator);
		runConversation();
	}

	private void listPendingConversations() throws IOException {
		printPendingConvsMenu();
		int option = Integer.parseInt(this.mConsole.readLine());
		int numInvitations = this.mPendingInvitations.size();
		if (numInvitations * 2 < option || numInvitations == 0
				|| (numInvitations == 1) && (option == 2))
			return;
		int index = option % 2;
		boolean accept = (option % 2 == 0);
		ChatClientIF c = (ChatClientIF) this.mPendingInvitations.values()
				.toArray()[index];
		this.mPendingInvitations.remove(c.getName());
		if (accept)
			accept(c);
		else
			decline(c);
	}

	private void accept(ChatClientIF c) throws IOException {
		String s = " has accepted your invitation.";
		Message m = new Message(this.getName() + s, "Notice");
		m.setFlag(SYSTEM_MSG_FLAG);
		c.receive(m);
		c.accepted(this.mUser.getName(), (ChatClientIF) this);
		this.mCoordinator = c;
		this.mCurrentConversation = new Conversation();
		this.goBackToMain = false;
		printConversationMenu(this.isCoordinator);
		runConversation();
	}

	private void decline(ChatClientIF c) throws RemoteException {
		String s = " has declined your invitation.";
		Message m = new Message(this.mUser.getName() + s, "Notice");
		m.setFlag(SYSTEM_MSG_FLAG);
		c.receive(m);
	}

	private void runConversation() throws IOException {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while (!this.goBackToMain && sc.hasNextLine()) {
			// String line = this.mConsole.readLine("> ");
			String line = sc.nextLine();
			if (line.equalsIgnoreCase("exit")) {
				assignNewCoordinator();
				this.goBackToMain = true;
				break;
			}
			if (line.equalsIgnoreCase("help"))
				printConversationMenu(this.isCoordinator);
			else if (line.startsWith("add(") && line.endsWith(")"))
				invite(line.substring(4, line.length() - 1));
			else if (this.isCoordinator && line.startsWith("remove(")
					&& line.endsWith(")"))
				removeParticipant(line.substring(7, line.length() - 1));
			else if (line.startsWith("list -n ")) {
				int n = Integer.parseInt(line.split(" ")[2]);
				String s = this.mCurrentConversation.listMessages(n);
				System.out.println(s);
			} else if (line.startsWith("attach("))
				attach(line.substring(7, line.length() - 1));
			else
				send(new Message(line, this.mUser.getName()));
		}
		this.isCoordinator = false;
		this.mCurrentConversation = null;
		this.mParticipants = null;
	}

	/*
	 * -------------------- Methods for a Conversation ------------------------
	 */

	private synchronized void assignNewCoordinator() throws RemoteException {
		if (this.isCoordinator) {
			String prompt = this.getName() + " has left the conversation.";
			Message m = new Message(prompt, "Notice");
			if (!this.mParticipants.isEmpty()) {
				this.send(m);
				this.isCoordinator = false;
				int counter = 0;
				ChatClientIF newCoord = null;
				String newCoordName = null;
				for (Map.Entry<String, ChatClientIF> entry : 
					this.mParticipants.entrySet()) {
					String key = entry.getKey();
					ChatClientIF value = entry.getValue();
					if (counter == 0) {
						newCoord = value;
						newCoordName = key;
						counter++;
					} else {
						value.newCoordinator(newCoord);
					}
				}	
				this.mParticipants.remove(newCoordName);
				newCoord.newCoordinator(this.mParticipants);			
			}
		} else {
			this.mCoordinator.leftConversation(this.getName());
		}
	}

	private synchronized void invite(String name) throws RemoteException {
		ChatClientIF newParticipant = this.mServer.getClient(name);
		if (newParticipant != null) {
			newParticipant.recvInvitation((ChatClientIF) this,
					this.mUser.getName());
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
			Message m = new Message(prompt, "Coordinator");
			m.setFlag(REMOVED_MSG_FLAG);
			c.receive(m);
			this.mParticipants.remove(name);
		}
	}
	
	private void attach(String filename) throws IOException {
		String text = readFile(filename, Charset.defaultCharset());
		Message m = new Message(text, this.getName());
		m.setFlag(ATTACHED_MSG_FLAG);
		send(m);
	}

	private void send(Message m) throws RemoteException {
		if (m.getFlag() == NORMAL_MSG_FLAG)
			this.mCurrentConversation.addMessage(m);
		if (this.isCoordinator) {
			Iterator<ChatClientIF> it;
			for (it = this.mParticipants.values().iterator(); it.hasNext();) {
				ChatClientIF c = it.next();
				c.receive(m);
			}
		} else {
			this.mCoordinator.broadcast(m, this.getName());
		}
	}

	/*
	 * -------------------- Methods for Printing -------------------------
	 */

	private void printConversationMenu(boolean coordinator) {
		String menu = "Welcome to the chat! \n" + "Enter exit to close \n"
				+ "Enter help to see this message again \n"
				+ "Enter add([name]) to add someone to the conversation \n";
		if (coordinator)
			menu += "Enter remove([name])"
					+ "to remove someone from the conversation\n";
		menu += "Enter list -n [number of messages] to list last n messages \n";
		menu += "Enter attach([filename]) to attach a text file";
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

	private void printPendingConvsMenu() {
		String menu = "Enter your option for the pending invitations: \n";
		Iterator<String> it = this.mPendingInvitations.keySet().iterator();
		int counter = 0;
		for (; it.hasNext();) {

			String name = it.next();
			menu += name + "\n[" + counter + "] accept ";
			counter++;
			menu += "[" + counter + "] decline\n";
			counter++;
		}
		if (counter > 2)
			menu += "[" + counter + "] decline all\n";
		counter++;
		menu += "[" + counter + "] exit";
		System.out.println(menu);

	}
	
	// -----------------------  Utilities  --------------------------------------
	private String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	private synchronized void recvFile(String text) throws FileNotFoundException {
		String filename = this.filesCounter + ".txt";
		String s = "You have received a new file, it has been downloaded as "
				+ filename;
		System.out.println(s);
		PrintWriter pw = new PrintWriter(filename);
		pw.print(text);
		pw.close();
	}
}
