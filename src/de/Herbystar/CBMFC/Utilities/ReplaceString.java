package de.Herbystar.CBMFC.Utilities;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Party;
import de.Herbystar.CBMFC.Commands.CommandFRIEND;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReplaceString {
	
	@SuppressWarnings("deprecation")
	public static String replace(String string, ProxiedPlayer pp, Boolean chatEvent) {
		String n = string;
		
		/*
		 * Symbols
		 */
		if(chatEvent == false) {
			n = n.replace('&', '§');
		}
		n = n.replace("<0>", "█").
		replace("<1>", "❤").
		replace("<2>", "☀").
		replace("<3>", "✯").
		replace("<4>", "☢").
		replace("<5>", "☎").
		replace("<6>", "♫").
		replace("<7>", "❄").
		replace("<8>", "«").
		replace("<9>", "»").
		replace("<10>", "Ω").
		replace("<11>", "☠").
		replace("<12>", "☣").
		replace("<13>", "✔").
		replace("<14>", "✖");
		
		n = n.replace("[cbmfc-nextline]", "\n");	
		n = n.replace("[cbmfc-prefix]", Main.instance.prefix);
				
		/*
		 * System/Proxy informations
		 */
		n = n.replace("[cbmfc-proxy-name]", Main.server.getName());
		n = n.replace("[cbmfc-proxy-players-online]", Integer.toString(Main.server.getOnlineCount()));
		n = n.replace("[cbmfc-proxy-players-max]", Integer.toString(Main.instance.getMaxPlayers()));
		n = n.replace("[cbmfc-proxy-gameversion]", Main.server.getGameVersion().toString());
		n = n.replace("[cbmfc-proxy-version]", Main.server.getVersion().toString());
		
		/*
		 * Player
		 */		
		if(pp == null) {
			return n;
		}
		n = n.replace("[cbmfc-player-name]", pp.getName());
		n = n.replace("[cbmfc-player-displayname]", pp.getDisplayName());
		
		
		/*
		 * Party
		 */
		if(Party.hasParty(pp)) {
			Party p = Party.getParty(pp);
			n = n.replace("[cbmfc-party-size]", Integer.toString(p.getPlayers().size()));
			n = n.replace("[cbmfc-party-creator-name]", p.getCreator().getName());
			n = n.replace("[cbmfc-party-creator-displayname]", p.getCreator().getDisplayName());
		}
		
		/*
		 * Friends
		 */
		if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
			n = n.replace("[cbmfc-friends]", Integer.toString(CommandFRIEND.friends.get(pp.getUniqueId()).size()));
		}
		
		return n;
	}
	
	public static String replace(String string) {
		return replace(string, null, false);		
	}
	
	public static String replace(String string, ProxiedPlayer pp) {
		return replace(string, pp, false);		
	}

}
