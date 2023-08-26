package dev.bedcrab.stomedit.session;

import dev.bedcrab.stomedit.StomEdit;
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
        if (!nbt.containsKey(name)) return emptyDefault;
        T result = Tag.Structure(name, record).read(nbt());
        if (result == null) return emptyDefault;
        return result;
    }

    @Override
    public <T extends Record & SessionData> void write(T value) {
        player.sendMessage(value.toString());
        //noinspection unchecked
        Class<T> record = (Class<T>) value.getClass();
        String name = record.getSimpleName().toLowerCase();
        MutableNBTCompound newNBT = NBTCompound.EMPTY.toMutableCompound();
        MutableNBTCompound playerNBT = nbt().toMutableCompound();
        Tag.View(record).write(newNBT, value);
        playerNBT.set(name, newNBT.toCompound());
        player.setTag(StomEdit.NBT_DATA_HOME, playerNBT.toCompound());
    }
}
