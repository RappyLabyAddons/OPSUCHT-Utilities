package com.rappytv.opsucht.core.config.subconfig;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class InteractionBulletConfig extends Config {

    @SpriteSlot(size = 32, y = 1, x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> payBullet = new ConfigProperty<>(true);

    @SpriteSlot(size = 32, y = 1, x = 2)
    @SwitchSetting
    private final ConfigProperty<Boolean> clanInviteBullet = new ConfigProperty<>(true);

    @SpriteSlot(size = 32, y = 1, x = 3)
    @SwitchSetting
    private final ConfigProperty<Boolean> friendRequestBullet = new ConfigProperty<>(true);

    public ConfigProperty<Boolean> payBullet() {
        return this.payBullet;
    }

    public ConfigProperty<Boolean> clanInviteBullet() {
        return this.clanInviteBullet;
    }

    public ConfigProperty<Boolean> friendRequestBullet() {
        return this.friendRequestBullet;
    }
}
