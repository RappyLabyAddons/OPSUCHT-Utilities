package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;
import net.labymod.api.event.client.network.server.SubServerSwitchEvent;

public class ServerNavigationListener {

    private final OPSuchtAddon addon;

    public ServerNavigationListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {

    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        if(Util.isConnectedToServer())
            addon.rpcManager.removeCustomRPC();
    }

    @Subscribe
    public void onServerSwitch(SubServerSwitchEvent event) {

    }
}
