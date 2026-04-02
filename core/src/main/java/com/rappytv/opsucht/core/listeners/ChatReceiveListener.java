package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.core.OPSuchtAddon;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;

public class ChatReceiveListener {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@\\w{3,16}", Pattern.CASE_INSENSITIVE);
    private static final Pattern SKULL_PATTERN = Pattern.compile("^OPSUCHT » Du hast den Kopf von ([A-Za-z0-9_]{3,16}) erhalten\\.$");
    private static final Pattern DAILY_REWARD_PATTERN = Pattern.compile("^OPSUCHT » Du hast deine tägliche Belohnung abgeholt und \\d+\\$ erhalten!$");
    private final OPSuchtAddon addon;

    public ChatReceiveListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if(!this.addon.server().isConnected()) return;
        Component message = event.message();
        String text = event.chatMessage().getPlainText();

        if(SKULL_PATTERN.matcher(text).find()) {
            this.addon.configuration()
                .reminderConfig()
                .lastSkullClaim()
                .set(System.currentTimeMillis());
            this.addon.logger().info("Saved last skull claim timestamp");
            return;
        } else if(DAILY_REWARD_PATTERN.matcher(text).find()) {
            this.addon.configuration()
                .reminderConfig()
                .lastDailyRewardClaim()
                .set(System.currentTimeMillis());
            this.addon.logger().info("Saved last daily reward claim timestamp");
            return;
        }

        if(this.addon.configuration().coloredMentions().get() && text.contains("@")) {
            for(MatchResult matcher : MENTION_PATTERN.matcher(text).results().toList()) {
                if(matcher.group().equals("@TEAM") || matcher.group().equals("@CLAN")) continue;
                this.replaceComponent(message, matcher.group(), () ->
                    Component.text(matcher.group(), NamedTextColor.AQUA).copy()
                );
            }
            event.setMessage(message);
        }
        if(this.addon.configuration().clickableNicknames().get()) {
            if(!text.contains("|") || !text.contains("~") || text.length() < 3) return;

            String nick = text.split(" ")[2];
            if(!nick.startsWith("~")) return;

            Style style = event.message().style()
                .hoverEvent(HoverEvent.showText(Component.translatable(
                    "opsucht.chat.clickableNickname",
                    NamedTextColor.GREEN
                )))
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
