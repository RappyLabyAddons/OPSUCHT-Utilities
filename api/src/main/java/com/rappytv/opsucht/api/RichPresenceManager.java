package com.rappytv.opsucht.api;

import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface RichPresenceManager {

    void updateCustomRPC(IRichPresenceConfig config);

    void updateCustomRPC(IRichPresenceConfig config, boolean joining);

    void removeCustomRPC();
}
