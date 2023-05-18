package com.rappytv.opsucht.listeners;

import com.rappytv.opsucht.OPSuchtAddon;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;
import net.labymod.api.event.client.network.server.SubServerSwitchEvent;
import java.util.stream.Collectors;

public class ServerNavigationListener {

    private final OPSuchtAddon addon;

    public ServerNavigationListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        addon.rpcManager.updateCustomRPC();
    }

    @Subscribe
    public void onServerSwitch(SubServerSwitchEvent event) {
        addon.rpcManager.updateCustomRPC();
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        addon.rpcManager.removeCustomRPC();
    }
}
