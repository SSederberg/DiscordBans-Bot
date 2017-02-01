package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;
import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class UnbanEvent extends ListenerAdapter {

	BanAPI api = new BanAPI();
	
	@Override
	public void onGuildUnban(GuildUnbanEvent e) {
		System.out.println("UnBan Action Firing");
		try {
			api.removeBan(e.getUser().getId(), e.getGuild().getId());
			System.out.println("The following user was unbanned: " + e.getUser().getId());
		} catch (SQLException ex) { 
			ex.printStackTrace();
		}
		
	}
}
