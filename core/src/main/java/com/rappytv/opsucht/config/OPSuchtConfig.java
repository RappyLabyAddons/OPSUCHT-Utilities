package com.rappytv.opsucht.config;

import com.rappytv.opsucht.config.subconfig.DiscordRPCSubconfig;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@ConfigName("settings")
@SpriteTexture("settings.png")
public class OPSuchtConfig extends AddonConfig {

    @SettingSection("general")
    @SpriteSlot(size = 32)
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);
    @SpriteSlot(size = 32, x = 1)
    private final DiscordRPCSubconfig discordRPCSubconfig = new DiscordRPCSubconfig();

    @SettingSection("chat")
    @SpriteSlot(size = 32, y = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> clickableNicknames = new ConfigProperty<>(true);

    @SettingSection("context")
    @SpriteSlot(size = 32, y = 1, x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> payContext = new ConfigProperty<>(true);
    @SpriteSlot(size = 32, y = 1, x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> clanInviteContext = new ConfigProperty<>(true);
    @SpriteSlot(size = 32, y = 1, x = 1)
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
