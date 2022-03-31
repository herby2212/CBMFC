package de.Herbystar.CBMFC.Events;

import java.util.Map;
import java.util.UUID;

import de.Herbystar.CBMFC.Party;
import de.Herbystar.CBMFC.Commands.CommandPARTY;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectEventHandler implements Listener {
	
	@EventHandler
	public void onPlayerDisconnectEvent(PlayerDisconnectEvent e) {
		ProxiedPlayer pp = e.getPlayer();
		if((Party.hasParty(pp)) && (Party.getParty(pp).isCreator(pp))) {
			
			//Removes all pending party invites
			for(Map.Entry<UUID, Party> entry : CommandPARTY.playerParty.entrySet()) {
				if(Party.getParty(pp) == entry.getValue()) {
					CommandPARTY.playerParty.remove(entry.getKey());
				}
			}
			
			Party.getParty(pp).removeParty();
		}
	}

}
