package de.Herbystar.CBMFC.Commands;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandRELOAD extends Command {
	
	
	public CommandRELOAD(String name) {
		super(name);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if(args.length == 0) {
				if(pp.hasPermission("CBMFC.Reload")) {
					executeReloadCMD();
					pp.sendMessage(TextComponent.fromLegacyText(Main.instance.internalPrefix + "§aReload successful!"));
				} else {
					if(Main.instance.config.getBoolean("NoPermission.Enabled") == true) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("NoPermission.Message"))));
					}
				}
			}
		} else {
			executeReloadCMD();
		}

	}
	
	private void executeReloadCMD() {
		Main.instance.createConfig();
		Main.instance.getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(Main.instance.internalPrefix + "§aReload successful!"));
	}
}
