package com.rappytv.opsucht;

import com.rappytv.opsucht.config.OPSuchtConfig;
import com.rappytv.opsucht.context.ClanInviteContext;
import com.rappytv.opsucht.context.FriendRequestContext;
import com.rappytv.opsucht.context.PayContext;
import com.rappytv.opsucht.listeners.ChatReceiveListener;
import com.rappytv.opsucht.listeners.PlayerInfo;
import com.rappytv.opsucht.listeners.ServerNavigationListener;
import com.rappytv.opsucht.managers.DiscordRPCManager;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    public static final String[] ip = {"162.19.233.3", "141.95.85.60"};
    public DiscordRPCManager rpcManager;
    private static OPSuchtAddon instance;

    @Override
    protected void enable() {

        rpcManager = new DiscordRPCManager(this);
        instance = this;

        registerSettingCategory();
        registerListener(new ChatReceiveListener(this));
        registerListener(new PlayerInfo(this));
        registerListener(new ServerNavigationListener(this));
        labyAPI().interactionMenuRegistry().register(new ClanInviteContext(this));
        labyAPI().interactionMenuRegistry().register(new FriendRequestContext(this));
        labyAPI().interactionMenuRegistry().register(new PayContext(this));
    }

    public static void updateRPC() {
        if(instance != null)
            instance.rpcManager.updateCustomRPC();
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }
}
