package com.rappytv.opsucht.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rappytv.opsucht.api.adapters.DateAdapter;
import com.rappytv.opsucht.api.auction.Auction;
import com.rappytv.opsucht.api.auction.AuctionManager;
import com.rappytv.opsucht.api.event.AuctionDataRefreshEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Singleton;
import net.labymod.api.Laby;
import net.labymod.api.models.Implements;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;

@Singleton
@Implements(AuctionManager.class)
public class DefaultAuctionManager implements AuctionManager {

    private static final String ENDPOINT = "https://api.opsucht.net/auctions/active";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Date.class, new DateAdapter())
        .create();

    private final List<Auction> auctions = new ArrayList<>();

    @Override
    public List<Auction> getActiveAuctions() {
        return Collections.unmodifiableList(this.auctions);
    }

    @Override
    public void cacheAuctions() {
        this.auctions.clear();
        Response<Auction[]> response = Request.ofGson(Auction[].class, gson)
            .url(ENDPOINT)
            .addHeader("User-Agent", OPSuchtAddon.getUserAgent())
            .executeSync();

        if (response.hasException()) {
            return;
        }

        if(response.getStatusCode() != 200) {
            return;
        }

        this.auctions.addAll(Arrays.asList(response.get()));
        Laby.fireEvent(new AuctionDataRefreshEvent());
    }
}
