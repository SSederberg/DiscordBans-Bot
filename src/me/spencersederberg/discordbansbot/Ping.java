package me.spencersederberg.discordbansbot;


import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

// Purely a test class
public class Ping extends ListenerAdapter {

	public BanAPI api; 
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getMessage().getContent().equals("!ping")) {
			
			e.getChannel().sendMessage("Pong! I think!...").queue();
		}
	}
}
