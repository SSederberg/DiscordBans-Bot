package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserJoin extends ListenerAdapter {
	
	/**
	 * When a user joins a guild, DiscordBans-Bot checks for
	 * any past offenses, if there are any, the guild owner is 
	 * notified.
	 */
	@Override
	public synchronized void onGuildMemberJoin(GuildMemberJoinEvent e) {
		System.out.println("User Join Action Firing");
		
		String userName = e.getMember().getUser().getName(); // The name of the user that is joining the guild.
		String welcomeMessage = "Hello, " + userName + "! This guild is protected by me, DiscordBans! For more "
				+ "information about me, visit https://ssederberg.github.io/"; 
		Member owner = e.getGuild().getOwner();
		
		// Opens a private channel to tell them about how the 
		// guild is protected by DiscordBans-Bot if the guild
		// disallows slient entry.
		try {
            if(!BanAPI.getAPI().allowSlientEntry(e.getGuild().getId())) {
                e.getMember().getUser().openPrivateChannel().queue( message -> {
                    
                    message.sendMessage(welcomeMessage).queue();
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
		
		// Warns the guild admins about the offender.	
			try {
				
				if(BanAPI.getAPI().userExists(e.getMember().getUser().getId())) {
						
				  owner.getUser().openPrivateChannel().queue( // Sends a message to the owner of the guild telling them about the convict.
					message-> {
						EmbedBuilder eb = new EmbedBuilder();
						
						eb.setTitle("DiscordBans Alert: " + e.getMember().getEffectiveName(), "https://ssederberg.github.io");
						eb.setAuthor(e.getJDA().getSelfUser().getName(), "https://ssederberg.github.io", e.getJDA().getSelfUser().getAvatarUrl());
						eb.setDescription("This user has been banned from guilds in the past, here's a summary.");
						try { 
							
							eb.addField("Ban Count:", String.valueOf(BanAPI.getAPI().getBanCount(e.getMember().getUser().getId())), true); //Shows ban count
							eb.addField("Last Ban Date:", BanAPI.getAPI().getLastBanDate(e.getMember().getUser().getId()), true); // Shows last ban date
							
						} catch (SQLException ex) { ex.printStackTrace(); }
						
						message.sendMessage(eb.build()).queue();
						
					}
				   );
				  
				}
			} catch (SQLException ex) { ex.printStackTrace(); }
		}
	}