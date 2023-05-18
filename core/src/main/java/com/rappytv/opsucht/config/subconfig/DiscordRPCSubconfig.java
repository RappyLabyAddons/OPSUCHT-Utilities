package com.rappytv.opsucht.config.subconfig;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class DiscordRPCSubconfig {


    @ParentSwitch
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> showSubServer = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> showPlayerAmount = new ConfigProperty<>(true);

    public boolean enabled() {
        return enabled.get();
    }
    public ConfigProperty<Boolean> showSubServer() {
        return showSubServer;
    }

    public ConfigProperty<Boolean> showPlayerAmount() {
        return showPlayerAmount;
    }
}
