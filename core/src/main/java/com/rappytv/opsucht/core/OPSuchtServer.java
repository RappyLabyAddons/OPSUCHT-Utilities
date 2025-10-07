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
        if(phase == LoginPhase.LOGIN) this.connected = true;
        else if(phase == LoginPhase.SWITCH && this.addon.configuration().autoFly().get()) {
            Laby.labyAPI().minecraft().executeNextTick(() ->
                Laby.references().chatExecutor().chat("/fly", false)
            );
        }

        Laby.labyAPI().minecraft().executeNextTick(() ->
            OPSuchtAddon.richPresenceManager().updateCustomRPC(
                this.addon.configuration().richPresenceConfig(),
                phase == LoginPhase.LOGIN
            )
        );
    }

    @Override
    public void disconnect(Phase phase) {
        if(phase == Phase.POST) this.connected = false;
        OPSuchtAddon.richPresenceManager().removeCustomRPC();
    }

    public boolean isConnected() {
        return this.connected;
    }
}
