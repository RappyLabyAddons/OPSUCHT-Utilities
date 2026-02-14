package com.rappytv.opsucht.api.plotswitch;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Referenceable
public interface PlotSwitchManager {

    void setData(@NotNull String player, int plot);

    @Nullable String getCurrentPlayer();

    void teleportPrevious(int amount);

    void teleportNext(int amount);

    void awaitTeleportation(@NotNull PlotSwitchDirection direction, int amount) throws IllegalStateException;

    void stopAwaitingTeleportation(boolean teleported);

    boolean isAwaitingTeleportation();

    int getCurrentPlot();

    void resetData();
}
