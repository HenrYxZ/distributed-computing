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
	private ChatServerIF server;

	public ChatClient(ChatServerIF chatServer) throws RemoteException{
		this.server = chatServer;
	}
	
	public void sendNewUser() {
		Scanner sc = new Scanner(System.in);
		boolean validName = false;
		String name = "default";
		while(!validName) {
			System.out.println("Enter new name: ");
			name = sc.nextLine();
			validName = this.server.register(this, name);
			if (!validName)
				System.out.println("Name already used.");
		}
		System.out.println("Enter new password: ");
		String pass = sc.nextLine();
		sc.close();
		this.server.register(this, name, pass);
	}
	
	public void authenticateNamePass() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your name: ");
		String name =  sc.nextLine();
		System.out.println("Enter your password: ");
		String pass = sc.nextLine();
		sc.close();
		this.server.authenticate(this, name, pass);	
	}
	
	public void enterToChat() {
		Scanner sc = new Scanner(System.in);
		while(true) {
			String menu = "Hello! Choose one option:\n"
						+ "[1] Create new user."
						+ "[2] Sign in with existing user"
						+ "[0] Exit";
			System.out.println(menu);
			try {
				switch (System.in.read()) {
					case 1: sendNewUser();
							break;
					case 2: authenticateNamePass();
							break;
					default: break;
					
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		sc.close();
	}

	@Override
	public void retrieveAuthentication(boolean answer) {
		if(answer == true) {
			run();
		} else {
			System.out.println("Wrong user or password, please try again.");
		}
	}

	@Override
	public void retrieveRegistration(boolean answer) {
		// TODO Auto-generated method stub
		
	}
	
	public void run() {
		
	}

}
