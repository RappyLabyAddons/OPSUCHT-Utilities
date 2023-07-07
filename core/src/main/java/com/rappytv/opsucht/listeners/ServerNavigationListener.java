package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.network.server.ServerData;
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
        ServerData serverData = Laby.labyAPI().serverController().getCurrentServerData();
        if (serverData != null) {
            if(serverData.actualAddress().getAddress().getAddress().getHostAddress().equals(OPSuchtAddon.ip[0]) || serverData.actualAddress().getAddress().getAddress().getHostAddress().equals(OPSuchtAddon.ip[1]))
                OPSuchtAddon.setConnected(true);
        }
        addon.rpcManager.updateCustomRPC();
    }

    @Subscribe
    public void onServerSwitch(SubServerSwitchEvent event) {
        addon.rpcManager.updateCustomRPC();
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        OPSuchtAddon.setConnected(false);
        addon.rpcManager.removeCustomRPC();
    }
}
