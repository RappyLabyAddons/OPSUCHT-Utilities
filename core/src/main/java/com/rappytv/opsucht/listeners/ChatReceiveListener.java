package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.OPSuchtConfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
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
    public void onChatReceive(ChatReceiveEvent e) {
        if(!config.clickableNicknames().get()) return;
        ChatMessage message = e.chatMessage();
        if(!message.getPlainText().contains("|") || !message.getPlainText().contains("~")) return;

        String text = message.getPlainText();
        if(!text.contains("|") || !text.contains("~")) return;

        String nick = Arrays.stream(text.split(" ")).filter(s -> s.startsWith("~")).findFirst().orElse(null);
        if(nick == null || !nick.equalsIgnoreCase(text.split(" ")[2])) return;
        System.out.println(nick);

        e.setMessage(applyStyle(message, nick));
    }

    /**
     * TODO: Fix colors
     */

    private Component applyStyle(ChatMessage message, String nick) {
        String[] words = message.getPlainText().split(" ");
        TextComponent.Builder msg = TextComponent.builder();
        for (int i = 0; i < words.length; i++) {
            if (i == 2) {
                TextComponent nickComponent = Component.text(words[i] + " ");

                Style style = message.component().style()
                    .hoverEvent(HoverEvent.showText(Component.text("§a" + Util.getTranslation("opsucht.chat.clickableNickname"))))
                    .clickEvent(ClickEvent.runCommand("/realname " + nick.substring(1)));
                nickComponent.style(style);
                msg.append(nickComponent);
            } else msg.append(words[i]).append(" ");
        }
        return msg.build();
    }
}
