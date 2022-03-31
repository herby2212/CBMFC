package de.Herbystar.CBMFC.Commands;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.UUID;

public class CommandMSG extends Command {
	
	
	public static HashMap<UUID, UUID> reply_buffer = new HashMap<UUID, UUID>();
	
	public CommandMSG(String name) {
		super(name);
	}
	
	@Override
	@SuppressWarnings({})
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			
			if(Main.instance.config.getBoolean("MessageSystem.Enabled") == false) {
				pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Disabled"), pp)));
				return;
			}
			
			if(args.length > 1) {
				if(pp.hasPermission("CBMFC.Message")) {
					//e = Empfänger
					ProxiedPlayer e = ProxyServer.getInstance().getPlayer(args[0]);
					if(e == null) {				
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.NotOnline"), pp).replace("[player-off]", args[0])));
						return;
					}
					String msg = "";
					for(int i = 1; i < args.length; i++) {
						msg = msg + " " + args[i];
					}
					msg = ReplaceString.replace(msg);
					if(Main.instance.config.getBoolean("MessageSystem.DisplayName") == true) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Syntax"), pp).replace("[player-sender]", pp.getDisplayName()).replace("[player-receiver]", e.getDisplayName()).replace("[message]", msg)));
						e.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Syntax"), e).replace("[player-sender]", e.getDisplayName()).replace("[player-receiver]", pp.getDisplayName()).replace("[message]", msg)));					
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Syntax"), pp).replace("[player-sender]", pp.getName()).replace("[player-receiver]", e.getName()).replace("[message]", msg)));
						e.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Syntax"), e).replace("[player-sender]", e.getName()).replace("[player-receiver]", pp.getName()).replace("[message]", msg)));					
					}
					if(reply_buffer.containsKey(e.getUniqueId())) {
						reply_buffer.remove(e.getUniqueId());
					}
					reply_buffer.put(e.getUniqueId(), pp.getUniqueId());
					
					if(Main.instance.config.getBoolean("MessageSystem.Monitor.Enabled") == true) {
						for(ProxiedPlayer all : Main.server.getPlayers()) {
							if(all != null && all.isConnected()) {
								if(all.hasPermission("CBMFC.Message.Monitor")) {
									if(Main.instance.config.getBoolean("MessageSystem.DisplayName") == true) {
										all.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Monitor.Text")).replace("[player-sender]", pp.getDisplayName()).replace("[player-receiver]", e.getDisplayName()).replace("[message]", msg)));
									} else {
										all.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Monitor.Text")).replace("[player-sender]", pp.getName()).replace("[player-receiver]", e.getName()).replace("[message]", msg)));
									}
								}
							}
						}	
					}
				} else {
					if(Main.instance.config.getBoolean("NoPermission.Enabled") == true) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("NoPermission.Message"))));
					}
				}
	
			}
		}
	}

}
