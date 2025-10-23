package com.rappytv.opsucht.core.ui.hudwidget.config;

import com.rappytv.opsucht.api.market.IPriceHudWidgetConfig;
import com.rappytv.opsucht.api.market.MarketManager.DisplayMode;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.annotation.IntroducedIn;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.CustomTranslation;
import net.labymod.api.configuration.settings.annotation.SettingOrder;
import net.labymod.api.configuration.settings.annotation.SettingOrder.Order;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.Color;

public class GlobalPriceHudWidgetConfig extends TextHudWidgetConfig implements
    IPriceHudWidgetConfig {

    @SettingSection(value = "format", translation = "opsucht.hudWidget.config")
    @IntroducedIn(namespace = "opsucht", value = "1.2.1")
    @SettingOrder(Order.NORMAL)
    @CustomTranslation("opsucht.hudWidget.config.includeStackSize")
    @SwitchSetting
    private final ConfigProperty<Boolean> includeStackSize = new ConfigProperty<>(true);

    @IntroducedIn(namespace = "opsucht", value = "1.2.1")
    @SettingOrder(Order.NORMAL)
    @CustomTranslation("opsucht.hudWidget.config.displayMode")
    @DropdownSetting
    private final ConfigProperty<DisplayMode> displayMode = new ConfigProperty<>(
        DisplayMode.BOTH);

    @IntroducedIn(namespace = "opsucht", value = "1.2.1")
    @SettingOrder(Order.NORMAL)
    @CustomTranslation("opsucht.hudWidget.config.priceFormat")
    @TextFieldSetting(maxLength = 10)
    private final ConfigProperty<String> priceFormat = new ConfigProperty<>("{price}$");

    @IntroducedIn(namespace = "opsucht", value = "1.2.1")
    @SettingOrder(Order.NORMAL)
    @SettingSection(value = "format", translation = "opsucht.hudWidget.config")
    @CustomTranslation("opsucht.hudWidget.config.buyPriceColor")
    @ColorPickerSetting
    private final ConfigProperty<Color> buyPriceColor = new ConfigProperty<>(NamedTextColor.AQUA.color());

    @IntroducedIn(namespace = "opsucht", value = "1.2.1")
    @SettingOrder(Order.NORMAL)
    @CustomTranslation("opsucht.hudWidget.config.sellPriceColor")
    @ColorPickerSetting
    private final ConfigProperty<Color> sellPriceColor = new ConfigProperty<>(NamedTextColor.RED.color());

    @Override
    public ConfigProperty<Boolean> includeStackSize() {
        return this.includeStackSize;
    }

    @Override
    public ConfigProperty<DisplayMode> displayMode() {
        return this.displayMode;
    }

    @Override
    public ConfigProperty<String> priceFormat() {
        return this.priceFormat;
    }

    @Override
    public ConfigProperty<Color> buyPriceColor() {
        return this.buyPriceColor;
    }

    @Override
    public ConfigProperty<Color> sellPriceColor() {
        return this.sellPriceColor;
    }
}
