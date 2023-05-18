package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.OPSuchtConfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import java.util.Arrays;

public class ChatReceiveListener {

    private final OPSuchtConfig config;

    public ChatReceiveListener(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if(!config.clickableNicknames().get() || !Util.isConnectedToServer()) return;
        ChatMessage message = event.chatMessage();
        if(!message.getPlainText().contains("|") || !message.getPlainText().contains("~")) return;

        String text = message.getPlainText();
        if(!text.contains("|") || !text.contains("~")) return;

        String nick = Arrays.stream(text.split(" ")).filter(s -> s.startsWith("~")).findFirst().orElse(null);
        if(nick == null || !nick.equalsIgnoreCase(text.split(" ")[2])) return;

        Style style = event.message().style()
            .hoverEvent(HoverEvent.showText(Component.text("§a" + Util.getTranslation("opsucht.chat.clickableNickname"))))
            .clickEvent(ClickEvent.runCommand("/realname " + nick.substring(1)));
        event.message().style(style);
    }
}
