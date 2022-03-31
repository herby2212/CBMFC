package de.Herbystar.CBMFC.Events;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Party;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchEventHandler implements Listener {

	@EventHandler
	public void onServerSwitchEvent(ServerSwitchEvent e) {
		ProxiedPlayer pp = e.getPlayer();
		
		if((Party.hasParty(pp)) && (Party.getParty(pp).isCreator(pp))) {
			if(Main.instance.config.getBoolean("Party.JoinWithAllMembers") == true) {
				for(ProxiedPlayer ppMembers : Party.getParty(pp).getPlayers()) {
					ppMembers.connect(pp.getServer().getInfo());
					ppMembers.sendMessage(new TextComponent(ReplaceString.replace(Main.instance.config.getString("Party.JoinedServer"), pp)));
				}
			}
		}
	}
}
