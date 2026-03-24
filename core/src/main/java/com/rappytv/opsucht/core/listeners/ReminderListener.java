package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.api.event.reminders.DailyRewardReminderEvent;
import com.rappytv.opsucht.api.event.reminders.SkullReminderEvent;
import com.rappytv.opsucht.api.inventory.ContainerOpenEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.config.subconfig.ReminderConfig;
import com.rappytv.opsucht.core.config.subconfig.ReminderConfig.DailyRewardReminderType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.serializer.plain.PlainTextComponentSerializer;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.notification.Notification;

public class ReminderListener {

    private static final ResourceLocation NOTIFICATION_SOUND = ResourceLocation.create(
        "labymod",
        "lootbox.common"
    );
    private static boolean awaitingRewardContainer = false;

    private final OPSuchtAddon addon;
    private final ReminderConfig config;
    private boolean sentDailyRewardNotification = false;
    private boolean sentSkullNotification = false;

    public ReminderListener(OPSuchtAddon addon) {
        this.addon = addon;
        this.config = addon.configuration().reminderConfig();
    }

    @Subscribe
    public void onDailyRewardReminderEvent(DailyRewardReminderEvent event) {
        boolean supportsAutoClaim = ReminderConfig.SUPPORTS_DAILY_REWARD_AUTO_CLAIMER;
        DailyRewardReminderType reminderType = config.dailyRewardReminderType().get();
        boolean remind = supportsAutoClaim
            ? reminderType.remind()
            : config.dailyRewardReminder().get();

        if(remind && !this.sentDailyRewardNotification) {
            if(config.playDailyRewardSound().get()) {
                Laby.references().minecraftSounds().playSound(
                    NOTIFICATION_SOUND,
                    config.dailyRewardVolume().get() * 0.1F,
                    2f
                );
            }
            this.sendNotification("dailyReward");
            this.sentDailyRewardNotification = true;
        }
        if(supportsAutoClaim && reminderType.autoClaim()) {
            this.addon.taskManager().claimDailyRewardTask().execute();
        }
    }

    @Subscribe
    public void onContainerOpen(ContainerOpenEvent event) {
        if(!(event.title() instanceof TextComponent component) || !awaitingRewardContainer) {
            return;
        }
        awaitingRewardContainer = false;
        String text = PlainTextComponentSerializer.plainText().serialize(component);

        if(!text.matches("^OPSUCHT » Belohnungen$")) {
            return;
        }
        OPSuchtAddon.references().containerApi().clickSlot(20);
        OPSuchtAddon.references().containerApi().closeContainer();
        this.addon.logger().info("Claimed daily reward");
    }

    @Subscribe
    public void onSkullReminder(SkullReminderEvent event) {
        if(this.sentSkullNotification) {
            return;
        }
        if(this.config.playSkullSound().get()) {
            Laby.references().minecraftSounds().playSound(
                NOTIFICATION_SOUND,
                config.skullSoundVolume().get() * 0.1F,
                2f
            );
        }
        this.sendNotification("skull");
        this.sentSkullNotification = true;
    }

    @Subscribe
    public void onDisconnect(ServerDisconnectEvent event) {
        this.sentDailyRewardNotification = false;
        this.sentSkullNotification = false;
    }

    private void sendNotification(String type) {
        Component description = Component.translatable(
            "opsucht.reminders." + type + ".description",
            Component.text("/skull", NamedTextColor.AQUA)
        );
        if(Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            Notification.builder()
                .title(Component.translatable("opsucht.reminders." + type + ".title"))
                .text(description)
                .duration(30000L)
                .buildAndPush();
        } else {
            Laby.references().chatExecutor().displayClientMessage(
                OPSuchtAddon.prefix().append(description.color(NamedTextColor.GREEN))
            );
        }
    }

    public static void awaitRewardContainer() {
        awaitingRewardContainer = true;
    }

}
