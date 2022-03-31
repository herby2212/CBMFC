package de.Herbystar.CBMFC.Events;

import java.util.List;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Party;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatEventHandler implements Listener {
	
	@EventHandler
	public void onChatEvent(ChatEvent e) {
		ProxiedPlayer pp = (ProxiedPlayer) e.getSender();
		String msg = e.getMessage();
		String firstChar = String.valueOf(msg.charAt(0));

		if(Party.partyChat == true && !firstChar.equals("/") && !firstChar.equals("\"")) {
			if(Party.hasParty(pp)) {
				String msgFinal = ReplaceString.replace(msg, pp);
				for(ProxiedPlayer ppMembers : Party.getParty(pp).getPlayers()) {
					ppMembers.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Chat.Format"), pp).replace("[cbmfc-party-message]", msgFinal)));
				}
			} else {
				pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
			}
			e.setCancelled(true);
		}
		
		if(msg.startsWith(Main.instance.config.getString("GlobalChat.StartCharacter")) && Main.instance.config.getBoolean("GlobalChat.Enabled") == true) {
			String msgFinal = ReplaceString.replace(msg, pp);
			List<String> excludedServers = Main.instance.config.getStringList("GlobalChat.ExcludedServers");
			
			if(excludedServers.contains(pp.getServer().getInfo().getName())) {
				pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("GlobalChat.UsageOnExcludedServerDenied"))));
			} else {
				for(ProxiedPlayer player : Main.instance.getProxy().getPlayers()) {
					if(!player.isConnected()) {
						return;
					}
					//Filter excluded servers
					if(excludedServers.contains(player.getServer().getInfo().getName())) {
						return;
					}
					player.sendMessage(TextComponent.fromLegacyText(msgFinal));
				}
			}
			e.setCancelled(true);
		}
		
		if(Main.instance.config.getBoolean("VariablesInChat") == true) {
			e.setMessage(ReplaceString.replace(msg, pp, true));
		} else {
			e.setMessage(msg);
		}
	}
}
