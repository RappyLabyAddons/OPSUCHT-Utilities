package com.rappytv.opsucht.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rappytv.opsucht.api.adapters.MerchantComponentAdapter;
import com.rappytv.opsucht.api.event.MerchantDataRefreshEvent;
import com.rappytv.opsucht.api.merchant.MerchantManager;
import com.rappytv.opsucht.api.merchant.MerchantRate;
import com.rappytv.opsucht.core.OPSuchtAddon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.models.Implements;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;

@Singleton
@Implements(MerchantManager.class)
public class DefaultMerchantManager implements MerchantManager {

    private static final String ENDPOINT = "https://api.opsucht.net/merchant/rates";
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Component.class, new MerchantComponentAdapter())
        .create();

    private final List<MerchantRate> merchantRates = new ArrayList<>();

    @Override
    public List<MerchantRate> getRates() {
        return Collections.unmodifiableList(this.merchantRates);
    }

    public void cacheRates() {
        this.merchantRates.clear();
        Response<MerchantRate[]> response = Request.ofGson(MerchantRate[].class, GSON)
            .url(ENDPOINT)
            .addHeader("User-Agent", OPSuchtAddon.getUserAgent())
            .handleErrorStream()
            .executeSync();

        if (response.hasException() || response.getStatusCode() != 200) {
            return;
        }

        this.merchantRates.addAll(Arrays.asList(response.get()));
        Laby.fireEvent(new MerchantDataRefreshEvent());
    }

}
