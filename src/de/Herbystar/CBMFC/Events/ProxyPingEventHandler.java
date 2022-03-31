package de.Herbystar.CBMFC.Events;

import java.util.List;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import de.Herbystar.CBMFC.Main;
import de.Herbystar.CBMFC.Utilities.ReplaceString;

public class ProxyPingEventHandler implements Listener {
	
    private List<String> default_lines = Main.instance.config.getStringList("PlayerCountMessage");
    private List<String> maintenance_lines = Main.instance.config.getStringList("Maintenance.PlayerCountMessage");
    private int MOTD_rotation = 0;
    private String motd = "";

    public PlayerInfo[] createPlayerCountMessage(List<String> list) {
    	PlayerInfo[] sample = new PlayerInfo[list.size()];
    	for(int i = 0; i < sample.length; i++) {
    		sample[i] = new PlayerInfo(ReplaceString.replace(list.get(i)), "");
    	}
    	return sample;
    }
    
	@EventHandler
	public void onProxyPingEvent(ProxyPingEvent e) {
		ServerPing sp = e.getResponse();
		if(Main.instance.Wartung == true) {
			sp.setVersion(new Protocol(ReplaceString.replace(Main.instance.config.getString("Maintenance.Player")), 2));
			BaseComponent[] bc = new ComponentBuilder(ReplaceString.replace(Main.instance.config.getString("Maintenance.MOTD"))).create();
			sp.setDescriptionComponent(bc[0]);
			sp.getPlayers().setSample(createPlayerCountMessage(maintenance_lines));
			e.setResponse(sp);
		} else {
			if(Main.instance.config.getBoolean("FakePlayers.Enabled") == true) {
				int online = Main.server.getOnlineCount() + Main.instance.config.getInt("FakePlayers.Online.Count");
				int max = Main.instance.config.getInt("FakePlayers.Max");
				if(Main.instance.config.getBoolean("FakePlayers.Online.Enabled") == true) {
					sp.setPlayers(new Players(max, online, sp.getPlayers().getSample()));
				} else {
					sp.setPlayers(new Players(max, Main.server.getOnlineCount(), sp.getPlayers().getSample()));
				}
			}
			if(Main.instance.config.getBoolean("PlayerVersion.Enabled")) {
				sp.setVersion(new Protocol(ReplaceString.replace(Main.instance.config.getString("PlayerVersion.Text")), sp.getVersion().getProtocol() - 1));
			}
			if(Main.instance.config.getBoolean("MOTD.Enabled")) {
				triggerMOTDRotation();
				BaseComponent[] bc = new ComponentBuilder(ReplaceString.replace(motd)).create();
				sp.setDescriptionComponent(bc[0]);
				sp.getPlayers().setSample(createPlayerCountMessage(default_lines));
			}
		}
	}
	
	private void triggerMOTDRotation() {
		if(Main.instance.config.getString("MOTD.Text." + Integer.toString(MOTD_rotation)) != "") {
			motd = Main.instance.config.getString("MOTD.Text." + Integer.toString(MOTD_rotation));
			if(Main.instance.config.getBoolean("MOTD.SwitchOnRefresh") == true) {
				MOTD_rotation++;
			}
		} else {
			MOTD_rotation = 0;
			triggerMOTDRotation();
		}
	}
}
