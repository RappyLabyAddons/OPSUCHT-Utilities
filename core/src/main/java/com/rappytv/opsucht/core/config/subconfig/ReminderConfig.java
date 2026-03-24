package com.rappytv.opsucht.core.config.subconfig;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.CustomTranslation;
import net.labymod.api.configuration.settings.annotation.SettingRequires;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

public class ReminderConfig extends Config { // TODO: add icons

    private static final String DAILY_REWARD_AUTO_CLAIMER_COMPAT = "1.16.5<1.21.11";
    public static final boolean SUPPORTS_DAILY_REWARD_AUTO_CLAIMER =
        OPSuchtAddon.isMinecraftMultiVersionSupported(DAILY_REWARD_AUTO_CLAIMER_COMPAT);

    @Exclude
    private final ConfigProperty<Long> lastSkullClaim = new ConfigProperty<>(-1L);

    @Exclude
    private final ConfigProperty<Long> lastDailyRewardClaim = new ConfigProperty<>(-1L);

    @SettingSection("skull")
    @SwitchSetting
    private final ConfigProperty<Boolean> skullReminders = new ConfigProperty<>(true);

    @CustomTranslation("opsucht.settings.reminderConfig.playSound")
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
    @DropdownSetting
    private final ConfigProperty<DailyRewardReminderType> dailyRewardReminderType =
        new ConfigProperty<>(DailyRewardReminderType.ONLY_CLAIM)
            .visibilitySupplier(() -> SUPPORTS_DAILY_REWARD_AUTO_CLAIMER);

    @SwitchSetting
    private final ConfigProperty<Boolean> dailyRewardReminder = new ConfigProperty<>(true)
        .visibilitySupplier(() -> !SUPPORTS_DAILY_REWARD_AUTO_CLAIMER);

    @CustomTranslation("opsucht.settings.reminderConfig.playSound")
    @SwitchSetting
    private final ConfigProperty<Boolean> playDailyRewardSound = new ConfigProperty<>(true)
        .customRequires((value) -> SUPPORTS_DAILY_REWARD_AUTO_CLAIMER
            ? this.dailyRewardReminderType.get().remind
            : this.dailyRewardReminder.get()
        );

    @SettingRequires("playDailyRewardSound")
    @SliderSetting(min = 1, max = 10)
    private final ConfigProperty<Integer> dailyRewardSoundVolume = new ConfigProperty<>(4);


    @MethodOrder(after = "dailyRewardSoundVolume")
    @ButtonSetting
    public void resetDailyRewardClaimer() {
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

    public ConfigProperty<DailyRewardReminderType> dailyRewardReminderType() {
        return this.dailyRewardReminderType;
    }

    public ConfigProperty<Boolean> dailyRewardReminder() {
        return this.dailyRewardReminder;
    }

    public ConfigProperty<Boolean> playDailyRewardSound() {
        return playDailyRewardSound;
    }

    public ConfigProperty<Integer> dailyRewardVolume() {
        return dailyRewardSoundVolume;
    }

    public enum DailyRewardReminderType {
        NONE(false, false),
        ONLY_REMIND(true, false),
        ONLY_CLAIM(false, true),
        BOTH(true, true);

        private final boolean remind;
        private final boolean autoClaim;

        DailyRewardReminderType(boolean remind, boolean autoClaim) {
            this.remind = remind;
            this.autoClaim = autoClaim;
        }

        public boolean remind() {
            return this.remind;
        }

        public boolean autoClaim() {
            return this.autoClaim;
        }
    }
}
