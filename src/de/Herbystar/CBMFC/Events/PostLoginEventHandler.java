package de.Herbystar.CBMFC.Events;

import java.util.List;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginEventHandler implements Listener {
	
	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer pp = e.getPlayer();
		List<String> header = Main.instance.config.getStringList("Tablist.Header");
		List<String> footer = Main.instance.config.getStringList("Tablist.Footer");
		Boolean enabled = Main.instance.config.getBoolean("Tablist.Enabled");
		String tab_header = "";
		String tab_footer = "";
		
		if(enabled == true) {
			pp.setTabHeader(TextComponent.fromLegacyText(createStringFromList(pp, tab_header, header)),
					TextComponent.fromLegacyText(createStringFromList(pp, tab_footer, footer)));
			
		}
	}
	
	private String createStringFromList(ProxiedPlayer pp, String finalString, List<String> stringCollection) {
		for(int i = 0; i < stringCollection.size(); i++) {
			String s = ReplaceString.replace(stringCollection.get(i), pp);
			if(i+1 == stringCollection.size()) {
				finalString = finalString + s;
			} else {
				finalString = finalString + s + "\n";
			}
		}
		return finalString;
	}

}
