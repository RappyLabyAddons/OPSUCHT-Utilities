package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.OPSuchtConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
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
        if(!OPSuchtAddon.isConnected()) return;

        if(config.clickableNicknames().get()) {
            String text = event.chatMessage().getPlainText();
            if(!text.contains("|") || !text.contains("~") || text.length() < 3) return;

            String nick = text.split(" ")[2];
            if(!nick.startsWith("~")) return;

            Style style = event.message().style()
                .hoverEvent(HoverEvent.showText(Component.translatable("opsucht.chat.clickableNickname", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand("/realname " + nick.substring(1)));
            event.message().style(style);
        }
    }
}
