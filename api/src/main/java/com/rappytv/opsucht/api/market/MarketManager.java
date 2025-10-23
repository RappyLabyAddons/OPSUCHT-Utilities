package com.rappytv.opsucht.api.market;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Inventory;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

@Referenceable
public interface MarketManager {

    @Nullable
    MarketItem getItem(String itemId);

    @NotNull
    Component formatValueComponent(float buyValue, float sellValue, @NotNull IPriceHudWidgetConfig config);

    void calculateInventoryValue(@NotNull Inventory inventory, boolean includeStack, @NotNull Consumer<@Nullable InventoryValueData> consumer);

    @NotNull
    String formatFloat(float number);

    void cachePrices();

    enum DisplayMode {
        BOTH,
        ONLY_BUY,
        ONLY_SELL
    }
}
