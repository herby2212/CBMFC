package de.Herbystar.CBMFC.Utilities;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Reflections {
	
	/*
	 * ChatSerializer
	 */
	public static void sendHoverMessage(ProxiedPlayer sender, ProxiedPlayer receiver, String message[], String[] buttons, String[] hover, String[] command) {
		try {
			TextComponent component = new TextComponent(message[0]);
			for(int i = 0; i < buttons.length; i++) {
				TextComponent c1 = new TextComponent(buttons[i]);
				c1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command[i]));
				c1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover[i])));
				component.addExtra(c1);
				if((i == 0) && !(message[1] == "")) {
					TextComponent spacer = new TextComponent("  ");
					component.addExtra(spacer);
				}
			}
			TextComponent afterButtons = new TextComponent(message[1]);
			component.addExtra(afterButtons);
			receiver.sendMessage(component);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
