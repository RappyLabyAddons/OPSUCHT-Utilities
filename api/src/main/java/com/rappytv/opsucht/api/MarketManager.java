package com.rappytv.opsucht.api;

import net.labymod.api.client.component.Component;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Referenceable
public interface MarketManager {

    @Nullable
    MarketItem getPrice(String itemId);

    @NotNull
    Component formatValueComponent(float buyValue, float sellValue, IPriceHudWidgetConfig config);

    void cachePrices();

    enum DisplayMode {
        BOTH,
        ONLY_BUY,
        ONLY_SELL
    }
}
