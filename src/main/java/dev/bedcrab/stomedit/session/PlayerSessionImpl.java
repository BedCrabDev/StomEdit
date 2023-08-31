package dev.bedcrab.stomedit.session;

import dev.bedcrab.stomedit.StomEdit;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.lang.reflect.RecordComponent;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSessionImpl implements PlayerSession {
    private final Player player;
    public PlayerSessionImpl(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull NBTCompound nbt() {
        NBTCompound nbt = (NBTCompound) player.getTag(StomEdit.NBT_DATA_HOME);
        if (nbt == null) return NBTCompound.EMPTY;
        return nbt;
    }

    @Override
    public <T extends Record & SessionData> @NotNull T read(@NotNull Class<T> record, Function<String, NBT> emptyFunc) throws RuntimeException {
        assert record.isRecord();
        NBTCompound nbt = nbt();
        String name = record.getSimpleName().toLowerCase();
        NBTCompound compound;
        if ((compound = nbt.getCompound(name)) == null) compound = NBTCompound.EMPTY;
        MutableNBTCompound updatedCompound = compound.toMutableCompound();
        for (RecordComponent c : record.getRecordComponents()) if (compound.get(c.getName()) == null) {
            updatedCompound.set(c.getName(), emptyFunc.apply(c.getName()));
        }
        compound = updatedCompound.toCompound();
        T result = Tag.View(record).read(compound);
        if (result == null) throw new NullPointerException("Couldn't read session data `"+name+"`!");
        return result;
    }

    @Override
    public <T> @NotNull T getTag(@NotNull Class<? extends SessionData> record, @NotNull Tag<T> tag) throws NullPointerException {
        assert record.isRecord();
        NBTCompound nbt = nbt();
        String name = record.getSimpleName().toLowerCase();
        NBTCompound compound = nbt.getCompound(name);
        return Objects.requireNonNull(tag.read(Objects.requireNonNull(compound)));
    }

    @Override
    public <T extends Record & SessionData> void write(@NotNull T value) {
        //noinspection unchecked
        Class<T> record = (Class<T>) value.getClass();
        assert record.isRecord();
        String name = record.getSimpleName().toLowerCase();
        MutableNBTCompound newNBT = NBTCompound.EMPTY.toMutableCompound();
        MutableNBTCompound rootNBT = nbt().toMutableCompound();
        Tag.View(record).write(newNBT, value);
        rootNBT.set(name, newNBT.toCompound());
        player.setTag(StomEdit.NBT_DATA_HOME, rootNBT.toCompound());
    }

    @Override
    public <T extends Record & SessionData> void write(@NotNull CompletableFuture<T> value) {
        value.thenAcceptAsync(this::write);
    }

    @Override
    public <T> void setTag(@NotNull Class<? extends SessionData> record, @NotNull Tag<T> tag, T value) {
        assert record.isRecord();
        NBTCompound nbt = nbt();
        String name = record.getSimpleName().toLowerCase();
        MutableNBTCompound newNBT = Objects.requireNonNull(nbt.getCompound(name)).toMutableCompound();
        MutableNBTCompound rootNBT = nbt.toMutableCompound();
        System.out.println("writing " + tag.getKey() + " as " + value);
        System.out.println("before "+newNBT.toSNBT());
        tag.write(newNBT, value);
        System.out.println("newNBT.toSNBT() = " + newNBT.toSNBT());
        rootNBT.set(name, newNBT.toCompound());
        System.out.println("rootNBT.toSNBT() = " + rootNBT.toSNBT());
        player.setTag(StomEdit.NBT_DATA_HOME, rootNBT.toCompound());
    }
}
