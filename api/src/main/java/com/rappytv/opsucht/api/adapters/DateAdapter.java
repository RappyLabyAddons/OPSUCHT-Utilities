package com.rappytv.opsucht.api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class DateAdapter extends TypeAdapter<Date> {

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(Instant.ofEpochMilli(value.getTime()).toString());
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        String str = in.nextString();
        return Date.from(Instant.parse(str));
    }
}
