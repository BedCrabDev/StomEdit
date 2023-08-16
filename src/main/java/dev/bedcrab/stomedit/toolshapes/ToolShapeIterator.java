package dev.bedcrab.stomedit.toolshapes;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Supplier;

public interface ToolShapeIterator extends Iterable<Pos>, Iterator<Pos> {
    int count();
    @NotNull
    @Override
    default Iterator<Pos> iterator() {
        return this;
    }
    default void fill(Block.Setter setter, Supplier<Block> supplier) {
        for (Pos pos : this) setter.setBlock(pos, supplier.get());
    }
}
