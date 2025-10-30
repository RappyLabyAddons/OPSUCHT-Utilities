package com.rappytv.opsucht.api;

import com.rappytv.opsucht.api.market.MarketManager.DisplayMode;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;

@Referenceable
public interface ValueFormatter {

    @NotNull
    Component formatSingleValueComponent(String format, float value, TextColor color);

    @NotNull
    Component formatValueComponent(
        float buyValue,
        float sellValue,
        String format,
        TextColor buyPriceColor,
        TextColor sellPriceColor,
        DisplayMode mode
    );

    @NotNull
    String formatFloat(float number);
}
