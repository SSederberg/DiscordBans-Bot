package me.spencersederberg.discordbansbot;

import java.sql.SQLException;

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
		
		if(e.getMessage().getContent().equals("!list")) {
			
			try { api.testDB(e.getMember().getEffectiveName());
			
			} catch (SQLException | NullPointerException ex) { ex.printStackTrace(); }
			
			}	
		}
	}
