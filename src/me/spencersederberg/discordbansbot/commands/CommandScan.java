package me.spencersederberg.discordbansbot.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandScan extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
	    
		if(e.getMessage().getContent().equals("?scan")) {
		    
			if(e.getMember().isOwner() || e.getMember().hasPermission(Permission.BAN_MEMBERS)) {
				//TODO: Make a better scan.
			}
		}
	}

}
	