package com.rappytv.opsucht.config;

import com.rappytv.opsucht.config.subconfig.ContextSubconfig;
import com.rappytv.opsucht.config.subconfig.DiscordRPCSubconfig;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;

@ConfigName("settings")
@SpriteTexture("settings.png")
public class OPSuchtConfig extends AddonConfig {

    public OPSuchtConfig() {
        autoFly.customRequires((ignored) -> rank.get().hasPermission(ServerRank.PLATIN));
    }

    @SpriteSlot(size = 32)
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);
    private final ConfigProperty<ServerRank> rank = new ConfigProperty<>(ServerRank.PLAYER);
    @SpriteSlot(size = 32, x = 1)
    private final DiscordRPCSubconfig discordRPCSubconfig = new DiscordRPCSubconfig();

    @SpriteSlot(size = 32, y = 1)
    private final ContextSubconfig contextSubconfig = new ContextSubconfig();

    @SpriteSlot(size = 32, y = 2)
    @SwitchSetting
    private final ConfigProperty<Boolean> clickableNicknames = new ConfigProperty<>(true);
    @SpriteSlot(size = 32, y = 2, x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> autoFly = new ConfigProperty<>(true);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }
    public ServerRank rank() {
        return rank.get();
    }
    public DiscordRPCSubconfig discordRPCSubconfig() {
        return discordRPCSubconfig;
    }

    public ConfigProperty<Boolean> clickableNicknames() {
        return clickableNicknames;
    }
    public ContextSubconfig contextSubconfig() {
        return contextSubconfig;
    }
    public ConfigProperty<Boolean> autoFly() {
        return autoFly;
    }
}
