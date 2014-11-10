package hjhenriq.chat.client;

import java.rmi.Remote;

public interface ChatClientIF extends Remote{
	void retrieveAuthentication(boolean answer);
	void retrieveRegistration(boolean answer);
}
