package com.rappytv.opsucht.core;

import com.rappytv.opsucht.api.OPSuchtRank;
import com.rappytv.opsucht.api.event.SkullReminderEvent;
import com.rappytv.opsucht.core.config.subconfig.ReminderConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.network.server.AbstractServer;
import net.labymod.api.event.Phase;
import net.labymod.api.util.concurrent.task.Task;

import java.util.concurrent.TimeUnit;

public class OPSuchtServer extends AbstractServer {

    private final OPSuchtAddon addon;
    private final Task autoFlyTask;
    private final Task compareSkullDateTask;
    private boolean connected;

    public OPSuchtServer(OPSuchtAddon addon) {
        super("opsucht");
        this.addon = addon;
        this.autoFlyTask = Task.builder(() ->
            Laby.references().chatExecutor().chat("/fly", false)
        ).delay(1, TimeUnit.SECONDS).build();
        this.compareSkullDateTask = Task.builder(() -> {
            if(!addon.configuration().enabled().get() || !addon.server().isConnected()) {
                return;
            }
            ReminderConfig config = addon.configuration().reminderConfig();
            if(!config.skullReminders().get() || config.lastSkullClaim().get() == -1L) {
                return;
            }
            OPSuchtRank rank = addon.configuration().rank().get();
            if(!rank.hasSkullPermission()) {
                return;
            }
            long nextSkull = config.lastSkullClaim().get() + rank.getSkullCooldownDays() * 86400000L;

            if(nextSkull > System.currentTimeMillis()) {
                return;
            }
            Laby.fireEvent(new SkullReminderEvent());
        }).repeat(5, TimeUnit.MINUTES).build();
        this.compareSkullDateTask.execute();
        this.connected = false;
    }

    @Override
    public void loginOrSwitch(LoginPhase phase) {
        if(phase == LoginPhase.LOGIN) {
            this.connected = true;
            this.compareSkullDateTask.run();
        }
        else if(phase == LoginPhase.SWITCH && this.addon.configuration().autoFly().get()) {
            this.autoFlyTask.execute();
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
