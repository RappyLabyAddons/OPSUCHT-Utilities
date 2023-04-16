package com.rappytv.opsucht;

import com.rappytv.opsucht.util.Util;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingElement;
import net.labymod.api.configuration.settings.annotation.SettingListener;
import net.labymod.api.configuration.settings.annotation.SettingListener.EventType;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@ConfigName("settings")
public class OPSuchtConfig extends AddonConfig {

    @SettingSection("general")
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SettingSection("context")
    @SwitchSetting
    private final ConfigProperty<Boolean> payContext = new ConfigProperty<>(true);
    @TextFieldSetting
    private final ConfigProperty<String> payDefault = new ConfigProperty<>("");
    @SwitchSetting
    private final ConfigProperty<Boolean> clanInviteContext = new ConfigProperty<>(true);
    @SwitchSetting
    private final ConfigProperty<Boolean> friendRequestContext = new ConfigProperty<>(true);

    @SettingListener(target = "paydefault", type = EventType.INITIALIZE)
    public void initialize(SettingElement ignored) {
        System.out.println("uwu");
        payDefault.addChangeListener((type, oldValue, newValue) -> {
            if(newValue.isEmpty()) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                payDefault.set(oldValue);
                Util.notify(Util.getTranslation("opsucht.toasts.error"), Util.getTranslation("opsucht.toasts.number"), null);
            }
        });
    }

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }

    public ConfigProperty<Boolean> payContext() {
        return payContext;
    }
    public ConfigProperty<String> payDefault() {
        return payDefault;
    }
    public ConfigProperty<Boolean> clanInviteContext() {
        return clanInviteContext;
    }
    public ConfigProperty<Boolean> friendRequestContext() {
        return friendRequestContext;
    }
}
