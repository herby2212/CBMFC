package de.Herbystar.CBMFC;

import java.util.ArrayList;
import java.util.UUID;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import de.Herbystar.CBMFC.Utilities.ReplaceString;

public class Party {
	
	public static boolean partyChat = false;
	
	private static ArrayList<Party> PartyPlayer = new ArrayList<Party>();
	private ArrayList<ProxiedPlayer> players = new ArrayList<ProxiedPlayer>();
	private String name;
	private UUID uuid;
	private ProxiedPlayer creator;
	
	
	public Party(ProxiedPlayer creator) {
		this.creator = creator;
		this.name = creator.getName();
		this.uuid = creator.getUniqueId();
		PartyPlayer.add(this);
		this.players.add(creator);
	}
		
	public void addPlayer(ProxiedPlayer pp) {
		this.players.add(pp);
	}
	
	public void removePlayer(ProxiedPlayer pp) {
		this.players.remove(pp);
	}
	
	public void removeParty() {
		for(ProxiedPlayer pp : this.players) {
			pp.sendMessage(new TextComponent(ReplaceString.replace(Main.instance.config.getString("Party.Dissolved"), pp)));
		}
		this.players.clear();
		PartyPlayer.remove(this);
	}
	
	public ArrayList<ProxiedPlayer> getPlayers() {
		return this.players;
	}
	
	public String getCreatorName() {
		return this.name;
	}
	
	public UUID getCreatorUUID() {
		return this.uuid;
	}

	public ProxiedPlayer getCreator() {
		return this.creator;
	}
	
	public boolean isCreator(ProxiedPlayer pp) {
		return pp == this.creator;
	}
	
	public static Party getParty(ProxiedPlayer pp) {
		for(Party p : PartyPlayer) {
			if(p.getPlayers().contains(pp)) {
				return p;
			}
		}
		return null;
	}
	
	public static boolean hasParty(ProxiedPlayer pp) {
		return getParty(pp) != null;
	}
	
}
