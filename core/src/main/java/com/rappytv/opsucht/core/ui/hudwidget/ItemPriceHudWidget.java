package com.rappytv.opsucht.core.ui.hudwidget;

import com.rappytv.opsucht.api.MarketItem;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.ui.hudwidget.ItemPriceHudWidget.ItemPriceHudWidgetConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownEntryTranslationPrefix;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ItemPriceHudWidget extends TextHudWidget<ItemPriceHudWidgetConfig> {

    private static final MarketStack EXAMPLE_STACK = new MarketStack(new MarketItem(
        "diamond", 500, 200
    ), 3);
    private static final MarketStack INVALID_STACK = new MarketStack(new MarketItem(
        "none", -1, -1
    ), -1);
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

    private void updateLine(MarketStack stack) {
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
        if (!stack.isValid()) {
            return NO_PRICE;
        }
        Component component = Component.empty();
        boolean includeStack = this.config.includeStackSize.get();

        if(this.config.showItemName.get()) {
            component.append(Component.text(stack.item.getName()));
            if(includeStack) {
                component.append(Component.space())
                    .append(Component.text("(" + stack.size + ")"));
            }
            component.append(Component.text(":", NamedTextColor.GRAY))
                .append(Component.space());
        }

        String format = this.config.priceFormat.get().trim();
        Component buyComponent = Component.text(format.replace(
            "{price}", this.formatFloat(stack.buyPrice(includeStack))
        ), TextColor.color(this.config.buyPriceColor.get().get()));
        Component sellComponent = Component.text(format.replace(
            "{price}", this.formatFloat(stack.sellPrice(includeStack))
        ), TextColor.color(this.config.sellPriceColor.get().get()));

        switch (this.config.displayMode.get()) {
            case BOTH -> component
                .append(buyComponent)
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(sellComponent);
            case ONLY_BUY -> component.append(buyComponent);
            case ONLY_SELL -> component.append(sellComponent);
        }
        return component;
    }

    private String formatFloat(float number) {
        DecimalFormat format = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMANY));
        return format.format(number);
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

    private record MarketStack(MarketItem item, int size) {

        public float buyPrice(boolean includeStack) {
            return this.item.getBuyPrice() * (includeStack ? this.size : 1);
        }

        public float sellPrice(boolean includeStack) {
            return this.item.getSellPrice() * (includeStack ? this.size : 1);
        }

        @SuppressWarnings("all")
        public boolean isValid() {
            return this.size > 0 && this.item.isValid();
        }
    }

    public static class ItemPriceHudWidgetConfig extends TextHudWidgetConfig {

        @SwitchSetting
        private final ConfigProperty<Boolean> hideNonPrices = new ConfigProperty<>(false);

        @SettingSection("format")
        @SwitchSetting
        private final ConfigProperty<Boolean> showItemName = new ConfigProperty<>(true);

        @SwitchSetting
        private final ConfigProperty<Boolean> includeStackSize = new ConfigProperty<>(true);

        @DropdownEntryTranslationPrefix("opsucht.hudWidget.item_price.displayMode.entries")
        @DropdownSetting
        private final ConfigProperty<DisplayMode> displayMode = new ConfigProperty<>(DisplayMode.BOTH);

        @TextFieldSetting(maxLength = 10)
        private final ConfigProperty<String> priceFormat = new ConfigProperty<>("{price}$");

        @SettingSection("colors")
        @ColorPickerSetting
        private final ConfigProperty<Color> buyPriceColor = new ConfigProperty<>(NamedTextColor.AQUA.color());

        @ColorPickerSetting
        private final ConfigProperty<Color> sellPriceColor = new ConfigProperty<>(NamedTextColor.RED.color());

        public enum DisplayMode {
            BOTH,
            ONLY_BUY,
            ONLY_SELL
        }
    }
}
