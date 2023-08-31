package dev.bedcrab.stomedit.toolshapes;

import dev.bedcrab.stomedit.SEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface ToolShapeIterator extends Iterable<SEUtils.BlockPos>, Iterator<SEUtils.BlockPos> {
    int count();
    @Override
    default @NotNull Iterator<SEUtils.BlockPos> iterator() {
        return this;
    }
}
