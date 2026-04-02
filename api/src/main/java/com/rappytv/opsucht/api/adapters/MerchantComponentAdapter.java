package com.rappytv.opsucht.api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.rappytv.opsucht.api.NbtComponentSerializer;
import com.rappytv.opsucht.api.market.MarketItem;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

public class MerchantComponentAdapter extends TypeAdapter<Component> {

    private static final Pattern COMPONENT_PATTERN = Pattern.compile(
        "custom_name=(\\{(?:[^{}]|\\{[^{}]*})*})"
    );
    private static final Pattern TEXT_PATTERN = Pattern.compile("text:\\s*\"([^\"]*)\"");

    private final NbtComponentSerializer serializer;

    public MerchantComponentAdapter(NbtComponentSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public Component read(JsonReader in) throws IOException {
        String item = in.nextString();

        if (item.equals("opshards")) {
            return Component.text("OPShards", NamedTextColor.AQUA);
        }

        Matcher matcher = COMPONENT_PATTERN.matcher(item);
        if (matcher.find()) {
            if (this.serializer != null) {
                try {
                    return this.serializer.deserializeComponent(
                        matcher.group(1).replace("custom_name=", "")
                    );
                } catch (RuntimeException ignored) {}
            }
            Matcher textMatcher = TEXT_PATTERN.matcher(item);
            if (textMatcher.find()) {
                return Component.text(textMatcher.group(1));
            }
        }

        return Component.text(MarketItem.formatName(item));
    }
}
