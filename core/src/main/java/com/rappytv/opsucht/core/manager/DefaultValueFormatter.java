package com.rappytv.opsucht.core.manager;

import com.rappytv.opsucht.api.ValueFormatter;
import com.rappytv.opsucht.api.market.MarketManager.DisplayMode;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.models.Implements;
import org.jetbrains.annotations.NotNull;
import javax.inject.Singleton;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Singleton
@Implements(ValueFormatter.class)
public class DefaultValueFormatter implements ValueFormatter {

    private final DecimalFormat format = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMANY));

    @Override
    @NotNull
    public Component formatSingleValueComponent(String format, float value, TextColor color) {
        return Component.text(format.replace("{price}", this.formatFloat(value)), color);
    }

    @Override
    @NotNull
    public Component formatValueComponent(
        float buyValue,
        float sellValue,
        String format,
        TextColor buyPriceColor,
        TextColor sellPriceColor,
        DisplayMode mode
    ) {
        Component buyComponent = this.formatSingleValueComponent(format, buyValue, buyPriceColor);
        Component sellComponent = this.formatSingleValueComponent(format, sellValue, sellPriceColor);

        return switch (mode) {
            case BOTH -> Component.empty()
                .append(buyComponent)
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(sellComponent);
            case ONLY_BUY -> buyComponent;
            case ONLY_SELL -> sellComponent;
        };
    }

    @Override
    public @NotNull String formatFloat(float number) {
        return this.format.format(number);
    }
}
