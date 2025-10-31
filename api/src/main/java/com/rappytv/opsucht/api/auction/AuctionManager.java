package com.rappytv.opsucht.api.auction;

import net.labymod.api.reference.annotation.Referenceable;
import java.util.List;

@Referenceable
public interface AuctionManager {

    List<Auction> getActiveAuctions();

    void cacheAuctions();
}
