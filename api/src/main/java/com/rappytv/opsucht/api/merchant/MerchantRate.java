package com.rappytv.opsucht.api.merchant;

import net.labymod.api.client.component.Component;
import org.jetbrains.annotations.NotNull;

public record MerchantRate(@NotNull Component source, @NotNull Component target, float exchangeRate) {

}
