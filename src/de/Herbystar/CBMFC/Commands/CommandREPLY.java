package de.Herbystar.CBMFC.Commands;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class CommandREPLY extends Command {
		
	public CommandREPLY(String name) {
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
					if(!CommandMSG.reply_buffer.containsKey(pp.getUniqueId())) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.Reply.NoMessage"), pp)));
						return;
					}
					//e = Empfänger
					UUID old_sender = CommandMSG.reply_buffer.get(pp.getUniqueId());
					ProxiedPlayer e = ProxyServer.getInstance().getPlayer(old_sender);
					if(e == null) {				
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("MessageSystem.NotOnline"), pp).replace("[player-off]", args[0])));
						return;
					}
					String msg = "";
					for(int i = 0; i < args.length; i++) {
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
					CommandMSG.reply_buffer.remove(pp.getUniqueId());
					
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
