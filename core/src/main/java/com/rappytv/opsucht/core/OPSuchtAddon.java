package com.rappytv.opsucht.core;

import com.rappytv.opsucht.api.generated.ReferenceStorage;
import com.rappytv.opsucht.core.command.MarketCommand;
import com.rappytv.opsucht.core.config.OPSuchtConfig;
import com.rappytv.opsucht.core.listeners.ChatReceiveListener;
import com.rappytv.opsucht.core.listeners.PlayerInfoListener;
import com.rappytv.opsucht.core.ui.hudwidget.InventoryValueHudWidget;
import com.rappytv.opsucht.core.ui.hudwidget.ItemValueHudWidget;
import com.rappytv.opsucht.core.ui.hudwidget.PlayerRecordHudWidget;
import com.rappytv.opsucht.core.ui.interaction.ClanInviteBulletPoint;
import com.rappytv.opsucht.core.ui.interaction.FriendRequestBulletPoint;
import com.rappytv.opsucht.core.ui.interaction.PayBulletPoint;
import java.util.concurrent.TimeUnit;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.revision.SimpleRevision;
import net.labymod.api.util.concurrent.task.Task;
import net.labymod.api.util.version.SemanticVersion;

@AddonMain
public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    private static Component prefix = Component.empty()
        .append(Component.text("OPSUCHT", NamedTextColor.RED).decorate(TextDecoration.BOLD))
        .append(Component.text(" » ", NamedTextColor.DARK_GRAY));
    private static String userAgent = "OPSUCHT LabyAddon";

    private static ReferenceStorage referenceStorage;
    private OPSuchtServer server;

    @Override
    protected void preConfigurationLoad() {
        Laby.references().revisionRegistry().register(new SimpleRevision("opsucht", new SemanticVersion("1.1.7"), "2023-11-21"));
        Laby.references().revisionRegistry().register(new SimpleRevision("opsucht", new SemanticVersion("1.2.1"), "2025-10-23"));
    }

    @Override
    protected void enable() {
        referenceStorage = this.referenceStorageAccessor();

        userAgent += " v" + this.addonInfo().getVersion();

        this.registerSettingCategory();
        this.labyAPI().serverController().registerServer(this.server = new OPSuchtServer(this));

        this.registerCommand(new MarketCommand(this));
        this.registerListener(new ChatReceiveListener(this));
        this.registerListener(new PlayerInfoListener(this));

        HudWidgetCategory category = new HudWidgetCategory(this, "opsucht");

        this.labyAPI().hudWidgetRegistry().categoryRegistry().register(category);
        this.labyAPI().hudWidgetRegistry().register(new InventoryValueHudWidget(this, category));
        this.labyAPI().hudWidgetRegistry().register(new ItemValueHudWidget(this, category));
        this.labyAPI().hudWidgetRegistry().register(new PlayerRecordHudWidget(this, category));

        this.labyAPI().interactionMenuRegistry().register(new ClanInviteBulletPoint(this));
        this.labyAPI().interactionMenuRegistry().register(new FriendRequestBulletPoint(this));
        this.labyAPI().interactionMenuRegistry().register(new PayBulletPoint(this));

        Task.builder(referenceStorage.marketManager()::cachePrices)
            .repeat(30, TimeUnit.MINUTES)
            .build()
            .execute();
    }

    public static ReferenceStorage references() {
        return referenceStorage;
    }

    public static Component prefix() {
        return prefix.copy();
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public OPSuchtServer server() {
        return this.server;
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }
}
