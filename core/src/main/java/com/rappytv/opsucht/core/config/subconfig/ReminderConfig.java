package com.rappytv.opsucht.core.config.subconfig;

import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

public class ReminderConfig extends Config { // TODO: add icons

    @Exclude
    private final ConfigProperty<Long> lastSkullClaim = new ConfigProperty<>(-1L);

    @Exclude
    private final ConfigProperty<Long> lastDailyRewardClaim = new ConfigProperty<>(-1L);

    @SettingSection("skull")
    @SwitchSetting
    private final ConfigProperty<Boolean> skullReminders = new ConfigProperty<>(true);

    @SettingRequires("skullReminders")
    @SwitchSetting
    private final ConfigProperty<Boolean> playSkullSound = new ConfigProperty<>(true);

    @SettingRequires("playSkullSound")
    @SliderSetting(min = 1, max = 10)
    private final ConfigProperty<Integer> skullSoundVolume = new ConfigProperty<>(4);

    @MethodOrder(after = "skullSoundVolume")
    @ButtonSetting
    public void resetSkullReminder() {
        this.lastSkullClaim.reset();
    }

    @SettingSection("dailyReward")
    @SwitchSetting
    private final ConfigProperty<Boolean> dailyRewardClaimer = new ConfigProperty<>(true);

    @MethodOrder(after = "dailyRewardClaimer")
    @ButtonSetting
    public void resetDailyRewardReminder() {
        this.lastDailyRewardClaim.reset();
    }

    public ConfigProperty<Long> lastSkullClaim() {
        return this.lastSkullClaim;
    }

    public ConfigProperty<Boolean> skullReminders() {
        return this.skullReminders;
    }

    public ConfigProperty<Boolean> playSkullSound() {
        return this.playSkullSound;
    }

    public ConfigProperty<Integer> skullSoundVolume() {
        return this.skullSoundVolume;
    }

    public ConfigProperty<Long> lastDailyRewardClaim() {
        return this.lastDailyRewardClaim;
    }

    public ConfigProperty<Boolean> dailyRewardClaimer() {
        return this.dailyRewardClaimer;
    }

}
