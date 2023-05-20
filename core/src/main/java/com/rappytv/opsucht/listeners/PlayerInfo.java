package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoAddEvent;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;

public class PlayerInfo {

    private final OPSuchtAddon addon;

    public PlayerInfo(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onPlayerInfoAdd(PlayerInfoAddEvent event) {
        addon.rpcManager.updateCustomRPC();
    }

    @Subscribe
    public void onPlayerInfoRemove(PlayerInfoRemoveEvent event) {
        addon.rpcManager.updateCustomRPC();
    }

}
