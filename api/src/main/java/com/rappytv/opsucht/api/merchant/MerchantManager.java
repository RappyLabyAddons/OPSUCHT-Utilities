package com.rappytv.opsucht.api.merchant;

import net.labymod.api.reference.annotation.Referenceable;
import java.util.List;

@Referenceable
public interface MerchantManager {

    List<MerchantRate> getRates();

    void cacheRates();
}
