package com.rappytv.opsucht.core.ui.hudwidget;

import com.rappytv.opsucht.api.OPSuchtTextures.SpriteHud;
import com.rappytv.opsucht.api.market.MarketItem;
import com.rappytv.opsucht.api.market.MarketStack;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.ui.hudwidget.config.GlobalPriceHudWidgetConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public class InventoryValueHudWidget extends TextHudWidget<GlobalPriceHudWidgetConfig> {

    private static final InventoryValueData EXAMPLE_VALUE_DATA = new InventoryValueData(23, 12345.67f, 123.45f);
    private static final Component LOADING_COMPONENT = Component.translatable("opsucht.hudWidget.inventory_value.loading");
    private static final Component INVALID_DATA_COMPONENT = Component.translatable("opsucht.hudWidget.inventory_value.invalidData");

    private final OPSuchtAddon addon;
    private TextLine line;
    private long lastUpdate = -1;

    public InventoryValueHudWidget(OPSuchtAddon addon, HudWidgetCategory category) {
        super("inventory_value", GlobalPriceHudWidgetConfig.class);
        this.addon = addon;

        this.setIcon(SpriteHud.INVENTORY_VALUE);
        this.bindCategory(category);
    }

    @Override
    public void load(GlobalPriceHudWidgetConfig config) {
        super.load(config);

        this.line = this.createLine(
            Component.translatable("opsucht.hudWidget.inventory_value.name"),
            LOADING_COMPONENT
        );
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if(!this.addon.configuration().enabled().get()) {
            return;
        }
        if(isEditorContext && !Laby.labyAPI().minecraft().isIngame()) {
            this.updateLine(EXAMPLE_VALUE_DATA);
            return;
        }

        if(!this.addon.server().isConnected()) {
            return;
        }

        long now = System.currentTimeMillis();
        if(now - this.lastUpdate > 1000) {
            this.updateLine();
            this.lastUpdate = now;
        }
    }

    @Override
    public boolean isVisibleInGame() {
        return this.addon.server().isConnected() && super.isVisibleInGame();
    }

    private void updateLine() {
        this.line.setState(State.HIDDEN);
        this.line.updateAndFlush(LOADING_COMPONENT);
        this.calculateInventoryValue(this::updateLine);
    }

    private void updateLine(@Nullable InventoryValueData data) {
        if(data == null || !data.isValid()) {
            this.line.setState(State.HIDDEN);
            this.line.updateAndFlush(INVALID_DATA_COMPONENT);
            return;
        }

        Component component = Component.empty()
            .append(Component.translatable(
                "opsucht.hudWidget.inventory_value.items",
                Component.text(data.itemAmount)
            ))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(OPSuchtAddon.references().marketManager().formatValueComponent(
                data.buyValue,
                data.sellValue,
                this.config
            ));

        this.line.setState(State.VISIBLE);
        this.line.updateAndFlush(component);
    }

    private void calculateInventoryValue(Consumer<@Nullable InventoryValueData> consumer) {
        ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
        if(player == null) {
            consumer.accept(null);
            return;
        }

        int items = 0;
        float totalBuyValue = 0f;
        float totalSellValue = 0f;

        for(int i = 0; i < 36; i++) {
            ItemStack itemStack = player.inventory().itemStackAt(i);
            if(itemStack == null || itemStack.isAir()) {
                continue;
            }

            ResourceLocation identifier = itemStack.getIdentifier();
            if(identifier == null) {
                continue;
            }

            MarketItem item = OPSuchtAddon.references().marketManager().getPrice(identifier.getPath());
            if(item == null || !item.isValid()) {
                continue;
            }
            MarketStack stack = new MarketStack(item, itemStack.getSize());
            boolean includeStack = this.config.includeStackSize().get();
            items += includeStack ? stack.getSize() : 1;
            totalBuyValue += includeStack ? stack.getStackBuyPrice() : stack.getBuyPrice();
            totalSellValue += includeStack ? stack.getStackSellPrice() : stack.getSellPrice();
        }

        consumer.accept(new InventoryValueData(items, totalBuyValue, totalSellValue));
    }

    private record InventoryValueData(int itemAmount, float buyValue, float sellValue) {

        public boolean isValid() {
            return this.itemAmount >= 0 && this.buyValue >= 0 && this.sellValue >= 0;
        }
    }
}
