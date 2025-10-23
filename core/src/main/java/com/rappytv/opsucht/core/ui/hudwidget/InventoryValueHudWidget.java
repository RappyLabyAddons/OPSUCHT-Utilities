package com.rappytv.opsucht.core.ui.hudwidget;

import com.rappytv.opsucht.api.OPSuchtTextures.SpriteHud;
import com.rappytv.opsucht.api.market.InventoryValueData;
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
import org.jetbrains.annotations.Nullable;

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
        ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
        if(player == null) {
            this.updateLine(null);
            return;
        }

        this.line.setState(State.HIDDEN);
        this.line.updateAndFlush(LOADING_COMPONENT);
        OPSuchtAddon.references().marketManager().calculateInventoryValue(
            player.inventory(),
            this.config.includeStackSize().get(),
            this::updateLine
        );
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
                Component.text(data.itemAmount())
            ))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(OPSuchtAddon.references().marketManager().formatValueComponent(
                data.buyValue(),
                data.sellValue(),
                this.config
            ));

        this.line.setState(State.VISIBLE);
        this.line.updateAndFlush(component);
    }
}
