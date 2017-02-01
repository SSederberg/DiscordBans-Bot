package me.spencersederberg.discordbansbot;

import javax.security.auth.login.LoginException;

import me.spencersederberg.discordbansbot.listeners.BanEvent;
import me.spencersederberg.discordbansbot.listeners.BotJoin;
import me.spencersederberg.discordbansbot.listeners.BotScan;
import me.spencersederberg.discordbansbot.listeners.UnbanEvent;
import me.spencersederberg.discordbansbot.listeners.UserJoin;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

public class DiscordBot implements EventListener {
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BanAPI api = new BanAPI();
		BanAPI.initDB();
		
		 try {
			 
			 JDA jda = new JDABuilder(AccountType.BOT)
				.addListener(new BanEvent())
				.addListener(new BotJoin())
				.addListener(new BotScan())
				.addListener(new UnbanEvent())
				.addListener(new UserJoin())
				.addListener(new Ping())
				.addListener(new DiscordBot())
				.setToken("MjYwMjg5MDc4ODk1ODM3MTg0.C1savQ.hTAyBhIy0HZpdujZR1VRb_54Rn0")
				.setGame(Game.of("BanHammer"))
				.setAutoReconnect(true)
				.setAudioEnabled(false)
				.setIdle(false)
				.setStatus(OnlineStatus.ONLINE)
				.buildBlocking();
		 	}
		 
	        catch (LoginException e) { e.printStackTrace(); }
		 
	        catch (InterruptedException e) { e.printStackTrace(); }
		 
	        catch (RateLimitedException e) { e.printStackTrace(); }
	}

	@Override
	public void onEvent(Event e) {
	
		if(e instanceof ReadyEvent) {
			System.out.println("DiscordBans is in a ready state!");
		}
		
	}
}
