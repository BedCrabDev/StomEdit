package dev.bedcrab.stomedit.session;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.function.Function;

public interface PlayerSession {
    Function<Player, PlayerSession> impl = PlayerSessionImpl::new; // overridable
    static PlayerSession of(Player player) {
        return impl.apply(player);
    }

    @NotNull NBTCompound nbt();
    <T extends Record & SessionData> @NotNull T read(Class<T> record, T emptyDefault) throws NullPointerException;
    <T extends Record & SessionData> void write(T value);
}
