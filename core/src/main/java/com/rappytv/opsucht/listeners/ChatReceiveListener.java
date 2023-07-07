package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.OPSuchtConfig;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.util.I18n;

public class ChatReceiveListener {

    private final OPSuchtConfig config;

    public ChatReceiveListener(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if(!config.clickableNicknames().get() || !OPSuchtAddon.isConnected()) return;
        ChatMessage message = event.chatMessage();
        if(!message.getPlainText().contains("|") || !message.getPlainText().contains("~")) return;

        String text = message.getPlainText();
        if(!text.contains("|") || !text.contains("~") || text.length() < 3) return;

        String nick = text.split(" ")[2];
        if(!nick.startsWith("~")) return;

        Style style = event.message().style()
            .hoverEvent(HoverEvent.showText(Component.text("§a" + I18n.translate("opsucht.chat.clickableNickname"))))
            .clickEvent(ClickEvent.runCommand("/realname " + nick.substring(1)));
        event.message().style(style);
    }
}
