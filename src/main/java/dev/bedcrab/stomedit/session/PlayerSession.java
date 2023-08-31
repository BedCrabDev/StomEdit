package dev.bedcrab.stomedit.session;

import dev.bedcrab.stomedit.StomEdit;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface PlayerSession {
    static @NotNull PlayerSession of(Player player) {
        return StomEdit.sessionGetter.apply(player);
    }

    @NotNull NBTCompound nbt();
    <T extends Record & SessionData> @NotNull T read(@NotNull Class<T> record, Function<String, NBT> emptyFunc) throws NullPointerException;
    <T> @NotNull T getTag(@NotNull Class<? extends SessionData> record, @NotNull Tag<T> tag) throws NullPointerException;
    <T extends Record & SessionData> void write(@NotNull T value);
    <T extends Record & SessionData> void write(@NotNull CompletableFuture<T> value);
    <T> void setTag(@NotNull Class<? extends SessionData> record, @NotNull Tag<T> tag, T value);
}
