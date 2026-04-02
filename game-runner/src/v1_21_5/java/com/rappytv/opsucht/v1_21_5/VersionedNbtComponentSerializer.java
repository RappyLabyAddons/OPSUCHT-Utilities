package com.rappytv.opsucht.v1_21_5;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rappytv.opsucht.api.NbtComponentSerializer;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.models.Implements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ComponentSerialization;

import javax.inject.Singleton;

@Singleton
@Implements(NbtComponentSerializer.class)
public class VersionedNbtComponentSerializer implements NbtComponentSerializer {

    @Override
    public Component deserializeComponent(String data) throws RuntimeException {
        try {
            CompoundTag tag = TagParser.parseCompoundFully(data);

            return Laby.references()
                .componentMapper()
                .fromMinecraftComponent(ComponentSerialization.CODEC
                    .decode(NbtOps.INSTANCE, tag)
                    .getOrThrow()
                    .getFirst());
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
