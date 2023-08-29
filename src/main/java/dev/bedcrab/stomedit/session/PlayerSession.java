package dev.bedcrab.stomedit.session;

import dev.bedcrab.stomedit.StomEdit;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.function.Function;

public interface PlayerSession {
    static PlayerSession of(Player player) {
        return StomEdit.sessionGetter.apply(player);
    }

    @NotNull NBTCompound nbt();
    <T extends Record & SessionData> @NotNull T read(@NotNull Class<T> record, Function<String, NBT> emptyFunc) throws NullPointerException;
    <T extends Record & SessionData> void write(T value);
}
