package com.rappytv.opsucht.api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.rappytv.opsucht.api.market.MarketItem;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.component.serializer.gson.GsonComponentSerializer;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MerchantComponentAdapter extends TypeAdapter<Component> {

    private static final Pattern componentPattern = Pattern.compile("item_name='((?:\\\\'|[^'])*)'");

    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public Component read(JsonReader in) throws IOException {
        String item = in.nextString();
        Matcher matcher = componentPattern.matcher(item);
        if(!matcher.find()) {
            if(item.equals("opshards")) {
                return Component.text("OP Shards", NamedTextColor.AQUA)
                    .decorate(TextDecoration.BOLD);
            }

            return Component.text(MarketItem.formatName(item));
        }

        return GsonComponentSerializer.gson().deserialize(
            matcher.group(1).replace("\\\"", "\"")
        );
    }
}
