package com.rappytv.opsucht.api;

import com.rappytv.opsucht.api.MarketManager.DisplayMode;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.Color;

public interface IPriceHudWidgetConfig {

    ConfigProperty<Boolean> includeStackSize();

    ConfigProperty<DisplayMode> displayMode();

    ConfigProperty<String> priceFormat();

    ConfigProperty<Color> buyPriceColor();

    ConfigProperty<Color> sellPriceColor();

}
