package com.rappytv.opsucht.core;

import net.labymod.api.Laby;
import net.labymod.api.client.network.server.AbstractServer;
import net.labymod.api.event.Phase;

public class OPSuchtServer extends AbstractServer {

    private final OPSuchtAddon addon;
    private boolean connected;

    public OPSuchtServer(OPSuchtAddon addon) {
        super("opsucht");
        this.addon = addon;
        this.connected = false;
    }

    @Override
    public void loginOrSwitch(LoginPhase phase) {
        if(phase == LoginPhase.LOGIN) {
            this.connected = true;
            this.addon.taskManager().compareSkullDateTask().run();
        }
        else if(phase == LoginPhase.SWITCH) {
            if(this.addon.configuration().autoFly().get()) {
                this.addon.taskManager().autoFlyTask().execute();
            }
            this.addon.taskManager().compareDailyRewardDateTask().run();
        }

        Laby.labyAPI().minecraft().executeNextTick(() ->
            OPSuchtAddon.references().richPresenceManager().updateCustomRPC(
                this.addon.configuration().richPresenceConfig(),
                phase == LoginPhase.LOGIN
            )
        );
    }

    @Override
    public void disconnect(Phase phase) {
        if(phase == Phase.POST) this.connected = false;
        OPSuchtAddon.references().richPresenceManager().removeCustomRPC();
    }

    public boolean isConnected() {
        return this.connected;
    }
}
