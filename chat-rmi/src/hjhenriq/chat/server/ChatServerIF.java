package hjhenriq.chat.server;

import hjhenriq.chat.client.ChatClientIF;

public interface ChatServerIF {
	void authenticate(ChatClientIF chatClient, String name, String pass);
	boolean register(ChatClientIF chatClient, String name);
	void register(ChatClientIF chatClient, String name, String pass);
}
