package com.rappytv.opsucht.core.manager;

import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchErrorEvent;
import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchErrorEvent.Reason;
import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchTeleportationEvent;
import com.rappytv.opsucht.api.plotswitch.PlotSwitchDirection;
import com.rappytv.opsucht.api.plotswitch.PlotSwitchManager;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.labymod.api.Laby;
import net.labymod.api.models.Implements;
import net.labymod.api.util.concurrent.task.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.inject.Singleton;

@Singleton
@Implements(PlotSwitchManager.class)
public class DefaultPlotSwitchManager implements PlotSwitchManager {

    private static final String PLOT_VISIT_COMMAND = "/plot visit %s %s";
    private final Task teleportationTimeoutTask = Task.builder(() -> {
        if(!this.isAwaitingTeleportation()) return;
        this.stopAwaitingTeleportation(false);
        Laby.fireEvent(new PlotSwitchErrorEvent(Reason.TIMED_OUT));
    }).delay(10, TimeUnit.SECONDS).build();

    private String currentPlayer = null;
    private PlotSwitchDirection direction = null;
    private int currentPlot = -1;
    private int teleportationAmount = -1;

    @Override
    public void setData(@NotNull String player, int plot) {
        Objects.requireNonNull(player);
        this.resetData();
        this.currentPlayer = player;
        this.currentPlot = plot;
    }

    @Override
    public @Nullable String getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public void teleportPrevious(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if(this.currentPlot - amount < 1) {
            throw new IllegalArgumentException("Cannot teleport to a negative plot");
        }
        this.awaitTeleportation(PlotSwitchDirection.PREVIOUS, amount);
        Laby.references().chatExecutor().chat(String.format(
            PLOT_VISIT_COMMAND,
            this.currentPlayer,
            Math.max(this.currentPlot - amount, 1)
        ), false);
    }

    @Override
    public void teleportNext(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.awaitTeleportation(PlotSwitchDirection.NEXT, amount);
        Laby.references().chatExecutor().chat(String.format(
            PLOT_VISIT_COMMAND,
            this.currentPlayer,
            this.currentPlot + amount
        ), false);
    }

    @Override
    public void awaitTeleportation(@NotNull PlotSwitchDirection direction, int amount) throws IllegalStateException {
        if(this.isAwaitingTeleportation()) {
            throw new IllegalStateException("Already awaiting teleportation");
        }
        Objects.requireNonNull(direction);
        this.direction = direction;
        this.teleportationAmount = amount;
        this.teleportationTimeoutTask.execute();
    }

    @Override
    public void stopAwaitingTeleportation(boolean teleported) {
        if(!this.isAwaitingTeleportation()) {
            throw new IllegalStateException("Not awaiting teleportation");
        }
        if(teleported) {
            int previousPlot = this.currentPlot;
            if(this.direction == PlotSwitchDirection.NEXT) {
                this.currentPlot += this.teleportationAmount;
            } else if(this.currentPlot > 1) {
                this.currentPlot -= this.teleportationAmount;
            }
            Laby.fireEvent(new PlotSwitchTeleportationEvent(
                this.currentPlayer,
                previousPlot,
                this.currentPlot
            ));
        }
        this.direction = null;
        this.teleportationAmount = -1;
    }

    @Override
    public boolean isAwaitingTeleportation() {
        return this.direction != null && this.teleportationAmount > 0;
    }

    @Override
    public int getCurrentPlot() {
        return this.currentPlot;
    }

    @Override
    public void resetData() {
        this.currentPlayer = null;
        this.direction = null;
        this.currentPlot = -1;
        this.teleportationAmount = -1;
    }
}
