package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UserJoin extends ListenerAdapter {
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		System.out.println("User Join Action Firing");
		BanAPI api = new BanAPI();
		
		String guildName = e.getGuild().getName();
		String userName = e.getMember().getUser().getName();
		String welcomeMessage = "Hello, " + userName + "! This guild is protected by DiscordBans, any ban detected by me will be logged and enforced on other servers protected by me."; //TODO: Addmore info about the bot
		
		e.getMember().getUser().openPrivateChannel().queue( message -> {
			
			message.sendMessage(welcomeMessage).queue();
		});
		
		e.getMember().getUser().getPrivateChannel().sendMessage("Welcome to " + guildName + ", " + userName + "! \n " + welcomeMessage);
		
		try {
			
			if(api.userExists(e.getMember().getUser().getId())) {
				
			    for(Member user : e.getGuild().getMembers()) {
			    	
			    	if(user == e.getGuild().getOwner()) { //TODO: Make it seeable for admins
			    		
			    		user.getUser().getPrivateChannel().sendMessage(" Notice: The user " + e.getMember().getEffectiveName() + " in the guild " + e.getGuild().getName() + " has had previous bans detected!").queue();
			    		
			    		try {
							
			    			user.getUser().getPrivateChannel().sendMessage("This user has been banned " + BanAPI.getBanCount(e.getMember().getUser().getId()) + " times, and was last banned on " + api.getLastBanDate(e.getMember().getUser().getId())).queue();
						} catch (SQLException ex) { ex.printStackTrace(); }
			    	}
			    }
			}
		} catch (SQLException ex) { ex.printStackTrace(); }
	}
	
	
}
