package me.spencersederberg.discordbansbot.commands;

import java.sql.SQLException;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandLookup extends ListenerAdapter {
    
	/**
	 * If an Administrator or Owner is suspicious user, they can quickly query
	 * for an answer. 
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message message = e.getMessage();
		
		if(message.getContent().startsWith("?lookup")) {
		    
			if(e.getMember().isOwner() || e.getMember().hasPermission(Permission.BAN_MEMBERS)) {
			    
				if(!message.getMentionedUsers().isEmpty()) {
				    
					for(User user : message.getMentionedUsers()) {
					    
						try {
						    
							EmbedBuilder eb = new EmbedBuilder();
							if(BanAPI.getAPI().userExists(user.getId())) {
								
								eb.setAuthor(e.getJDA().getSelfUser().getName(), "https://ssederberg.github.io", e.getJDA().getSelfUser().getAvatarUrl());
								eb.setDescription("This user has been banned from guilds in the past, here's a summary.");
								try { 
									
									eb.addField("Ban Count:", String.valueOf(BanAPI.getAPI().getBanCount(e.getMember().getUser().getId())), true); //Shows ban count
									eb.addField("Last Ban Date:", BanAPI.getAPI().getLastBanDate(e.getMember().getUser().getId()), true); // Shows last ban date
									
								} catch (SQLException ex) { ex.printStackTrace(); }
								
							} else {
								e.getChannel().sendMessage("The user requested was not found!");
							}
							message.getChannel().sendMessage(eb.build()).queue();
						} catch (SQLException ex) { ex.printStackTrace(); }
					}
				} else {
				    e.getChannel().sendMessage("No (mentioned) users were specified in your request please try again!");
				}
			}
		}
	}

}
