package com.rappytv.opsucht.core.ui.hudwidget;

import com.rappytv.opsucht.api.market.MarketItem;
import com.rappytv.opsucht.api.market.MarketStack;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.ui.hudwidget.ItemPriceHudWidget.ItemPriceHudWidgetConfig;
import com.rappytv.opsucht.core.ui.hudwidget.config.GlobalPriceHudWidgetConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemPriceHudWidget extends TextHudWidget<ItemPriceHudWidgetConfig> {

    private static final MarketStack EXAMPLE_STACK = new MarketStack("diamond", 500, 200, 3);
    private static final MarketStack INVALID_STACK = new MarketStack("none", -1, -1, -1);
    private static final Component NO_ITEM = Component.translatable("opsucht.hudWidget.item_price.noItem");
    private static final Component NO_PRICE = Component.translatable("opsucht.hudWidget.item_price.noPrice");

    private final OPSuchtAddon addon;
    private TextLine line;
    private long lastUpdate = -1;

    public ItemPriceHudWidget(OPSuchtAddon addon) {
        super("item_price", ItemPriceHudWidgetConfig.class);
        this.addon = addon;
    }

    @Override
    public void load(ItemPriceHudWidgetConfig config) {
        super.load(config);

        this.line = this.createLine(
            Component.translatable("opsucht.hudWidget.item_price.name"),
            NO_ITEM
        );
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if(!this.addon.configuration().enabled().get()) {
            return;
        }
        if(isEditorContext && !Laby.labyAPI().minecraft().isIngame()) {
            MarketItem exampleItem = OPSuchtAddon.references().marketManager().getPrice("diamond");
            this.updateLine(
                exampleItem != null
                    ? new MarketStack(exampleItem, 3)
                    : EXAMPLE_STACK
            );
            return;
        }

        if(!this.addon.server().isConnected()) {
            return;
        }

        long now = System.currentTimeMillis();
        if(now - this.lastUpdate > 100) {
            this.updateLine();
            this.lastUpdate = now;
        }
    }

    @Override
    public boolean isVisibleInGame() {
        return this.addon.server().isConnected() && super.isVisibleInGame();
    }

    private void updateLine() {
        this.updateLine(this.getCurrentMarketStack());
    }

    private void updateLine(@Nullable MarketStack stack) {
        State state = State.VISIBLE;
        Component component;
        if(stack == null || !stack.isValid()) {
            if(this.config.hideNonPrices.get()) {
                state = State.HIDDEN;
            }
            component = stack == null ? NO_ITEM : NO_PRICE;
        } else {
            component = this.formatStack(stack);
        }

        this.line.setState(state);
        this.line.updateAndFlush(component);
    }

    @NotNull
    private Component formatStack(@NotNull MarketStack stack) {
        Component component = Component.empty();
        boolean includeStack = this.config.includeStackSize().get();

        if(this.config.showItemName.get()) {
            component.append(Component.text(stack.getName()));
            if(includeStack) {
                component.append(Component.text(" (" + stack.getSize() + ")"));
            }
            component.append(Component.text(": ", NamedTextColor.GRAY));
        }

        return component.append(OPSuchtAddon.references().marketManager().formatValueComponent(
            includeStack ? stack.getStackBuyPrice() : stack.getBuyPrice(),
            includeStack ? stack.getStackSellPrice() : stack.getSellPrice(),
            this.config
        ));
    }

    @Nullable
    private MarketStack getCurrentMarketStack() {
        Player player = Laby.labyAPI().minecraft().getClientPlayer();
        if (player == null) {
            return null;
        }

        ItemStack currentItem = player.getMainHandItemStack();
        if(currentItem == null || currentItem.isAir()) {
            return null;
        }

        ResourceLocation identifier = currentItem.getIdentifier();
        if(identifier == null) {
            return null;
        }

        MarketItem item = OPSuchtAddon.references().marketManager().getPrice(identifier.getPath());
        return item != null ? new MarketStack(item, currentItem.getSize()) : INVALID_STACK;
    }

    public static class ItemPriceHudWidgetConfig extends GlobalPriceHudWidgetConfig {

        @SwitchSetting
        private final ConfigProperty<Boolean> hideNonPrices = new ConfigProperty<>(false);

        @SwitchSetting
        private final ConfigProperty<Boolean> showItemName = new ConfigProperty<>(true);
    }
}
