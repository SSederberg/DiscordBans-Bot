package me.spencersederberg.discordbansbot.listeners;

import java.sql.SQLException;
import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BanEvent extends ListenerAdapter {

	BanAPI api = new BanAPI();
	
	/*
	 * When a Discord Guild admin bans a user via the client, this event is 
	 * triggered.
	 */
	public void onBan(GuildBanEvent e) throws SQLException {
		
		System.out.println("Ban Action Firing"); // Makes sure the event fired.
		
		api.addBan(e.getUser().getName(), e.getUser().getId(), e.getGuild().getId()); //Adds ban to database
		
	}
}
