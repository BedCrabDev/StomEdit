package dev.bedcrab.stomedit.session.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.session.SessionData;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

public record ToolShapeData(int num, NBTCompound params) implements SessionData {
    public static @NotNull NBT defaultFunc(@NotNull String missing) {
        return switch (missing) {
            case "num" -> NBT.Int(ToolShape.Mode.CUBIC.ordinal());
            case "params" -> SEUtils.emptyCompound();
            default -> throw new IllegalStateException("Unexpected value: " + missing);
        };
    }

    @Contract("_ -> new")
    public @NotNull ToolShapeData withParams(@NotNull MutableNBTCompound newParams) {
        return new ToolShapeData(num, newParams.toCompound());
    }
    @Contract("_ -> new")
    public @NotNull ToolShapeData withMode(ToolShape.@NotNull Mode newMode) {
        return new ToolShapeData(newMode.ordinal(), params);
    }
    public @NotNull ToolShape.Mode parseMode() {
        return ToolShape.Mode.values()[num];
    }
    public void validateParams() throws NullPointerException {
        ToolShape.Mode mode = parseMode();
        for (Tag<?> tag : mode.getRequiredParams()) if (tag.read(params) == null) throw new NullPointerException("Parameter `"+tag.getKey()+"` not found!");
    }
    public @NotNull ToolShapeIterator parseIter() throws NullPointerException {
        ToolShape.Mode mode = parseMode();
        validateParams();
        return mode.iter(TagHandler.fromCompound(params));
    }
}
