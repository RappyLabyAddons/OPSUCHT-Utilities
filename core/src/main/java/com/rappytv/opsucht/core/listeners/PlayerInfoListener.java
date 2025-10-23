package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoAddEvent;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;
import net.labymod.api.util.Debounce;

public class PlayerInfoListener {

    private final OPSuchtAddon addon;

    public PlayerInfoListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onPlayerInfoAdd(PlayerInfoAddEvent event) {
        if(!this.addon.server().isConnected()) return;
        this.updateCustomRPC();
    }

    @Subscribe
    public void onPlayerInfoRemove(PlayerInfoRemoveEvent event) {
        if(!this.addon.server().isConnected()) return;
        this.updateCustomRPC();
    }

    private void updateCustomRPC() {
        Debounce.of(
            "refresh-opsucht-discord-rpc",
            2000,
            () -> OPSuchtAddon.references()
                    .richPresenceManager()
                    .updateCustomRPC(this.addon.configuration().richPresenceConfig())
        );
    }

}
