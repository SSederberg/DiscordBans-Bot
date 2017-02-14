package me.spencersederberg.discordbansbot.listeners;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotScan extends ListenerAdapter {

	BanAPI api;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getMessage().getContent().equals("?scan")) {
			if(e.getMember().isOwner()) {
				//TODO: Make a better scan.
			}
		}
	}

}
	