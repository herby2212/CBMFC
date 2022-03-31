package de.Herbystar.CBMFC.Commands;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Party;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandPartyChat extends Command {
		
	public CommandPartyChat(String name) {
		super(name);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			
			if(Main.instance.config.getStringList("Party.DisabledServers").contains(pp.getServer().getInfo().getName())) {
				return;
			}
			
			if(!pp.hasPermission("CBMFC.Party")) {
				if(Main.instance.config.getBoolean("NoPermission.Enabled") == true) {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("NoPermission.Message"))));
				}
				return;
			}
						
			if((args.length >= 1)) {
				if(Party.hasParty(pp)) {
					String msg = "";
					for(int i = 0; i < args.length; i++) {
						msg = msg + " " + args[i];
					}
					String msgFinal = ReplaceString.replace(msg, pp);
					for(ProxiedPlayer ppMembers : Party.getParty(pp).getPlayers()) {
						ppMembers.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Chat.Format"), pp).replace("[cbmfc-party-message]", msgFinal)));
					}
				} else {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
				}
			} else {
				if(Party.hasParty(pp)) {
					if(Party.partyChat == false) {
						Party.partyChat = true;
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Chat.Enabled"), pp)));
					} else {
						Party.partyChat = false;
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Chat.Disabled"), pp)));
					}
				} else {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
				}
				//pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoChatMessage"), pp)));
			}
		}
	}

}
