package com.rappytv.opsucht.config.subconfig;

import com.rappytv.opsucht.OPSuchtAddon;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class DiscordRPCSubconfig extends Config {

    public DiscordRPCSubconfig() {
        enabled.addChangeListener((property, oldValue, newValue) -> OPSuchtAddon.updateRPC());
        showSubServer.addChangeListener((property, oldValue, newValue) -> OPSuchtAddon.updateRPC());
        showPlayerCount.addChangeListener((property, oldValue, newValue) -> OPSuchtAddon.updateRPC());
    }

    @ParentSwitch
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> showSubServer = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> showPlayerCount = new ConfigProperty<>(true);

    public boolean enabled() {
        return enabled.get();
    }
    public ConfigProperty<Boolean> showSubServer() {
        return showSubServer;
    }

    public ConfigProperty<Boolean> showPlayerCount() {
        return showPlayerCount;
    }
}
