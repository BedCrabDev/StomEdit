package dev.bedcrab.stomedit.session.impl;

import dev.bedcrab.stomedit.session.SessionData;
import dev.bedcrab.stomedit.toolshapes.ToolShape;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagHandler;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

public record ToolShapeSessionData(int num, NBTCompound params) implements SessionData {
    public final static ToolShapeSessionData DEFAULT = new ToolShapeSessionData(ToolShape.Mode.CUBIC.ordinal(), NBTCompound.EMPTY);
    public ToolShapeSessionData withParams(MutableNBTCompound newParams) {
        return new ToolShapeSessionData(num, newParams.toCompound());
    }
    public ToolShapeSessionData withMode(ToolShape.Mode newMode) {
        return new ToolShapeSessionData(newMode.ordinal(), params);
    }
    public @NotNull ToolShape.Mode parseMode() {
        return ToolShape.Mode.values()[num];
    }
    public void validateParams() throws NullPointerException {
        ToolShape.Mode mode = parseMode();
        for (Tag<?> tag : mode.getRequiredParams()) if (tag.read(params) == null) throw new NullPointerException("Parameter `"+tag.getKey()+"` not found!");
    }
    public @NotNull ToolShapeIterator iter() throws NullPointerException {
        ToolShape.Mode mode = parseMode();
        validateParams();
        return mode.iter(TagHandler.fromCompound(params));
    }
}
