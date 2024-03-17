package com.rappytv.opsucht;

import com.rappytv.opsucht.config.OPSuchtConfig;
import com.rappytv.opsucht.context.ClanInviteContext;
import com.rappytv.opsucht.context.FriendRequestContext;
import com.rappytv.opsucht.context.PayContext;
import com.rappytv.opsucht.listeners.ChatReceiveListener;
import com.rappytv.opsucht.listeners.PlayerInfo;
import com.rappytv.opsucht.managers.DiscordRPCManager;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.revision.SimpleRevision;
import net.labymod.api.util.version.SemanticVersion;

@AddonMain
public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    public DiscordRPCManager rpcManager;
    private static OPSuchtAddon instance;
    private OPSuchtServer server;

    @Override
    protected void preConfigurationLoad() {
        Laby.references().revisionRegistry().register(new SimpleRevision("opsucht", new SemanticVersion("1.4.0"), "2023-11-21"));
    }

    @Override
    protected void enable() {

        rpcManager = new DiscordRPCManager(this);
        instance = this;

        registerSettingCategory();
        registerListener(new ChatReceiveListener(this));
        registerListener(new PlayerInfo(this));
        labyAPI().interactionMenuRegistry().register(new ClanInviteContext(this));
        labyAPI().interactionMenuRegistry().register(new FriendRequestContext(this));
        labyAPI().interactionMenuRegistry().register(new PayContext(this));
        labyAPI().serverController().registerServer(server = new OPSuchtServer(this));
    }

    public static void updateRPC() {
        if(instance != null)
            instance.rpcManager.updateCustomRPC(false);
    }

    public OPSuchtServer server() {
        return server;
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }
}
