package com.rappytv.opsucht.core.ui.hudwidget;

import com.rappytv.opsucht.api.OPSuchtTextures.SpriteHud;
import com.rappytv.opsucht.api.event.MerchantDataRefreshEvent;
import com.rappytv.opsucht.api.merchant.MerchantRate;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.ui.hudwidget.MerchantHudWidget.MerchantHudWidgetConfig;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.CustomTranslation;
import net.labymod.api.event.Subscribe;

public class MerchantHudWidget extends TextHudWidget<MerchantHudWidgetConfig> {

    private final OPSuchtAddon addon;
    private TextLine line;

    public MerchantHudWidget(OPSuchtAddon addon, HudWidgetCategory category) {
        super("merchant_rates", MerchantHudWidgetConfig.class);
        this.addon = addon;

        this.setIcon(SpriteHud.MERCHANT);
        this.bindCategory(category);
    }

    @Override
    public void load(MerchantHudWidgetConfig config) {
        super.load(config);

        this.line = this.createLine(
            Component.translatable("opsucht.hudWidget.merchant_rates.name"),
            this.getMerchantRates()
        );
    }

    @Subscribe
    public void onMerchantDataRefresh(MerchantDataRefreshEvent event) {
        this.updateLineOnRenderThread();
    }

    private void updateLineOnRenderThread() {
        Runnable runnable = () -> this.line.updateAndFlush(this.getMerchantRates());
        if(Laby.labyAPI().minecraft().isOnRenderThread()) {
            runnable.run();
        } else {
            Laby.labyAPI().minecraft().executeOnRenderThread(runnable);
        }
    }

    @Override
    public boolean isVisibleInGame() {
        return this.addon.server().isConnected() && super.isVisibleInGame();
    }

    private Component getMerchantRates() {
        List<MerchantRate> rates = new ArrayList<>(
            OPSuchtAddon.references().merchantManager().getRates()
        );

        if(rates.isEmpty()) {
            return Component.empty().append(Component.translatable(
                "opsucht.hudWidget.merchant_rates.noRates",
                NamedTextColor.RED
            ));
        }

        if(rates.size() > this.config.maxRates.get()) {
            rates = rates.subList(0, this.config.maxRates.get());
        }

        Component rateList = Component.empty();

        for (MerchantRate rate : rates) {
            rateList.append(Component.newline())
                .append(Component.space())
                .append(this.formatMerchantRate(rate));
        }

        return rateList;
    }

    private Component formatMerchantRate(MerchantRate rate) {
        return Component.empty()
            .append(Component.text("1x "))
            .append(rate.source())
            .append(Component.text(" -> ", NamedTextColor.GRAY))
            .append(Component.text(rate.exchangeRate()))
            .append(Component.space())
            .append(rate.target());
    }

    public static class MerchantHudWidgetConfig extends TextHudWidgetConfig {

        @CustomTranslation("opsucht.hudWidget.config.maxResults")
        @SliderSetting(min = 1, max = 20) // May need to be adjusted in the future
        private final ConfigProperty<Integer> maxRates = new ConfigProperty<>(10);
    }
}
