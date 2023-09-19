package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;
import net.labymod.api.event.client.network.server.ServerSwitchEvent;
import net.labymod.api.event.client.network.server.SubServerSwitchEvent;

public class ServerNavigationListener {

    private final OPSuchtAddon addon;

    public ServerNavigationListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        if(isOpSucht(event.serverData()))
            OPSuchtAddon.setConnected(true);
        Laby.labyAPI().minecraft().executeNextTick(() -> addon.rpcManager.updateCustomRPC(true));
    }

    @Subscribe
    public void onServerSwitch(ServerSwitchEvent event) {
        OPSuchtAddon.setConnected(isOpSucht(event.newServerData()));
        Laby.labyAPI().minecraft().executeNextTick(() -> addon.rpcManager.updateCustomRPC(true));
    }

    @Subscribe
    public void onSubServerSwitch(SubServerSwitchEvent event) {
        if(OPSuchtAddon.isConnected()) {
            Laby.labyAPI().minecraft().executeNextTick(() -> {
                addon.rpcManager.updateCustomRPC(false);
                if(addon.configuration().autoFly().get())
                    Laby.labyAPI().minecraft().executeNextTick(() -> Laby.references().chatExecutor().chat("/fly", false));
            });
        }
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        OPSuchtAddon.setConnected(false);
        addon.rpcManager.removeCustomRPC();
    }

    private boolean isOpSucht(ServerData data) {
        if(data == null) return false;
        return data.actualAddress().getAddress().getAddress().getHostAddress().equals(OPSuchtAddon.ip[0]) || data.actualAddress().getAddress().getAddress().getHostAddress().equals(OPSuchtAddon.ip[1]);
    }
}
