package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotJoin extends ListenerAdapter {

	/**
	 * When the bot joins a guild, it will automatically start a scan for potential offenders the 
	 * administrators are not aware of.
	 */
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		BanAPI  api = new BanAPI ();
		System.out.println("Join Action Firing");
		
		for(Member member : e.getGuild().getMembers()) {
			
			try {
				
				if(api.userExists(member.getUser().getId())) { // TODO: Make users with the Administrators permission get this message.
					
					e.getGuild().getOwner().getUser().openPrivateChannel().queue();
					e.getGuild().getOwner().getUser().getPrivateChannel()
					.sendMessage("Notice: The user " + member.getEffectiveName() + " in the guild " + e.getGuild().getName() + " has had previous bans detected!").queue();
					
					e.getGuild().getOwner().getUser().getPrivateChannel()
					.sendMessage("This user has been banned " + BanAPI.getBanCount(member.getUser().getId()) + " times, and was last banned on " + api.getLastBanDate(member.getUser().getId())).queue();
				}
				
			} catch (SQLException ex) { ex.printStackTrace(); }
		}
	}
}