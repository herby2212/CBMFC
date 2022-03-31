package de.Herbystar.CBMFC.Commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import de.Herbystar.CBMFC.Utilities.UUIDFetcher;

public class CommandFRIEND extends Command {
	
	//<sender, receiver>
	public HashMap<UUID, List<UUID>> pending = new HashMap<UUID, List<UUID>>();
	//<request_sender, request_receiver>
	public static HashMap<UUID, List<UUID>> friends;
	
	public static Configuration data_set = null;
	
	public CommandFRIEND(String name) {
		super(name);
	}
	List<String> friendList_header = Main.instance.config.getStringList("Friend.FriendList.Header");
	List<String> friendList_footer = Main.instance.config.getStringList("Friend.FriendList.Footer");
	String friendList_Syntax_Online = Main.instance.config.getString("Friend.FriendList.Syntax.Online");
	String friendList_Syntax_Offline = Main.instance.config.getString("Friend.FriendList.Syntax.Offline");
	String friendList_noFriend = Main.instance.config.getString("Friend.FriendList.Syntax.NoFriend");
	List<String> friend_help = Main.instance.config.getStringList("Friend.Help");
	String NoPermission = Main.instance.config.getString("NoPermission.Message");
	String alreadyFriends = Main.instance.config.getString("Friend.AlreadyFriends");
	String userNotOnline = Main.instance.config.getString("Friend.UserNotOnline");
	String friend_removed = Main.instance.config.getString("Friend.Removed");
	String friend_invite_sender = Main.instance.config.getString("Friend.Invite.Sender");
	String friend_invite_receiver = Main.instance.config.getString("Friend.Invite.Receiver");
	String friend_invite_pending = Main.instance.config.getString("Friend.Invite.Pending");
	String friend_invite_removed = Main.instance.config.getString("Friend.Invite.Removed");
	String no_friend_invite = Main.instance.config.getString("Friend.NoFriendInvite");
	String notFriends = Main.instance.config.getString("Friend.NotFriends");
	String friend_accept = Main.instance.config.getString("Friend.Accept");
	String friend_accept_info = Main.instance.config.getString("Friend.Accept_Info");
	String friend_deny = Main.instance.config.getString("Friend.Deny");
	String friend_deny_info = Main.instance.config.getString("Friend.Deny_Info");
	String friend_server_join = Main.instance.config.getString("Friend.Server_Join.Try");
	String friend_server_fails = Main.instance.config.getString("Friend.Server_Join.Error");
	String noFriends = Main.instance.config.getString("Friend.NoFriends");
	
	
	public static void loadFriends() {
		if(!Main.instance.getDataFolder().exists()) {
			Main.instance.getDataFolder().mkdir();
		}
		File data = new File(Main.instance.getDataFolder().getPath(), "friends_data.yml");
		if(!data.exists()) {
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			data_set = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(data), Charsets.UTF_8));
			
			friends = new HashMap<UUID, List<UUID>>();
			for(String l : data_set.getStringList("Friends")) {
				String[] uuids = l.split(",");
				UUID friend_1 = UUID.fromString(uuids[0]);
				UUID friend_2 = UUID.fromString(uuids[1]);
				if(friends.containsKey(friend_1)) {
					List<UUID> friends_temp = friends.get(friend_1);
					friends_temp.add(friend_2);
					friends.put(friend_1, friends_temp);
				} else {
					List<UUID> friends_temp = new ArrayList<UUID>();
					friends_temp.add(friend_2);
					friends.put(friend_1, friends_temp);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFriends() {
		if(!Main.instance.getDataFolder().exists()) {
			Main.instance.getDataFolder().mkdir();
		}
		File data = new File(Main.instance.getDataFolder().getPath(), "friends_data.yml");
		if(!data.exists()) {
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> pairs = new ArrayList<String>();
		for(UUID uuidKeys : friends.keySet()) {
			for(UUID uuidValue : friends.get(uuidKeys)) {
				pairs.add(uuidKeys.toString() + "," + uuidValue.toString());	
			}
		}
		data_set.set("Friends", pairs);
		Main.instance.saveConfig(data_set, data);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			
			if(Main.instance.config.getStringList("Friend.DisabledServers").contains(pp.getServer().getInfo().getName())) {
				return;
			}
			
			if(!pp.hasPermission("CBMFC.Friend")) {
				if(Main.instance.config.getBoolean("NoPermission.Enabled") == true) {
					pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(NoPermission, pp)));
				}
				return;
			}
			
			if(args.length == 0) {
				for(String s : friend_help) {
			        pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(s, pp)));
				}
			}
			
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("list")) {
					List<String> friend_list = new ArrayList<String>();
					for(String s : friendList_header) {
						friend_list.add(ReplaceString.replace(s, pp));
					}
					try {
						if(friends.get(pp.getUniqueId()).size() == 0) {
							friend_list.add(ReplaceString.replace(friendList_noFriend, pp));
						} else {
							for(UUID plUUID : friends.get(pp.getUniqueId())) {
								try {
									ProxiedPlayer pl = Main.server.getPlayer(plUUID);
									if(pl.isConnected() == true) {
										friend_list.add(ReplaceString.replace(friendList_Syntax_Online, pp).replace("[cbmfc-player-friend]", pl.getName()));
									}
								} catch(Exception ex) {
									String offlineFriendName = UUIDFetcher.getName(plUUID);
									friend_list.add(ReplaceString.replace(friendList_Syntax_Offline, pp).replace("[cbmfc-player-friend]", offlineFriendName));
								}
							}
						}
					} catch(NullPointerException ex) {
						friend_list.add(ReplaceString.replace(friendList_noFriend, pp));
					}
					for(String s : friendList_footer) {
						friend_list.add(ReplaceString.replace(s, pp));
					}
					for(String s : friend_list) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(s)));
					}
				}
			}
			
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("join")) {
					ProxiedPlayer ppTarget = null;
					try {
						ppTarget = Main.server.getPlayer(args[1]);
					} catch(Exception ex) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(userNotOnline, pp)));
						return;
					}
					if(this.pending.containsKey(pp.getUniqueId())) {
						if(this.pending.get(pp.getUniqueId()).contains(ppTarget.getUniqueId())) {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_pending, pp)));
						} else {
							if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
								if(CommandFRIEND.friends.get(pp.getUniqueId()).contains(ppTarget.getUniqueId())) {
									try {
										pp.connect(ppTarget.getServer().getInfo());
										pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_server_join, pp)));
									} catch(Exception ex) {
										pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_server_fails, pp)));
									}
								} else {
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(notFriends, pp)));
								}
							} else {
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(noFriends, pp)));
							}
						}
					} else {
						if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
							if(CommandFRIEND.friends.get(pp.getUniqueId()).contains(ppTarget.getUniqueId())) {
								try {
									pp.connect(ppTarget.getServer().getInfo());
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_server_join, pp)));
								} catch(Exception ex) {
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_server_fails, pp)));
								}
							} else {
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(notFriends, pp)));
							}
						} else {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(noFriends, pp)));
						}
					}
				}
				/*
				 * Test command
				 * 
				if(args[0].equalsIgnoreCase("setFriend")) {
					UUID uuid = UUIDFetcher.getUUID(args[1]);
					if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
						if(!CommandFRIEND.friends.get(pp.getUniqueId()).contains(uuid)) {
							List<UUID> friend = CommandFRIEND.friends.get(pp.getUniqueId());
							friend.add(uuid);
							CommandFRIEND.friends.put(pp.getUniqueId(), friend);
						}
					} else {
						ArrayList<UUID> friend = new ArrayList<UUID>();
						friend.add(uuid);
						CommandFRIEND.friends.put(pp.getUniqueId(), friend);
					}
				}
				*/
				if(args[0].equalsIgnoreCase("add")) {
					ProxiedPlayer ppInvited = Main.server.getPlayer(args[1]);
					if(ppInvited == null) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(userNotOnline, pp)));
						return;
					}
					if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
						if(!CommandFRIEND.friends.get(pp.getUniqueId()).contains(ppInvited.getUniqueId())) {
							if(this.pending.containsKey(pp.getUniqueId())) {
								if(!this.pending.get(pp.getUniqueId()).contains(ppInvited.getUniqueId())) {
									List<UUID> friends_temp = this.pending.get(pp.getUniqueId());
									friends_temp.add(ppInvited.getUniqueId());
									this.pending.put(pp.getUniqueId(), friends_temp);
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_sender, pp)));
									ppInvited.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_receiver, pp)));
								} else {
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_pending, pp)));
								}
							} else {
								List<UUID> friends_temp = new ArrayList<UUID>();
								friends_temp.add(ppInvited.getUniqueId());
								this.pending.put(pp.getUniqueId(), friends_temp);
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_sender, pp)));
								ppInvited.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_receiver, pp)));
							}
						} else {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(alreadyFriends, pp)));
						}
					} else {
						if(this.pending.containsKey(pp.getUniqueId())) {
							if(!this.pending.get(pp.getUniqueId()).contains(ppInvited.getUniqueId())) {
								List<UUID> friends_temp = this.pending.get(pp.getUniqueId());
								friends_temp.add(ppInvited.getUniqueId());
								this.pending.put(pp.getUniqueId(), friends_temp);
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_sender, pp)));
								ppInvited.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_receiver, pp)));
							} else {
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_pending, pp)));
							}
						} else {
							List<UUID> friends_temp = new ArrayList<UUID>();
							friends_temp.add(ppInvited.getUniqueId());
							this.pending.put(pp.getUniqueId(), friends_temp);
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_sender, pp)));
							ppInvited.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_receiver, pp)));
						}
					}
				}
				if(args[0].equalsIgnoreCase("remove")) {
					UUID uuidPpOffline = null;
					if(Main.server.getPlayer(args[1]) == null) {
						uuidPpOffline = UUIDFetcher.getUUID(args[1]);
					} else {
						uuidPpOffline = Main.server.getPlayer(args[1]).getUniqueId();
					}
					if(this.pending.containsKey(pp.getUniqueId())) {
						if(!this.pending.get(pp.getUniqueId()).contains(uuidPpOffline)) {
							if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
								if(CommandFRIEND.friends.get(pp.getUniqueId()).contains(uuidPpOffline)) {
									CommandFRIEND.friends.get(pp.getUniqueId()).remove(uuidPpOffline);
									CommandFRIEND.friends.get(uuidPpOffline).remove(pp.getUniqueId());
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_removed, pp)));
								} else {
									pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(notFriends, pp)));
								}
							} else {
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(noFriends, pp)));
							}
						} else {
							this.pending.get(pp.getUniqueId()).remove(uuidPpOffline);
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_invite_removed, pp)));
						}
					} else {
						if(CommandFRIEND.friends.containsKey(pp.getUniqueId())) {
							if(CommandFRIEND.friends.get(pp.getUniqueId()).contains(uuidPpOffline)) {
								CommandFRIEND.friends.get(pp.getUniqueId()).remove(uuidPpOffline);
								CommandFRIEND.friends.get(uuidPpOffline).remove(pp.getUniqueId());
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_removed, pp)));
							} else {
								pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(notFriends, pp)));
							}
						} else {
							pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(noFriends, pp)));
						}
					}
					CommandFRIEND.saveFriends();
				}
				
				if(args[0].equalsIgnoreCase("accept")) {
					ProxiedPlayer ppSender = Main.server.getPlayer(args[1]);
					if(ppSender == null) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(userNotOnline, pp)));
						return;
					}
					ProxiedPlayer ppReceiver = pp;
					if(CommandFRIEND.friends.containsKey(ppReceiver.getUniqueId())) {
						if(!CommandFRIEND.friends.get(ppReceiver.getUniqueId()).contains(ppSender.getUniqueId())) {
							if(this.pending.containsKey(ppSender.getUniqueId())) {
								if(this.pending.get(ppSender.getUniqueId()).contains(ppReceiver.getUniqueId())) {
									this.pending.get(ppSender.getUniqueId()).remove(ppReceiver.getUniqueId());
									if(CommandFRIEND.friends.containsKey(ppSender.getUniqueId())) {
										List<UUID> friends_sender = CommandFRIEND.friends.get(ppSender.getUniqueId());
										friends_sender.add(ppReceiver.getUniqueId());
										CommandFRIEND.friends.put(ppSender.getUniqueId(), friends_sender);
									} else {
										List<UUID> friends_sender = new ArrayList<UUID>();
										friends_sender.add(ppReceiver.getUniqueId());
										CommandFRIEND.friends.put(ppSender.getUniqueId(), friends_sender);
									}
									ppSender.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_accept_info, ppReceiver)));
									List<UUID> friends_receiver = CommandFRIEND.friends.get(ppReceiver.getUniqueId());
									friends_receiver.add(ppSender.getUniqueId());
									CommandFRIEND.friends.put(ppReceiver.getUniqueId(), friends_receiver);
									ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_accept, ppReceiver)));
								} else {
									ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
								}
							} else {
								ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
							}
						} else {
							ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(alreadyFriends, ppReceiver)));
						}
					} else {
						if(this.pending.containsKey(ppSender.getUniqueId())) {
							if(this.pending.get(ppSender.getUniqueId()).contains(ppReceiver.getUniqueId())) {
								this.pending.get(ppSender.getUniqueId()).remove(ppReceiver.getUniqueId());
								if(CommandFRIEND.friends.containsKey(ppSender.getUniqueId())) {
									List<UUID> friends_sender = CommandFRIEND.friends.get(ppSender.getUniqueId());
									friends_sender.add(ppReceiver.getUniqueId());
									CommandFRIEND.friends.put(ppSender.getUniqueId(), friends_sender);
								} else {
									List<UUID> friends_sender = new ArrayList<UUID>();
									friends_sender.add(ppReceiver.getUniqueId());
									CommandFRIEND.friends.put(ppSender.getUniqueId(), friends_sender);
								}
								ppSender.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_accept_info, ppReceiver)));
								List<UUID> friends_receiver = new ArrayList<UUID>();
								friends_receiver.add(ppSender.getUniqueId());
								CommandFRIEND.friends.put(ppReceiver.getUniqueId(), friends_receiver);
								ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_accept, ppReceiver)));
							} else {
								ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
							}
						} else {
							ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
						}
					}
					CommandFRIEND.saveFriends();
				}
				
				if(args[0].equalsIgnoreCase("deny")) {
					ProxiedPlayer ppSender = Main.server.getPlayer(args[1]);
					if(ppSender == null) {
						pp.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(userNotOnline, pp)));
						return;
					}
					ProxiedPlayer ppReceiver = pp;
					if(CommandFRIEND.friends.containsKey(ppReceiver.getUniqueId())) {
						if(!CommandFRIEND.friends.get(ppReceiver.getUniqueId()).contains(ppSender.getUniqueId())) {
							if(this.pending.containsKey(ppSender.getUniqueId())) {
								if(this.pending.get(ppSender.getUniqueId()).contains(ppReceiver.getUniqueId())) {
									this.pending.get(ppSender.getUniqueId()).remove(ppReceiver.getUniqueId());
									ppSender.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_deny_info, ppReceiver)));
									ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_deny, ppReceiver)));
								} else {
									ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
								}
							} else {
								ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
							}
						} else {
							ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(alreadyFriends, ppReceiver)));
						}
					} else {
						if(this.pending.containsKey(ppSender.getUniqueId())) {
							if(this.pending.get(ppSender.getUniqueId()).contains(ppReceiver.getUniqueId())) {
								this.pending.get(ppSender.getUniqueId()).remove(ppReceiver.getUniqueId());
								ppSender.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_deny_info, ppReceiver)));
								ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(friend_deny, ppReceiver)));
							} else {
								ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
							}
						} else {
							ppReceiver.sendMessage(TextComponent.fromLegacyText(ReplaceString.replace(no_friend_invite, ppReceiver)));
						}
					}
				}
			}
		}
	}

}
