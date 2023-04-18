package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.OPSuchtConfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import java.util.Arrays;
import java.util.Iterator;

public class ChatReceiveListener {

    private final OPSuchtConfig config;

    public ChatReceiveListener(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if(!config.clickableNicknames().get()) return;
        ChatMessage message = event.chatMessage();
        if(!message.getPlainText().contains("|") || !message.getPlainText().contains("~")) return;

        String text = message.getPlainText();
        if(!text.contains("|") || !text.contains("~")) return;

        String nick = Arrays.stream(text.split(" ")).filter(s -> s.startsWith("~")).findFirst().orElse(null);
        if(nick == null || !nick.equalsIgnoreCase(text.split(" ")[2])) return;

        applyEvents(event.message(), nick);
    }

    /**
     * TODO: Apply style only to nickname and not the whole message
     */

    public void applyEvents(Component component, String playerName) {
        Iterator<Component> children = component.getChildren().iterator();

        Component argument;
        while(children.hasNext()) {
            argument = children.next();
            this.applyEvents(argument, playerName);
        }

        if(component instanceof TranslatableComponent) {
            children = ((TranslatableComponent) component).getArguments().iterator();

            while(children.hasNext()) {
                argument = children.next();
                this.applyEvents(argument, playerName);
            }
        }

        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent) component;
            String text = textComponent.getText();
            Style style = textComponent.style()
                .hoverEvent(HoverEvent.showText(Component.text("§a" + Util.getTranslation("opsucht.chat.clickableNickname"))))
                .clickEvent(ClickEvent.runCommand("/realname " + playerName.substring(1)));
            textComponent.style(style);
            int next = text.indexOf(playerName);

            if(next != -1) {
                textComponent.text("");
                if(next == 0 && text.length() == playerName.length()) {
                    component.append(0, textComponent);
                } else {
                    int lastNameAt = 0;
                    int childIndex = 0;

                    for(int i = 0; i < text.length(); ++i) {
                        if(i == next) {
                            if(i > lastNameAt) {
                                component.append(childIndex++, Component.text(text.substring(lastNameAt, i)));
                            }

                            component.append(childIndex++, textComponent);
                            lastNameAt = i + playerName.length();
                        }
                    }

                    if(lastNameAt < text.length()) {
                        component.append(childIndex, Component.text(text.substring(lastNameAt)));
                    }
                }
            }
        }
    }
}
