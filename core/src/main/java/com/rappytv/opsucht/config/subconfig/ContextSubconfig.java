package com.rappytv.opsucht.config.subconfig;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ContextSubconfig extends Config {

    @SpriteSlot(size = 32, y = 1, x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> payContext = new ConfigProperty<>(true);
    @SpriteSlot(size = 32, y = 1, x = 2)
    @SwitchSetting
    private final ConfigProperty<Boolean> clanInviteContext = new ConfigProperty<>(true);
    @SpriteSlot(size = 32, y = 1, x = 3)
    @SwitchSetting
    private final ConfigProperty<Boolean> friendRequestContext = new ConfigProperty<>(true);

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
