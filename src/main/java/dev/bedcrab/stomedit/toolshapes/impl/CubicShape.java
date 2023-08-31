package dev.bedcrab.stomedit.toolshapes.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.ToolShapeData;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import dev.bedcrab.stomedit.toolshapes.ToolShapeMode;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CubicShape implements ToolShapeMode {
    @Override
    public Collection<Argument<?>> shapeVariables() {
        return List.of();
    }

    @Override
    public Collection<Tag<?>> getRequiredParams() {
        return List.of(
            Tag.Structure("from", SEUtils.BlockPos.class),
            Tag.Structure("to", SEUtils.BlockPos.class)
        );
    }

    @Override
    public ToolShapeIterator iter(TagReadable params) {
        return new ShapeIterator(
            params.getTag(Tag.Structure("from", SEUtils.BlockPos.class)),
            params.getTag(Tag.Structure("to", SEUtils.BlockPos.class))
        );
    }

    @Override
    public Component getHintMsg() {
        return SEColorUtil.HINT.format("Set %% and %% to specify a selection!", "FROM (lclick)", "TO (rclick)");
    }

    @Override
    public void onRightClick(@NotNull Player player, SEUtils.BlockPos bPos, @NotNull PlayerSession session) {
        ToolShapeData data = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        MutableNBTCompound nbt = data.params().toMutableCompound();
        Tag.Structure("to", SEUtils.BlockPos.class).write(nbt, bPos);
        session.write(data.withParams(nbt));
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %%", "TO", bPos.toString()));
    }

    @Override
    public void onLeftClick(@NotNull Player player, SEUtils.BlockPos bPos, @NotNull PlayerSession session) {
        ToolShapeData data = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        MutableNBTCompound nbt = data.params().toMutableCompound();
        Tag.Structure("from", SEUtils.BlockPos.class).write(nbt, bPos);
        session.write(data.withParams(nbt));
        SEUtils.message(player, SEColorUtil.GENERIC.format("%% target set to %%", "FROM", bPos.toString()));
    }

    public static class ShapeIterator implements ToolShapeIterator {
        private final int minX, maxX, minY, maxY, minZ, maxZ;
        public final SEUtils.BlockPos minPos, maxPos;
        private SEUtils.BlockPos lastPos;
        private boolean last = false;
        private int count = 0;
        public ShapeIterator(SEUtils.BlockPos from, SEUtils.BlockPos to) {
            minX = Math.min(to.x(), from.x()); maxX = Math.max(to.x(), from.x());
            minY = Math.min(to.y(), from.y()); maxY = Math.max(to.y(), from.y());
            minZ = Math.min(to.z(), from.z()); maxZ = Math.max(to.z(), from.z());
            minPos = new SEUtils.BlockPos(minX, minY, minZ);
            maxPos = new SEUtils.BlockPos(maxX, maxY, maxZ);
            lastPos = minPos;
        }

        @Override
        public boolean hasNext() {
            if (last) return false;
            last = lastPos.pos().sameBlock(maxX, maxY, maxZ);
            return true;
        }

        @Override
        public @Nullable SEUtils.BlockPos next() {
            count++;

            SEUtils.BlockPos oldPos = lastPos;
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
