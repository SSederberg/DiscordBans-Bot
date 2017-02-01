package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;
import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BanEvent extends ListenerAdapter {

	BanAPI api = new BanAPI();
	
	public void onBan(GuildBanEvent e) throws SQLException {
		
		System.out.println("Ban Action Firing");
		api.addBan(e.getUser().getName(), e.getUser().getId(), e.getGuild().getId());
		
	}
}
