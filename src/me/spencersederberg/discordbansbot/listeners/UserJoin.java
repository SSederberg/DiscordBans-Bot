package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;

import me.spencersederberg.discordbansbot.BanAPI;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserJoin extends ListenerAdapter {
	
	/**
	 * When a user joins a guild, DiscordBans-Bot checks for
	 * any past offenses, if there are any, the guild owner is 
	 * notified.
	 */
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		System.out.println("User Join Action Firing");
		BanAPI api = new BanAPI();
		
		String userName = e.getMember().getUser().getName(); // The name of the user that is joining the guild.
		String welcomeMessage = "Hello, " + userName + "! This guild is protected by DiscordBans, any ban detected by me will be logged and enforced on other servers protected by me."; //TODO: Addmore info about the bot //Represents the owner of the guild.
		Member owner = e.getGuild().getOwner();
		
		// Opens a private channel to tell them about how the 
		// guild is protected by DiscordBans-Bot
		e.getMember().getUser().openPrivateChannel().queue( message -> {
			
			message.sendMessage(welcomeMessage).queue();
		});
		
		// Warns the guild admins about the offender.	
			try {
				
				if(api.userExists(e.getMember().getUser().getId())) {
						
				  owner.getUser().openPrivateChannel().queue(
					message-> {
						try { 
							
							message.sendMessage(" Notice: The user " + e.getMember().getEffectiveName() 
									+ " in the guild " + e.getGuild().getName() + " has had previous bans detected!").queue();;
							
							message.sendMessage("This user has been banned " + BanAPI.getBanCount(e.getMember().getUser().getId()) 
							+ " times, and was last banned on " + api.getLastBanDate(e.getMember().getUser().getId())).queue();;
						} catch (SQLException ex) { ex.printStackTrace(); }
					}
				   );
				  
				}
			} catch (SQLException ex) { ex.printStackTrace(); }
		}
	}