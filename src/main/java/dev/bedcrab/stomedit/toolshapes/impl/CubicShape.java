package dev.bedcrab.stomedit.toolshapes.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import dev.bedcrab.stomedit.toolshapes.ToolShapeMode;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class CubicShape implements ToolShapeMode {

    @Override
    public Collection<Tag<?>> getRequiredTags() {
        return Arrays.asList(
            Tag.Structure("from", Pos.class),
            Tag.Structure("to", Pos.class)
        );
    }

    @Override
    public ToolShapeIterator iter(TagReadable tags) {
        return new ShapeIterator(tags.getTag(Tag.Structure("from", Pos.class)), tags.getTag(Tag.Structure("to", Pos.class)));
    }

    @Override
    public Component getHelpMessage() {
        return SEColorUtil.FAIL.format("Set %% and %% to specify a selection!", "FROM (lclick)", "TO (rclick)");
    }

    @Override
    public void onRightClick(Player player, Pos pos, MutableNBTCompound nbt) {
        Tag.Structure("to", Pos.class).write(nbt, pos);
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %%", Component.text("TO"), SEUtils.pointToComp(pos)));
    }

    @Override
    public void onLeftClick(Player player, Pos pos, MutableNBTCompound nbt) {
        Tag.Structure("from", Pos.class).write(nbt, pos);
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %%", Component.text("FROM"), SEUtils.pointToComp(pos)));
    }

    public static class ShapeIterator implements ToolShapeIterator {
        private final int minX, maxX, minY, maxY, minZ, maxZ;
        private Pos lastPos;
        private boolean last = false;
        private int count = 0;
        public ShapeIterator(Pos from, Pos to) {
            minX = Math.min(to.blockX(), from.blockX()); maxX = Math.max(to.blockX(), from.blockX());
            minY = Math.min(to.blockY(), from.blockY()); maxY = Math.max(to.blockY(), from.blockY());
            minZ = Math.min(to.blockZ(), from.blockZ()); maxZ = Math.max(to.blockZ(), from.blockZ());
            lastPos = new Pos(minX, minY, minZ);
        }

        @Override
        public boolean hasNext() {
            if (last) return false;
            last = lastPos.sameBlock(maxX, maxY, maxZ);
            return true;
        }

        @Override
        public @Nullable Pos next() {
            count++;

            Pos oldPos = lastPos;
            if (lastPos.x() < maxX) {
                lastPos = lastPos.withX(lastPos.x() + 1);
                return oldPos;
            } else {
                lastPos = lastPos.withX(minX);
            }
            if (lastPos.y() < maxY) {
                lastPos = lastPos.withY(lastPos.y() + 1);
                return oldPos;
            } else {
                lastPos = lastPos.withY(minY);
            }
            if (lastPos.z() < maxZ) {
                lastPos = lastPos.withZ(lastPos.z() + 1);
                return oldPos;
            } else {
                lastPos = lastPos.withZ(minZ);
            }
            return oldPos;
        }

        @Override
        public int count() {
            return count;
        }
    }
}
