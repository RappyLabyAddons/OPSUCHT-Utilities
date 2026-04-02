package com.rappytv.opsucht.core;

import com.rappytv.opsucht.api.generated.ReferenceStorage;
import com.rappytv.opsucht.core.command.MarketCommand;
import com.rappytv.opsucht.core.config.OPSuchtConfig;
import com.rappytv.opsucht.core.listeners.ChatReceiveListener;
import com.rappytv.opsucht.core.listeners.PlayerInfoListener;
import com.rappytv.opsucht.core.listeners.ReminderListener;
import com.rappytv.opsucht.core.manager.TaskManager;
import com.rappytv.opsucht.core.ui.hudwidget.AuctionListHudWidget;
import com.rappytv.opsucht.core.ui.hudwidget.InventoryValueHudWidget;
import com.rappytv.opsucht.core.ui.hudwidget.ItemValueHudWidget;
import com.rappytv.opsucht.core.ui.hudwidget.MerchantHudWidget;
import com.rappytv.opsucht.core.ui.hudwidget.PlayerRecordHudWidget;
import com.rappytv.opsucht.core.ui.interaction.ClanInviteBulletPoint;
import com.rappytv.opsucht.core.ui.interaction.FriendRequestBulletPoint;
import com.rappytv.opsucht.core.ui.interaction.PayBulletPoint;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.loader.MinecraftVersion;
import net.labymod.api.loader.MinecraftVersions;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.revision.SimpleRevision;
import net.labymod.api.util.version.SemanticVersion;
import net.labymod.api.util.version.comparison.VersionMultiRangeComparison;
import net.labymod.api.util.version.serial.VersionDeserializer;

@AddonMain
public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    private static final Component PREFIX = Component.empty()
        .append(Component.text("OPSUCHT", NamedTextColor.RED).decorate(TextDecoration.BOLD))
        .append(Component.text(" » ", NamedTextColor.DARK_GRAY));
    private static String userAgent = "OPSUCHT LabyAddon";

    private static ReferenceStorage referenceStorage;
    private TaskManager taskManager;
    private OPSuchtServer server;

    @Override
    protected void preConfigurationLoad() {
        this.registerRevision(1, 1, 7, "2023-11-21");
        this.registerRevision(1, 2, 1, "2025-10-23");
        this.registerRevision(1, 2, 2, "2025-11-10");
        this.registerRevision(1, 2, 3, "2026-04-10");
    }

    @Override
    protected void enable() {
        referenceStorage = this.referenceStorageAccessor();

        userAgent += " v" + this.addonInfo().getVersion();

        this.registerSettingCategory();
        this.taskManager = new TaskManager(this);
        this.taskManager.executeTasks();
        this.labyAPI().serverController().registerServer(this.server = new OPSuchtServer(this));

        this.registerCommand(new MarketCommand(this));
        this.registerListener(new ChatReceiveListener(this));
        this.registerListener(new PlayerInfoListener(this));
        this.registerListener(new ReminderListener(this));

        HudWidgetCategory category = new HudWidgetCategory(this, "opsucht");

        this.labyAPI().hudWidgetRegistry().categoryRegistry().register(category);
        this.labyAPI().hudWidgetRegistry().register(new AuctionListHudWidget(this, category));
        this.labyAPI().hudWidgetRegistry().register(new InventoryValueHudWidget(this, category));
        this.labyAPI().hudWidgetRegistry().register(new ItemValueHudWidget(this, category));
        this.labyAPI().hudWidgetRegistry().register(new MerchantHudWidget(this, category));
        this.labyAPI().hudWidgetRegistry().register(new PlayerRecordHudWidget(this, category));

        this.labyAPI().interactionMenuRegistry().register(new ClanInviteBulletPoint(this));
        this.labyAPI().interactionMenuRegistry().register(new FriendRequestBulletPoint(this));
        this.labyAPI().interactionMenuRegistry().register(new PayBulletPoint(this));
    }

    public OPSuchtServer server() {
        return this.server;
    }

    public TaskManager taskManager() {
        return taskManager;
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }

    private void registerRevision(int major, int minor, int patch, String releaseDate) {
        Laby.references().revisionRegistry().register(new SimpleRevision(
            "opsucht",
            new SemanticVersion(major, minor, patch),
            releaseDate
        ));
    }

    public static ReferenceStorage references() {
        return referenceStorage;
    }

    public static Component prefix() {
        return PREFIX.copy();
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static boolean isMinecraftMultiVersionSupported(String version) {
        MinecraftVersion runningVersion = MinecraftVersions.byId(Laby.labyAPI().labyModLoader().version());
        VersionMultiRangeComparison<MinecraftVersion> comparison = VersionMultiRangeComparison.parse(
            version,
            v -> MinecraftVersions.byId(VersionDeserializer.from(v))
        );
        return comparison.isCompatible(runningVersion);
    }
}
