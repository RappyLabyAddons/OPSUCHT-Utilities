package com.rappytv.opsucht.config;

import com.rappytv.opsucht.config.subconfig.DiscordRPCSubconfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingListener;
import net.labymod.api.configuration.settings.annotation.SettingListener.EventType;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.configuration.settings.type.SettingElement;
import net.labymod.api.util.I18n;

@ConfigName("settings")
public class OPSuchtConfig extends AddonConfig {

    @SettingSection("general")
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);
    private final DiscordRPCSubconfig discordRPCSubconfig = new DiscordRPCSubconfig();

    @SettingSection("chat")
    @SwitchSetting
    private final ConfigProperty<Boolean> clickableNicknames = new ConfigProperty<>(true);

    @SettingSection("context")
    @SwitchSetting
    private final ConfigProperty<Boolean> payContext = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> clanInviteContext = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> friendRequestContext = new ConfigProperty<>(true);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }
    public DiscordRPCSubconfig discordRPCSubconfig() {
        return discordRPCSubconfig;
    }

    public ConfigProperty<Boolean> clickableNicknames() {
        return clickableNicknames;
    }

    public ConfigProperty<Boolean> payContext() {
        return payContext;
    }
    public ConfigProperty<Boolean> clanInviteContext() {
        return clanInviteContext;
    }
    public ConfigProperty<Boolean> friendRequestContext() {
        return friendRequestContext;
    }
}
