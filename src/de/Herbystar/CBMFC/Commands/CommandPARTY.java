package de.Herbystar.CBMFC.Commands;

import java.util.HashMap;
import java.util.UUID;

import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Party;
import de.Herbystar.CBMFC.Utilities.Reflections;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandPARTY extends Command {
	
	public static HashMap<UUID, Party> playerParty = new HashMap<UUID, Party>();
	
	public CommandPARTY(String name) {
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
			
			if(args.length == 0) {
				for(String s : Main.instance.config.getStringList("Party.Help")) {
			        pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(s, pp)));
				}
			}
			
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("create")) {
					if(Party.hasParty(pp)) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.AlreadyCreated"), pp)));
					} else {
						new Party(pp);
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Created"), pp)));
					}
				}
				if(args[0].equalsIgnoreCase("leave")) {
					if(Party.hasParty(pp)) {
						Party p = Party.getParty(pp);
						p.removePlayer(pp);
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Leave"), pp)));
						p.getCreator().sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.LeaveInfo"), pp)));
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
					}
				}
				if(args[0].equalsIgnoreCase("dissolve")) {
					if(Party.hasParty(pp)) {
						if(Party.getParty(pp).isCreator(pp)) {
							Party.getParty(pp).removeParty();
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Remove"))));
						} else {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NotCreator"), pp)));
						}
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
					}
				}
				if(args[0].equalsIgnoreCase("have")) {
					if(Party.hasParty(pp)) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Have"), pp)));
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
					}
				}
				if(args[0].equalsIgnoreCase("join")) {
					if(CommandPARTY.playerParty.containsKey(pp.getUniqueId())) {
						if(Party.hasParty(pp)) {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.AlreadyInParty"), pp)));
						} else {
							((Party)CommandPARTY.playerParty.get(pp.getUniqueId())).addPlayer(pp);
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Join"), pp)));
							Party.getParty(pp).getCreator().sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.JoinInfo"), pp)));
						}
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoInvite"), pp)));
					}
				}
				if(args[0].equalsIgnoreCase("decline")) {
					if(CommandPARTY.playerParty.containsKey(pp.getUniqueId())) {
						if(Party.hasParty(pp)) {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.AlreadyInParty"), pp)));
						} else {
							((Party)CommandPARTY.playerParty.get(pp.getUniqueId())).getCreator().sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.DeclineInfo"), pp)));
							CommandPARTY.playerParty.remove(pp.getUniqueId());
						}
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoInvite"), pp)));
					}
				}
			}
			
			if(args.length == 2) {
				if(pp.getName().equals(args[1])) {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.YourSelf"), pp)));
					return;
				}
				if(args[0].equalsIgnoreCase("invite")) {
					if(Party.hasParty(pp)) {
						if(Party.getParty(pp).isCreator(pp)) {
							try {
								ProxiedPlayer ppInvited = Main.server.getPlayer(args[1]);
								if(!Party.hasParty(ppInvited)) {
									CommandPARTY.playerParty.put(ppInvited.getUniqueId(), Party.getParty(pp));
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Invite.Sent"), pp)));
									
									String[] buttons = new String[2];
									buttons[0] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.Accept.Text"), pp);
									buttons[1] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.Decline.Text"), pp);
									
									String[] hover = new String[2];
									hover[0] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.Accept.Hover"), pp);
									hover[1] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.Decline.Hover"), pp);
									
									String[] cmd = new String[2];
									cmd[0] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.Accept.Command"), pp);
									cmd[1] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.Decline.Command"), pp);
									
									String[] msg = new String[2];
									msg[0] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.BeforeButtons"), pp);
									msg[1] = ReplaceString.replace(Main.instance.config.getString("Party.Invite.AfterButtons"), pp);

									if(Main.instance.config.getBoolean("Party.Invite.Old.Use")) {
										ppInvited.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Invite.Old.Text"), pp)));
									} else {
										try {
											Reflections.sendHoverMessage(pp, ppInvited, msg, buttons, hover, cmd);
										} catch(Error e) {
											ppInvited.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Invite.Old.Text"), pp)));
										}
									}
								} else {
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.PlayerAlreadyInAParty"), pp)));
								}
							} catch(Error e) {
								pp.sendMessage(new TextComponent(Main.instance.prefix + "&cError"));
							}
						} else {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NotCreator"), pp)));
						}
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
					}
				}
				if(args[0].equalsIgnoreCase("kick")) {
					ProxiedPlayer ppKick = Main.server.getPlayer(args[1]);
					if(Party.hasParty(pp)) {
						if(Party.getParty(pp).isCreator(pp)) {
							try {
								Party.getParty(pp).removePlayer(ppKick);
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.PlayerKicked"), pp)));
								ppKick.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Kicked"), pp)));
							} catch(Error e) {
								pp.sendMessage(new TextComponent(Main.instance.prefix + "&cError"));
							}
						} else {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NotCreator"), pp)));
						}
					} else {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
					}
				}
			}
			
			if((args.length >= 2) && (args[0].equalsIgnoreCase("chat"))) {
				if(Party.hasParty(pp)) {
					String msg = "";
					for(int i = 1; i < args.length; i++) {
						msg = msg + " " + args[i];
					}
					String msgFinal = ReplaceString.replace(msg, pp);
					for(ProxiedPlayer ppMembers : Party.getParty(pp).getPlayers()) {
						ppMembers.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.Chat.Format"), pp).replace("[cbmfc-party-message]", msgFinal)));
					}
				} else {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(Main.instance.config.getString("Party.NoParty"), pp)));
				}
			} else if((args.length == 1) && (args[0].equalsIgnoreCase("chat"))) {
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
