package de.Herbystar.CBMFC.Events;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Commands.CommandWARTUNG;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerConnectEventHandler implements Listener {
	
	@EventHandler
	public void onServerConnectEvent(ServerConnectEvent e) {
		ProxiedPlayer pp = e.getPlayer();
		
		if(Main.instance.Wartung == true) {
			if(pp.hasPermission("CBMFC.Maintenance.Bypass") || CommandWARTUNG.maintenance_whitelist.contains(pp.getName())) {
				e.setCancelled(false);
			} else {
				e.setCancelled(true);
				pp.disconnect(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Maintenance.Join"), pp)));
			}
		}
	}

}
