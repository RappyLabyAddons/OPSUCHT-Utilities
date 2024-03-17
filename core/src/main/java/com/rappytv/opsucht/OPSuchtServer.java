package com.rappytv.opsucht;

import net.labymod.api.Laby;
import net.labymod.api.client.network.server.AbstractServer;
import net.labymod.api.event.Phase;

public class OPSuchtServer extends AbstractServer {

    private final OPSuchtAddon addon;
    private boolean connected;

    public OPSuchtServer(OPSuchtAddon addon) {
        super("opsucht");
        this.addon = addon;
        connected = false;
    }

    @Override
    public void loginOrSwitch(LoginPhase phase) {
        if(phase == LoginPhase.LOGIN) connected = true;
        else if(phase == LoginPhase.SWITCH && addon.configuration().autoFly().get()) {
            Laby.labyAPI().minecraft().executeNextTick(() ->
                Laby.references().chatExecutor().chat("/fly", false)
            );
        }

        Laby.labyAPI().minecraft().executeNextTick(() ->
            addon.rpcManager.updateCustomRPC(phase == LoginPhase.LOGIN)
        );
    }

    @Override
    public void disconnect(Phase phase) {
        if(phase == Phase.POST) connected = false;
        addon.rpcManager.removeCustomRPC();
    }

    public boolean isConnected() {
        return connected;
    }
}
