package com.rappytv.opsucht.core.manager;

import com.rappytv.opsucht.api.PlotSwitchManager;
import net.labymod.api.models.Implements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

@Implements(PlotSwitchManager.class)
public class DefaultPlotSwitchManager implements PlotSwitchManager {

    private String currentPlayer = null;
    private int currentPlot = -1;

    @Override
    public void init(@NotNull String player, int plot) {
        Objects.requireNonNull(player);
        this.currentPlayer = player;
        this.currentPlot = plot;
    }

    @Override
    public @Nullable String getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public int nextPlot() {
        return this.currentPlot++;
    }

    @Override
    public int previousPlot() {
        if(this.currentPlot == 1) {
            return this.currentPlot;
        }
        return this.currentPlot--;
    }

    @Override
    public int getCurrentPlot() {
        return this.currentPlot;
    }

    @Override
    public void reset() {
        this.currentPlayer = null;
        this.currentPlot = -1;
    }
}
