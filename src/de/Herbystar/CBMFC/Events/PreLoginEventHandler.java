package de.Herbystar.CBMFC.Events;

import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Commands.CommandWARTUNG;
import de.Herbystar.CBMFC.Utilities.ReplaceString;

public class PreLoginEventHandler implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLoginEvent(PreLoginEvent e) {
		if(Main.instance.Wartung == true) {
			if(CommandWARTUNG.maintenance_whitelist.contains(e.getConnection().getName())) {
				e.setCancelled(false);
			} else {
				e.setCancelled(true);
				e.setCancelReason(ReplaceString.replace(Main.instance.config.getString("Maintenance.Join")));
			}
		}
	}

}
