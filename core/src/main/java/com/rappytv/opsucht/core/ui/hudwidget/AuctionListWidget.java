package com.rappytv.opsucht.core.ui.hudwidget;

import com.rappytv.opsucht.api.OPSuchtTextures.SpriteHud;
import com.rappytv.opsucht.api.auction.Auction;
import com.rappytv.opsucht.api.auction.AuctionCategory;
import com.rappytv.opsucht.api.event.AuctionDataRefreshEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.ui.hudwidget.AuctionListWidget.AuctionListWidgetConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.CustomTranslation;
import net.labymod.api.configuration.settings.annotation.SettingListener;
import net.labymod.api.configuration.settings.annotation.SettingListener.EventType;
import net.labymod.api.configuration.settings.type.SettingElement;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.concurrent.task.Task;
import net.labymod.api.util.io.web.request.Request;
import org.jetbrains.annotations.Nullable;

public class AuctionListWidget extends TextHudWidget<AuctionListWidgetConfig> {

    private final OPSuchtAddon addon;
    private TextLine line;

    public AuctionListWidget(OPSuchtAddon addon, HudWidgetCategory category) {
        super("auction_list", AuctionListWidgetConfig.class);
        this.addon = addon;

        this.setIcon(SpriteHud.AUCTIONS);
        this.bindCategory(category);
    }

    @Override
    public void load(AuctionListWidgetConfig config) {
        super.load(config);
        this.line = this.createLine(
            Component.translatable("opsucht.hudWidget.auction_list.name"),
            this.getAuctionList()
        );
        Task.builder(() -> this.line.updateAndFlush(this.getAuctionList()))
            .repeat(30, TimeUnit.SECONDS)
            .build()
            .execute();
    }

    @Subscribe
    private void onAuctionDataRefresh(AuctionDataRefreshEvent event) {
        this.line.updateAndFlush(this.getAuctionList());
    }

    private Component getAuctionList() {
        List<Auction> auctions = new ArrayList<>(OPSuchtAddon.references()
            .auctionManager()
            .getActiveAuctions());
        auctions.removeIf(auction -> {
            if(!auction.isActive()) {
                return true;
            }
            AuctionCategory category = this.config.getCategory();
            if(category == null) {
                return false;
            }
            return !category.matches(auction);
        });

        if(auctions.isEmpty()) {
            return Component.empty().append(Component.translatable(
                "opsucht.hudWidget.auction_list.empty",
                NamedTextColor.RED
            ));
        }
        auctions.sort(this.config.sortType().get().comparator());

        if(auctions.size() > this.config.maxAuctions().get()) {
            auctions = auctions.subList(0, this.config.maxAuctions().get());
        }

        Component auctionList = Component.empty();

        for (Auction auction : auctions) {
            auctionList.append(Component.newline())
                .append(Component.space())
                .append(this.formatAuction(auction));
        }

        return auctionList;
    }

    private Component formatAuction(Auction auction) {
        Component component = Component.empty();
        Auction.Item item = auction.item();

        component.append(Component.text(auction.item().getName().trim(), NamedTextColor.AQUA));

        if(item.amount() > 1) {
            component.append(Component.text(" (" + item.amount() + "x)", NamedTextColor.YELLOW));
        }

        component.append(Component.text(": ", NamedTextColor.GRAY))
            .append(OPSuchtAddon.references().valueFormatter().formatSingleValueComponent(
                this.addon.configuration().priceFormat().get(),
                auction.currentBid(),
                NamedTextColor.GREEN
            ))
            .append(Component.text(" | ", NamedTextColor.GRAY))
            .append(Component.text(
                "⌚ " + auction.formatRelativeExpiration(),
                NamedTextColor.YELLOW
            ));

        return component;
    }

    public static class AuctionListWidgetConfig extends TextHudWidgetConfig {

        private static final String ENDPOINT = "https://api.opsucht.net/auctions/categories";
        private static final Map<String, AuctionCategory> CATEGORIES = new HashMap<>();

        @SliderSetting(min = 1, max = 50)
        private final ConfigProperty<Integer> maxAuctions = new ConfigProperty<>(5);

        @CustomTranslation("opsucht.hudWidget.auction_list.sortType")
        @DropdownSetting
        private final ConfigProperty<SortType> sortType = new ConfigProperty<>(SortType.ENDING_SOON);

        @CustomTranslation("opsucht.hudWidget.auction_list.category")
        @DropdownSetting
        private final ConfigProperty<String> category = new ConfigProperty<>("all");

        @SuppressWarnings("unchecked")
        @SettingListener(target = "category", type = EventType.INITIALIZE)
        public void initialize(SettingElement setting) {
            DropdownWidget<String> widget = (DropdownWidget<String>) setting.asElement().getWidgets()[0];
            widget.add("all");
            Request.ofGson(AuctionCategory[].class)
                .url(ENDPOINT)
                .addHeader("User-Agent", OPSuchtAddon.getUserAgent())
                .handleErrorStream()
                .async()
                .execute(response -> {
                    if(response.hasException() || response.getStatusCode() != 200) {
                        category.set("all");
                    } else {
                        for (AuctionCategory category : response.get()) {
                            CATEGORIES.put(category.name(), category);
                            widget.add(category.name());
                        }
                    }

                    setting.setWidgets(Collections.singletonList(widget).toArray(new Widget[0]));
                });
        }

        public ConfigProperty<Integer> maxAuctions() {
            return this.maxAuctions;
        }

        public ConfigProperty<SortType> sortType() {
            return this.sortType;
        }

        public ConfigProperty<String> category() {
            return this.category;
        }

        @Nullable
        public AuctionCategory getCategory() {
            String category = this.category().get();
            if(category.equals("all")) {
                return null;
            }
            return CATEGORIES.get(this.category.get());
        }

        public enum SortType {
            ENDING_SOON(Comparator.comparing(Auction::endTime)),
            HIGHEST_BIDS(Comparator.comparing(Auction::currentBid).reversed()),
            LOWEST_BIDS(Comparator.comparing(Auction::currentBid));

            private final Comparator<Auction> comparator;

            SortType(Comparator<Auction> comparator) {
                this.comparator = comparator;
            }

            public Comparator<Auction> comparator() {
                return this.comparator;
            }
        }
    }

}
