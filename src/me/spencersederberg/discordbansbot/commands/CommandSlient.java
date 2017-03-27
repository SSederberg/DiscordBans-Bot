package me.spencersederberg.discordbansbot.commands;

import java.sql.SQLException;

import me.spencersederberg.discordbansbot.BanAPI;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandSlient extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message message = e.getMessage();
        
        if(message.getContent().startsWith("?slient")) {
            if(e.getMember().hasPermission(Permission.ADMINISTRATOR) || e.getMember().isOwner()) {
                try {
                    if(BanAPI.getAPI().allowSlientEntry(e.getGuild().getId())) {
                        e.getChannel().sendMessage("Currently, this guild **allows** users to bypass my entry warning!");
                    } else {
                        e.getChannel().sendMessage("Currently, this guild **does not allow** users to bypass my entry warning!");
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}
