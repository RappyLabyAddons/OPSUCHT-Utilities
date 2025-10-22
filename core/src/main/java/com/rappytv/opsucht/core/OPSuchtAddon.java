package com.rappytv.opsucht.core;

import com.rappytv.opsucht.api.RichPresenceManager;
import com.rappytv.opsucht.api.generated.ReferenceStorage;
import com.rappytv.opsucht.core.config.OPSuchtConfig;
import com.rappytv.opsucht.core.listeners.ChatReceiveListener;
import com.rappytv.opsucht.core.listeners.PlayerInfoListener;
import com.rappytv.opsucht.core.ui.interaction.ClanInviteBulletPoint;
import com.rappytv.opsucht.core.ui.interaction.FriendRequestBulletPoint;
import com.rappytv.opsucht.core.ui.interaction.PayBulletPoint;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.revision.SimpleRevision;
import net.labymod.api.util.version.SemanticVersion;

@AddonMain
public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    private static ReferenceStorage referenceStorage;
    private OPSuchtServer server;

    @Override
    protected void preConfigurationLoad() {
        Laby.references().revisionRegistry().register(new SimpleRevision("opsucht", new SemanticVersion("1.1.7"), "2023-11-21"));
    }

    @Override
    protected void enable() {
        referenceStorage = this.referenceStorageAccessor();

        this.registerSettingCategory();
        this.registerListener(new ChatReceiveListener(this));
        this.registerListener(new PlayerInfoListener(this));
        this.labyAPI().interactionMenuRegistry().register(new ClanInviteBulletPoint(this));
        this.labyAPI().interactionMenuRegistry().register(new FriendRequestBulletPoint(this));
        this.labyAPI().interactionMenuRegistry().register(new PayBulletPoint(this));
        this.labyAPI().serverController().registerServer(this.server = new OPSuchtServer(this));
    }

    public static RichPresenceManager richPresenceManager() {
        return referenceStorage.richPresenceManager();
    }

    public static void updateRPC() { // TODO: Fix this
//        if(rpcManager != null) {
//            rpcManager.updateCustomRPC(false);
//        }
    }

    public OPSuchtServer server() {
        return this.server;
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }
}
