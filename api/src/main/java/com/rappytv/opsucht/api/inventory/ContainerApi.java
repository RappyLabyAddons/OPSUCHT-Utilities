package com.rappytv.opsucht.api.inventory;

import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface ContainerApi {

    void clickSlot(int slot);

    void closeContainer();
}
