package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.api.event.SkullReminderEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.config.subconfig.ReminderConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.notification.Notification;

public class ReminderListener {

    private static final ResourceLocation NOTIFICATION_SOUND = ResourceLocation.create(
        "labymod",
        "lootbox.common"
    );

    private final ReminderConfig config;
    private boolean notified = false;

    public ReminderListener(OPSuchtAddon addon) {
        this.config = addon.configuration().reminderConfig();
    }

    @Subscribe
    public void onSkullReminder(SkullReminderEvent event) {
        if(this.notified) {
            return;
        }
        if(this.config.playSkullSound().get()) {
            Laby.references().minecraftSounds().playSound(
                NOTIFICATION_SOUND,
                config.skullSoundVolume().get() * 0.1F,
                1f
            );
        }
        Component description = Component.translatable(
            "opsucht.reminders.skull.description",
            Component.text("/skull", NamedTextColor.AQUA)
        );
        if(Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            Notification.builder()
                .title(Component.translatable("opsucht.reminders.skull.title"))
                .text(description)
                .duration(30000L)
                .buildAndPush();
        } else {
            Laby.references().chatExecutor().displayClientMessage(
                OPSuchtAddon.prefix().append(description.color(NamedTextColor.GREEN))
            );
        }
        this.notified = true;
    }

    @Subscribe
    public void onDisconnect(ServerDisconnectEvent event) {
        this.notified = false;
    }

}
