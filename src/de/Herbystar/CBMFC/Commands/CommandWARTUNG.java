package de.Herbystar.CBMFC.Commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Charsets;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class CommandWARTUNG extends Command {
	
	public static List<String> maintenance_whitelist = new ArrayList<String>();
	
	private static Configuration data_set = null;
	
	public CommandWARTUNG(String name) {
		super(name);
	}
	
	public static void loadMaintenanceData() {
		if(!Main.instance.getDataFolder().exists()) {
			Main.instance.getDataFolder().mkdir();
		}
		File data = new File(Main.instance.getDataFolder().getPath(), "maintenance_data.yml");
		if(!data.exists()) {
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			data_set = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(data), Charsets.UTF_8));
			
			Main.instance.Wartung = data_set.getBoolean("RestartSave");
			
			for(String l : data_set.getStringList("Whitelist")) {
				if(!maintenance_whitelist.contains(l)) {
					maintenance_whitelist.add(l);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveMaintenanceData() {
		if(!Main.instance.getDataFolder().exists()) {
			Main.instance.getDataFolder().mkdir();
		}
		File data = new File(Main.instance.getDataFolder().getPath(), "maintenance_data.yml");
		if(!data.exists()) {
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		data_set.set("RestartSave", Main.instance.Wartung);
		data_set.set("Whitelist", maintenance_whitelist);
		Main.instance.saveConfig(data_set, data);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if(pp.hasPermission("CBMFC.Maintenance")) {
				if(args.length == 0) {
					if(Main.instance.Wartung == false) {
						Main.instance.Wartung = true;
						saveMaintenanceData();
						pp.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §c§lMaintenance Mode §2enabled!"));						
					} else {
						Main.instance.Wartung = false;
						saveMaintenanceData();
						pp.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §c§lMaintenance Mode §4disabled!"));
					}
				}
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("add")) {
						if(!maintenance_whitelist.contains(args[1])) {
							maintenance_whitelist.add(args[1]);
							saveMaintenanceData();
							pp.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §ePlayer §c" + args[1] + "§e added to maintenance whitelist!"));
						} else {
							pp.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §cPlayer is already on maintenance whitelist!"));
						}
					}
					if(args[0].equalsIgnoreCase("remove")) {
						if(maintenance_whitelist.contains(args[1])) {
							maintenance_whitelist.remove(args[1]);
							saveMaintenanceData();
							pp.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §ePlayer §c" + args[1] + "§e removed from maintenance whitelist!"));						
						} else {
							pp.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §cPlayer is not on the maintenance whitelist!"));		
						}
					}
				}
			} else {
				if(Main.instance.config.getBoolean("NoPermission.Enabled") == true) {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("NoPermission.Message"))));
				}
			}
		} else {
			if(args.length == 0) {
				if(Main.instance.Wartung == false) {
					Main.instance.Wartung = true;
					saveMaintenanceData();
					sender.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §c§lMaintenance Mode §2enabled!"));
				} else {
					Main.instance.Wartung = false;			
					saveMaintenanceData();
					sender.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §c§lMaintenance Mode §4disabled!"));
				}
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("add")) {
					if(!maintenance_whitelist.contains(args[1])) {
						maintenance_whitelist.add(args[1]);
						saveMaintenanceData();
						sender.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §ePlayer §c" + args[1] + "§e added to maintenance whitelist!"));
					} else {
						sender.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §cPlayer is already on maintenance whitelist!"));
					}
				}
				if(args[0].equalsIgnoreCase("remove")) {
					if(maintenance_whitelist.contains(args[1])) {
						maintenance_whitelist.remove(args[1]);
						saveMaintenanceData();
						sender.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §ePlayer §c" + args[1] + "§e removed from maintenance whitelist!"));						
					} else {
						sender.sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] §cPlayer is not on the maintenance whitelist!"));		
					}
				}
			}
		}

	}

}
