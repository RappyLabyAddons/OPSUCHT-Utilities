package com.rappytv.opsucht.api;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Referenceable
public interface PlotSwitchManager {

    void init(@NotNull String player, int plot);

    @Nullable String getCurrentPlayer();

    int nextPlot();

    int previousPlot();

    int getCurrentPlot();

    void reset();
}
