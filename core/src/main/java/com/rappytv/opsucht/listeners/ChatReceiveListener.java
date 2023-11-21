package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.OPSuchtConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ChatReceiveListener {

    private final OPSuchtConfig config;

    public ChatReceiveListener(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if(!OPSuchtAddon.isConnected()) return;
        Component message = event.message();
        String text = event.chatMessage().getPlainText();

        if(config.coloredMentions().get() && text.contains("@")) {
            Pattern pattern = Pattern.compile("@\\w{3,16}", Pattern.CASE_INSENSITIVE);
            for(MatchResult matcher : pattern.matcher(text).results().toList()) {
                replaceComponent(message, matcher.group(), () -> Component.text(matcher.group(), NamedTextColor.AQUA).copy());
            }
            event.setMessage(message);
        }
        if(config.clickableNicknames().get()) {
            if(!text.contains("|") || !text.contains("~") || text.length() < 3) return;

            String nick = text.split(" ")[2];
            if(!nick.startsWith("~")) return;

            Style style = event.message().style()
                .hoverEvent(HoverEvent.showText(Component.translatable("opsucht.chat.clickableNickname", NamedTextColor.GREEN)))
                .clickEvent(ClickEvent.runCommand("/realname " + nick.substring(1)));
            event.message().style(style);
        }
    }

    private void replaceComponent(Component component, String target, Supplier<Component> replacement) {
        for(Component child : component.getChildren()) {
            this.replaceComponent(child, target, replacement);
        }

        if(component instanceof TranslatableComponent) {
            for(Component argument : ((TranslatableComponent) component).getArguments()) {
                this.replaceComponent(argument, target, replacement);
            }
        }

        if(component instanceof TextComponent textComponent) {
            String text = textComponent.getText();

            int next = text.indexOf(target);
            if(next != -1) {
                textComponent.text("");
                if(next == 0 && text.length() == target.length()) {
                    component.append(0, replacement.get());
                } else {
                    int lastTargetAt = 0;
                    int childIndex = 0;
                    for (int i = 0; i < text.length(); i++) {
                        if (i != next) {
                            continue;
                        }

                        if (i > lastTargetAt) {
                            component.append(childIndex++, Component.text(text.substring(lastTargetAt, i)));
                        }

                        component.append(childIndex++, replacement.get());
                        lastTargetAt = i + target.length();
                    }

                    if (lastTargetAt < text.length()) {
                        component.append(childIndex, Component.text(text.substring(lastTargetAt)));
                    }
                }
            }
        }
    }
}
