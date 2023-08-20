package dev.bedcrab.stomedit.session;

import dev.bedcrab.stomedit.StomEdit;
import dev.bedcrab.stomedit.StomEditException;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSessionImpl implements PlayerSession {
    private final Player player;
    public PlayerSessionImpl(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull NBTCompound nbt() {
        NBTCompound nbt = (NBTCompound) player.getTag(StomEdit.NBT_DATA_HOME);
        if (nbt == null) {
            nbt = NBTCompound.EMPTY;
            player.setTag(StomEdit.NBT_DATA_HOME, nbt);
        }
        return nbt;
    }

    @Override
    public <T extends Record & SessionData> @NotNull T read(Class<T> record, T emptyDefault) throws NullPointerException {
        NBTCompound nbt = nbt();
        String name = record.getSimpleName().toLowerCase();
        if (!nbt.containsKey(name)) throw new StomEditException(player, "Session data `"+name+"` couldn't be found!", new NullPointerException());
        T result = Tag.Structure(name, record).read(nbt());
        if (result == null) return emptyDefault;
        return result;
    }

    @Override
    public <T extends Record & SessionData> void write(T value) {
        MutableNBTCompound newNBT = nbt().toMutableCompound();
        //noinspection unchecked
        Tag.Structure(value.getClass().getSimpleName().toLowerCase(), (Class<T>) value.getClass()).write(newNBT, value);
        player.setTag(StomEdit.NBT_DATA_HOME, newNBT.toCompound());
    }
}
