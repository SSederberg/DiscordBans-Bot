package me.spencersederberg.discordbansbot.listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotScan extends ListenerAdapter {

	BanAPI api;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		ArrayList<String> offenders = new ArrayList<String>();
		ArrayList<String> members = new ArrayList<String>();
		
			if(e.getMessage().getContent().equals("!scan")) {
								
							e.getChannel().sendMessage(" :radioactive: Starting guild scan for potential offenders :radioactive: ").queue();
							
							// Scan start, create a ArrayList of Snowflake IDs of people in the Guild.
							for(Member member : e.getGuild().getMembers()) {
								members.add(member.getUser().getId());
								System.out.println("Added " + member.getEffectiveName() + " as " + member.getUser().getId());
							}
							
							// Put it against the known bans database, we just want to know.
							
							for(String s : members) {
								try {									
									BanAPI.openConnection();
									PreparedStatement data = BanAPI.con.prepareStatement("USE discordbans");
									data.executeQuery();
									PreparedStatement ps = BanAPI.con.prepareStatement("select discordID from bans where discordID = '" + s + "';");
									ps.executeQuery();
									ResultSet r = ps.getResultSet();
									if(r.next()) offenders.add(s);
									BanAPI.closeConnection();
									
								} catch (SQLException ex) { ex.printStackTrace(); }
							}
							
							//TODO: Convert Snowflake ID to a user
							if(offenders.isEmpty()) {
								e.getChannel().sendMessage("Lucky you! No offenders were detected in your guild!").queue();
							} else {
								e.getChannel().sendMessage("Here is the list of offenders: " + Arrays.toString(offenders.toArray())).queue();
								System.out.println("Here is the list of offenders: " + Arrays.toString(offenders.toArray()));
							}
						}		
					}
	
}
