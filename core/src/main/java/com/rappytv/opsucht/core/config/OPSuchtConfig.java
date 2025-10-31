package com.rappytv.opsucht.core.config;

import com.rappytv.opsucht.core.config.subconfig.InteractionBulletConfig;
import com.rappytv.opsucht.core.config.subconfig.RichPresenceConfig;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.IntroducedIn;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@ConfigName("settings")
@SpriteTexture("settings.png")
public class OPSuchtConfig extends AddonConfig {

    @SpriteSlot
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SpriteSlot(x = 1)
    private final RichPresenceConfig richPresenceConfig = new RichPresenceConfig();

    @SpriteSlot(x = 4)
    private final InteractionBulletConfig interactionBulletConfig = new InteractionBulletConfig();

    @SpriteSlot(size = 32, y = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> autoFly = new ConfigProperty<>(true);

    @SettingSection("chat")
    @SpriteSlot(y = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> clickableNicknames = new ConfigProperty<>(true);

    @IntroducedIn(namespace = "opsucht", value = "1.1.7")
    @SpriteSlot(x = 1, y = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> coloredMentions = new ConfigProperty<>(true);

    @SettingSection("opmarket")
    @SpriteSlot(x = 7)
    @TextFieldSetting(maxLength = 10)
    private final ConfigProperty<String> priceFormat = new ConfigProperty<>("{price}$");

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }

    public RichPresenceConfig richPresenceConfig() {
        return this.richPresenceConfig;
    }

    public InteractionBulletConfig interactionBulletConfig() {
        return this.interactionBulletConfig;
    }

    public ConfigProperty<Boolean> autoFly() {
        return this.autoFly;
    }

    public ConfigProperty<Boolean> clickableNicknames() {
        return this.clickableNicknames;
    }

    public ConfigProperty<Boolean> coloredMentions() {
        return this.coloredMentions;
    }

    public ConfigProperty<String> priceFormat() {
        return this.priceFormat;
    }
}
