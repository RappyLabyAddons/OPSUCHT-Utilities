package com.rappytv.opsucht.core.config.subconfig;

import com.rappytv.opsucht.api.IRichPresenceConfig;
import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ShowSettingInParent;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.property.Property;
import net.labymod.api.util.function.ChangeListener;

public class RichPresenceConfig extends Config implements IRichPresenceConfig {

    public RichPresenceConfig() {
        ChangeListener<Property<Boolean>, Boolean> listener = (property, oldValue, newValue) -> OPSuchtAddon.updateRPC();
        this.enabled.addChangeListener(listener);
        this.showSubServer.addChangeListener(listener);
        this.showPlayerCount.addChangeListener(listener);
    }

    @ShowSettingInParent
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    @SpriteSlot(size = 32, x = 2)
    private final ConfigProperty<Boolean> showSubServer = new ConfigProperty<>(true);

    @SwitchSetting
    @SpriteSlot(size = 32, x = 3)
    private final ConfigProperty<Boolean> showPlayerCount = new ConfigProperty<>(true);

    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }

    public ConfigProperty<Boolean> showSubServer() {
        return this.showSubServer;
    }

    public ConfigProperty<Boolean> showPlayerCount() {
        return this.showPlayerCount;
    }
}
