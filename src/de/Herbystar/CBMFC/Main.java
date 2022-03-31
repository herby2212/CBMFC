package de.Herbystar.CBMFC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Charsets;

import de.Herbystar.CBMFC.Commands.CommandFRIEND;
import de.Herbystar.CBMFC.Commands.CommandMSG;
import de.Herbystar.CBMFC.Commands.CommandPARTY;
import de.Herbystar.CBMFC.Commands.CommandPartyChat;
import de.Herbystar.CBMFC.Commands.CommandRELOAD;
import de.Herbystar.CBMFC.Commands.CommandREPLY;
import de.Herbystar.CBMFC.Commands.CommandWARTUNG;
import de.Herbystar.CBMFC.Events.ChatEventHandler;
import de.Herbystar.CBMFC.Events.PlayerDisconnectEventHandler;
import de.Herbystar.CBMFC.Events.PostLoginEventHandler;
import de.Herbystar.CBMFC.Events.ProxyPingEventHandler;
import de.Herbystar.CBMFC.Events.ServerConnectEventHandler;
import de.Herbystar.CBMFC.Events.ServerSwitchEventHandler;
import de.Herbystar.CBMFC.Utilities.ReplaceString;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
	
	public String prefix = "";
	public String internalPrefix = "§c[§6CBMFC§c] ";
	public boolean Wartung;
	public Configuration config = null;
	public static Main instance;
	public static ProxyServer server = null;


	@Override
	public void onEnable() {
		instance = this;
		Wartung = false;
		server = this.getProxy();
		server.broadcast(TextComponent.fromLegacyText("§c[§6CBMFC§c] " + "§bVersion: §c" + this.getDescription().getVersion() + " §aenabled!"));
		
		createConfig();
		getCommands();
		registerEvents();
		CommandFRIEND.loadFriends();
		CommandWARTUNG.loadMaintenanceData();

		server.broadcast(TextComponent.fromLegacyText("§c[§6CBMFC§c] §aConfiguration generated!"));
		prefix = ReplaceString.replace(config.getString("Prefix"));
	}

	
	@Override
	public void onDisable() {
		server.broadcast(TextComponent.fromLegacyText("§c[§6CBMFC§c] " + "§bVersion: §c" + this.getDescription().getVersion() + " §cdisabled!"));
		CommandFRIEND.saveFriends();
	}

	private void getCommands() {
		if(this.config.getBoolean("Commands.Message") == true) {
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMSG("msg"));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMSG("w"));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandMSG("message"));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandREPLY("reply"));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandREPLY("r"));
		}
		if(this.config.getBoolean("Commands.Party") == true) {
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandPARTY("party"));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandPartyChat("pc"));
		}
		if(this.config.getBoolean("Commands.Friends") == true) {
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandFRIEND("friend"));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandFRIEND("friends"));
		}
		//ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandTEAM("Team", (this)));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandWARTUNG("maintenance"));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandRELOAD("gblreload"));
		//ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandREGELN("Regeln", (this)));
		//ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandSPENDEN("Spenden", (this)));
		//ProxyServer.getInstance().getPluginManager().registerCommand(this, new CommandVOTE("Vote", (this)));
	}
	
	public void saveConfig(Configuration config, File file) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void registerEvents() {
		server.getPluginManager().registerListener(instance, new ProxyPingEventHandler());
//		server.getPluginManager().registerListener(instance, new PreLoginEventHandler());
		server.getPluginManager().registerListener(instance, new ChatEventHandler());
		server.getPluginManager().registerListener(instance, new ServerConnectEventHandler());
		server.getPluginManager().registerListener(instance, new PlayerDisconnectEventHandler());
		server.getPluginManager().registerListener(instance, new ServerSwitchEventHandler());
		server.getPluginManager().registerListener(instance, new PostLoginEventHandler());
	}
	
	public int getMaxPlayers() {
		int maxPlayers = 0;
		try {
			try {
				for(ListenerInfo listener : server.getConfigurationAdapter().getListeners()) {
					maxPlayers = listener.getMaxPlayers();
					break;
				}
			} catch(ConcurrentModificationException | NullPointerException ex) {
				server.getConsole().sendMessage(TextComponent.fromLegacyText("§c[§6CBMFC§c] &7- §cWarning: Error in getMaxPlayers method using fallback!"));
				Iterator<ListenerInfo> listener = server.getConfigurationAdapter().getListeners().iterator();
				while(listener.hasNext()) {
					maxPlayers = listener.next().getMaxPlayers();
					break;
				}
			}
		} catch(Exception ex) {
			//If both methods above fail skip the maxPlayer variable to prevent further errors in global plugin functions.
		}
		return maxPlayers;
	}
	
	public void createConfig() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File file = new File(getDataFolder().getPath(), "config.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.config = loadConfig(file);
		
	    List<String> default_lines = Arrays.asList("&7<0><0><0><0><0><0><0><0><0><0><0><0><0><0><0>",
	    		"&7<0> &6&lTest Player Count &7<0>",
	    		"&7<0><0><0><0><0><0><0><0><0><0><0><0><0><0><0>");
	    List<String> maintenance_lines = Arrays.asList("&7<0><0><0><0><0><0><0><0><0><0><0><0><0><0>",
	    		"&7<0> &c&l<10> Maintenance <10> &7<0>",
	    		"&7<0><0><0><0><0><0><0><0><0><0><0><0><0><0>");
		
	    List<String> disabledServersDefault = Arrays.asList(
	    		"NameOfDisabledServer");
	    
	    List<String> friendList_header = Arrays.asList(
	    		"&c[]=========== &2&lFriends &c===========[]",
	    		"");
	    
	    List<String> friendList_footer = Arrays.asList(
	    		"",
	    		"&c[]=========== &2&lFriends &c===========[]");
	    
	    List<String> friend_help = Arrays.asList(
	    		"&c[]=================== [&2&lFriends&c] &c===================[]",
	    		"&c/friend &elist &7<9><9> &aShow all your friends",
	    		"&c/friend &eadd &b<player> &7<9><9> &aSend a friend invite to a player",
	    		"&c/friend &eremove &b<player> &7<9><9> &aRemove a friend from your list",
	    		"&c/friend &eaccept &b<player> &7<9><9> &aAccept a friend invite",
	    		"&c/friend &edeny &b<player> &7<9><9> &aDeny a friend invite",
	    		"&c/friend &ejoin &b<player> &7<9><9> &aJoins a friends server (if joinable)",
	    		"&c[]=================== [&2&lFriends&c] &c===================[]");
	    
	    List<String> party_help = Arrays.asList(
	    		"",
	    		"&c[]=================== [&b&lParty&c] &c===================[]",
	    		"&c/party create &7<9><9> &acreate a party",
	    		"&c/party invite &e<player name> &7<9><9> &ainvite a player to your party",
	    		"&c/party have &7<9><9> &acheck if you're in a party",
	    		"&c/party join &7<9><9> &ajoin the invited party",
	    		"&c/party decline &7<9><9> &adecline the party invite",
	    		"&c/party leave &7<9><9> &aleave your current party",
	    		"&c/party dissolve &7<9><9> &aremove your party",
	    		"&c/party kick &e<player> &7<9><9> &aremove a player from your party",
	    		"&c/party chat &e<message> &7<9><9> &asend a message to all party members",
	    		"&c&o/pc &e&o<message> &7&o<9><9> &a&oalias for &c&o/party chat &e&o<message>",
	    		"",
	    		"&e- The party host need to join a server!",
	    		"&e- Only the creator/host of the party can invite or",
	    		"&e  remove players from the party!",
	    		"&c[]=================== [&b&lParty&c] &c===================[]");
	    
	    List<String> tabheader = Arrays.asList(
	    		"&c[&6&lCBMFC&c] &8| &a&lWorks fine&e!",
	    		"&6Also with multiple lines!",
	    		"",
	    		"&7&oThis header is visible to &7&o&lall",
	    		"&7&oservers on the network!",
	    		"");
	    
	    List<String> tabfooter = Arrays.asList(
	    		"",
	    		"&2&lExample &e&lNetwork &8&l| &c&oJoin Now!",
	    		"",
	    		"&6IP: &8<9> &aYourNetwork.com",
	    		"",
	    		"&b&lSkype&7: ...");
	    
	    List<String> GChatExcludedServers = Arrays.asList(
	    		"excluded_example_server_name");
	    
	    setObject(config, "Prefix", "&e<3> &cBungeeCord&7-&aServer &e<3> &6<9> ");
	    setObject(config, "VariablesInChat", true);
		setObject(config, "NoPermission.Message", "[cbmfc-prefix]&c&lYou don''t have the right permission!");
		setObject(config, "NoPermission.Enabled", true);
		setObject(config, "FakePlayers.Enabled", true);
		setObject(config, "FakePlayers.Online.Enabled", true);
		setObject(config, "FakePlayers.Online.Count", 75);
		setObject(config, "FakePlayers.Max", 250);
		setObject(config, "PlayerVersion.Enabled", false);
		setObject(config, "PlayerVersion.Text", "&7[cbmfc-proxy-players-online]&8/&7[cbmfc-proxy-players-max]");
		try {
			setObject(config, "MOTD.Enabled", true);
			setObject(config, "MOTD.SwitchOnRefresh", false);
			setObject(config, "MOTD.Text.0", "&e<3> &a&lYourServer&7&l.&a&lnet&e <3> &7| &e&oMinigame Network &7 &4[&c1.8-1.14&4][cbmfc-nextline]&c&lNew Gamemodes!: &aGame1 &7- &cGame2 &7- &eGame3");
		} catch(Exception ex) {
			server.broadcast(TextComponent.fromLegacyText("§c[§6CBMFC§c] §cYou need to regenerate the MOTD line in your config.yml!"));
		}
		setObject(config, "PlayerCountMessage", default_lines);
		setObject(config, "Maintenance.MOTD", "&e<3> &a&lYourServer&7&l.&a&lnet&e <3> &7| &c&lMaintenance [cbmfc-nextline]     &6<9> &a&lServer Performence Update!");
		setObject(config, "Maintenance.Player", "&c&lMaintenance");
		setObject(config, "Maintenance.Join", "[cbmfc-prefix]&cThe server is in maintenance mode! [cbmfc-nextline] &a&lTry it again in some minutes!");
		setObject(config, "Maintenance.PlayerCountMessage", maintenance_lines);
		setObject(config, "MessageSystem.Enabled", true);
		setObject(config, "MessageSystem.Disabled", "[cbmfc-prefix]&cThe network wide message system is currently disabled!");
		setObject(config, "MessageSystem.NotOnline", "[cbmfc-prefix]&cThe player &e[player-off] &cis not online!");
		setObject(config, "MessageSystem.Syntax", "&e[player-sender] &8-> &a[player-receiver]&8: &7[message]");
		setObject(config, "MessageSystem.Monitor.Enabled", true);
		setObject(config, "MessageSystem.Monitor.Text", "&e[player-sender] &8send &a[player-receiver] &8following private: &7&8[message]");
		setObject(config, "MessageSystem.DisplayName", true);
		setObject(config, "MessageSystem.Reply.NoMessage", "[cbmfc-prefix]&cYou have no messages to reply to!");
		setObject(config, "GlobalChat.Enabled", true);
		setObject(config, "GlobalChat.StartCharacter", "!");
		setObject(config, "GlobalChat.ExcludedServers", GChatExcludedServers);
		setObject(config, "GlobalChat.UsageOnExcludedServerDenied", "&cYou can't use this feature right now!");
		setObject(config, "Party.Help", party_help);
		setObject(config, "Party.JoinedServer", "&6[&bParty&6] &aThe party joined a server!");
		setObject(config, "Party.JoinWithAllMembers", true);
		setObject(config, "Party.Dissolved", "&6[&bParty&6] &cParty dissolved by &e[cbmfc-party-creator-name]!");
		setObject(config, "Party.NoParty", "&6[&bParty&6] &cYou have no party!");
		setObject(config, "Party.Created", "&6[&bParty&6] &aParty successfully created! [cbmfc-nextline]Use /party invite &e<playername> &ato invite players to your party!");
		setObject(config, "Party.AlreadyCreated", "&6[&bParty&6] &cA party was already created!");
		setObject(config, "Party.Remove", "&6[&bParty&6] &aYour party got removed!");
		setObject(config, "Party.NotCreator", "&6[&bParty&6] &cYou're not the creator of this party!");
		setObject(config, "Party.Have", "&6[&bParty&6] &eYou're in the party of &6[cbmfc-party-creator-name]&e!");
		setObject(config, "Party.Leave", "&6[&bParty&6] &cYou left the party!");
		setObject(config, "Party.LeaveInfo", "&6[&bParty&6] &eThe player &6[cbmfc-player-name] &cleft &eyour party!");
		setObject(config, "Party.Join", "&6[&bParty&6] &aYou joined the party of &6[cbmfc-party-creator-name]&a!");
		setObject(config, "Party.JoinInfo", "&6[&bParty&6] &eThe player &6[cbmfc-player-name] &ajoined &eyour party!");
		setObject(config, "Party.DeclineInfo", "&6[&bParty&6] &eThe player &6[cbmfc-player-name] &cdeclined &eyour invite!");
		setObject(config, "Party.AlreadyInParty", "&6[&bParty&6] &cYou're already in a party!");
		setObject(config, "Party.NoInvite", "&6[&bParty&6] &cYou did not receive any invite!");
		setObject(config, "Party.Invite.Sent", "&6[&bParty&6] &aInvite sent!");
		setObject(config, "Party.Invite.Accept.Text", "&2[Accept <13>]");
		setObject(config, "Party.Invite.Accept.Hover", "&2Click here to accept the invite!");
		setObject(config, "Party.Invite.Accept.Command", "/party join");
		setObject(config, "Party.Invite.Decline.Text", "&4[Deny <14>]");
		setObject(config, "Party.Invite.Decline.Hover", "&4Click here to deny the invite!");
		setObject(config, "Party.Invite.Decline.Command", "/party decline");
		setObject(config, "Party.Invite.BeforeButtons", "&6[&bParty&6] &aYou got invited by &6[cbmfc-player-name] &ato join his party! [cbmfc-nextline] &aUse ");
		setObject(config, "Party.Invite.AfterButtons", " &a to join the party!");
		setObject(config, "Party.Invite.Old.Use", false);
		setObject(config, "Party.Invite.Old.Text", "&6[&bParty&6] &aYou got invited by &6[cbmfc-player-name] &ato join his party! [cbmfc-nextline]&aUse &e/party join &ato accept the invite");
		setObject(config, "Party.PlayerAlreadyInAParty", "&6[&bParty&6] &cThis player is already in a party!");
		setObject(config, "Party.Kicked", "&6[&bParty&6] &6[cbmfc-player-name] &ckicked you out of the party!");
		setObject(config, "Party.PlayerKicked", "&6[&bParty&6] &aThe player got kicked!");
		setObject(config, "Party.Chat.Format", "&6[&bParty&6] &c[cbmfc-player-displayname] &8<9> &f[cbmfc-party-message]");
		setObject(config, "Party.Chat.Enabled", "&6[&bParty&6] &eParty only chat &aenabled &e!");
		setObject(config, "Party.Chat.Disabled", "&6[&bParty&6] &eParty only chat &cdisabled &e!");
		setObject(config, "Party.NoChatMessage", "&6[&bParty&6] &cNo chat message defined!");
		setObject(config, "Party.DisabledServers", disabledServersDefault);
		setObject(config, "Friend.Help", friend_help);
		setObject(config, "Friend.FriendList.Header", friendList_header);
		setObject(config, "Friend.FriendList.Footer", friendList_footer);
		setObject(config, "Friend.FriendList.Syntax.Online", "&7<9><9> &e[cbmfc-player-friend] &7 - &aOnline");
		setObject(config, "Friend.FriendList.Syntax.Offline", "&7<9><9> &e[cbmfc-player-friend] &7 - &cOffline");
		setObject(config, "Friend.FriendList.Syntax.NoFriend", "&7<9><9> &cNo friends");
		setObject(config, "Friend.UserNotOnline", "&c[&2Friend&c] &cUser not online!");
		setObject(config, "Friend.AlreadyFriends", "&c[&2Friend&c] &cThis user is already your friend!");
		setObject(config, "Friend.NoFriends", "&c[&2Friend&c] &cYou don't have any friends at the moment");
		setObject(config, "Friend.NotFriends", "&c[&2Friend&c] &cYou and the invited user are no friends!");
		setObject(config, "Friend.Removed", "&c[&2Friend&c] &eFriend &cremoved!");
		setObject(config, "Friend.NoFriendInvite", "&c[&2Friend&c] &cYou got no friend invite by that player!");
		setObject(config, "Friend.Invite.Sender", "&c[&2Friend&c] &aFriend invite successfully sent!");
		setObject(config, "Friend.Invite.Receiver", "&c[&2Friend&c] &eYou received a friend invite by &6[cbmfc-player-name]");
		setObject(config, "Friend.Invite.Pending", "&c[&2Friend&c] &cUser already invited, waiting for answer!");
		setObject(config, "Friend.Invite.Removed", "&c[&2Friend&c] &cOutstanding friend invite for that player removed!");
		setObject(config, "Friend.Accept", "&c[&2Friend&c] &eYou &aaccepted &ethe friend invite!");
		setObject(config, "Friend.Accept_Info", "&c[&2Friend&c] &6[cbmfc-player-name] &aaccepted &eyour friend invite!");
		setObject(config, "Friend.Deny", "&c[&2Friend&c] &eYou &cdenied &ethe friend invite!");
		setObject(config, "Friend.Deny_Info", "&c[&2Friend&c] &6[cbmfc-player-name] &cdenied &eyour friend invite!");
		setObject(config, "Friend.Server_Join.Try", "&c[&2Friend&c] &eTrying to join a friend's server");
		setObject(config, "Friend.Server_Join.Error", "&c[&2Friend&c] &cConnecting to friend's server failed!");
		setObject(config, "Friend.DisabledServers", disabledServersDefault);
		setObject(config, "Tablist.Enabled", true);
		setObject(config, "Tablist.Header", tabheader);
		setObject(config, "Tablist.Footer", tabfooter);
		setObject(config, "Commands.Message", true);
		setObject(config, "Commands.Party", true);
		setObject(config, "Commands.Friends", true);
		saveConfig(config, file);
	}
	
	public void setString(Configuration config, String path, Object value) {
		if(config == null) {
			return;
		}
		if(config.getString(path) == "") {
			config.set(path, value);
		}
	}
	
	public static void setObject(Configuration config, String path, Object value) {
		if(config == null) {
			return;
		}
		if(value == null) {
			return;
		}
		if(value instanceof Boolean) {
			if(!config.contains(path)) {
				config.set(path, value);
			}
		} else {
			if(config.get(path) == null) {
				config.set(path, value);
			}
		}
	}	

	public Configuration loadConfig(File file) {
		Configuration config = null;
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}
}
